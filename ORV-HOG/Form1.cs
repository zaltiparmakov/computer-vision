using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Emgu.CV;
using Emgu.Util;
using Emgu.CV.Structure;
using System.Drawing;

namespace ORV_HOG
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void btn_upload_Click(object sender, EventArgs e)
        {
            Image<Bgr, Byte> img = null;
            Image<Gray, Byte> newMatrix = null;

            OpenFileDialog fd = new OpenFileDialog();
            fd.Title = "Select image";
            fd.Filter = "image files (*.jpg)|*.jpg";

            if (fd.ShowDialog() == DialogResult.OK)
            {
                img = new Image<Bgr, Byte>(fd.FileName);
                originalImage.Image = img;
            }

            /* PREPROCESS THE IMAGE - Resizing and Color Normalization */
            newMatrix = img.Convert<Gray, Byte>();
            // Square-Root Normalization - compresses the input pixel less than Gamma. Increases accuracy of HOG
            //CvInvoke.Sqrt(newMatrix, newMatrix);
            // make the image 64x128 - recommended for HOG description
            int h = img.Height;
            double scaleBy = h / 256.0;
            int width = (int) ((double)newMatrix.Width / scaleBy);
            int height = (int) ((double)newMatrix.Height / scaleBy);
            newMatrix = newMatrix.Resize(width, height, Emgu.CV.CvEnum.Inter.Linear);

            /* Compute the Gradient Vector of every pixel, as well as magnitude and direction */
            // apply Sobel by x and y
            Image<Gray, float> sobel = newMatrix.Sobel(0, 1, 3).Add(newMatrix.Sobel(1, 0, 3)).AbsDiff(new Gray(0.0));
            newMatrix = sobel.Convert<Gray, Byte>();

            /* Compute descriptor values */
            HOGDescriptor hog = new HOGDescriptor();
            Size WinStride = new Size(18, 18);
            Size Padding = new Size(10, 10);
            Point[] locations = null;
            float[] descriptorValues = hog.Compute(newMatrix, WinStride, Padding, locations);
            grayImage.Image = newMatrix;

            Image<Gray, Byte> descMatrix = Visualize(newMatrix, descriptorValues);
            newImage.Image = descMatrix;
        }

        private Image<Gray, Byte> Visualize(Image<Gray, Byte> img, float[] descriptorValues)
        {
            Image<Gray, Byte> matrix = img.Clone();

            int Buckets = 9;
            int CellSize = 8;
            Size WinSize = new Size(img.Width, img.Height);
            Size BlockStride = new Size(8, 8);
            // 180 degrees in Buckets
            float radiansRangeBin = (float)Math.PI / Buckets;

            /* CELLS */
            int cellsX = img.Width / CellSize;
            int cellsY = img.Height / CellSize;
            // 3d arrays for histogram (x, y, and bin)
            float[,,] gradients = new float[cellsY, cellsX, Buckets];
            // 2d array
            int[,] cellUpdateCounter = new int[cellsY, cellsX];
            for (int y = 0; y < cellsY; y++)
            {
                for(int x = 0; x < cellsX; x++)
                {
                    cellUpdateCounter[y, x] = 0;
                    for(int bin = 0; bin < Buckets; bin++)
                    {
                        gradients[y, x, bin] = 0.0f;
                    }
                }
            }

            /* BLOCKS */
            // number of blocks is one less than cells, because we have new block on every cell except the last one
            int blocksX = cellsX - 1;
            int blocksY = cellsY - 1;
            // gradients per cells computation
            int descriptorDataIdx = 0;
            int cell_x, cell_y = 0;
            int CellsPerBlock = 4;
            for (int block_x = 0; block_x < blocksX; block_x++)
            {
                for(int block_y = 0; block_y < blocksY; block_y++)
                {
                    for (int cellNum = 0; cellNum < CellsPerBlock; cellNum++)
                    {
                        cell_x = block_x;
                        cell_y = block_y;
                        switch(cellNum)
                        {
                            case 1:
                                cell_y++;
                                break;
                            case 2:
                                cell_x++;
                                break;
                            case 3:
                                cell_x++;
                                cell_y++;
                                break;
                        }

                        for(int bin = 0; bin < Buckets; bin++)
                        {
                            float gradientStrength = descriptorValues[descriptorDataIdx];
                            descriptorDataIdx++;

                            gradients[cell_y, cell_x, bin] += gradientStrength;
                        }

                        cellUpdateCounter[cell_y, cell_x]++;
                    }
                }
            }

            /* COMPUTE AVERAGE GRADIENT STRENGTHS */
            for (cell_y = 0; cell_y < cellsY; cell_y++)
            {
                for(cell_x = 0; cell_x < cellsX; cell_x++)
                {
                    float cellUpdate_times = cellUpdateCounter[cell_y, cell_x];

                    for(int bin = 0; bin < Buckets; bin++)
                    {
                        gradients[cell_y, cell_x, bin] /= cellUpdate_times;
                    }
                }
            }

            /* DRAW CELLS */
            for (cell_y = 0; cell_y < cellsY; cell_y++) {
                for(cell_x = 0; cell_x < cellsX; cell_x++)
                {
                    int drawX = cell_x * CellSize;
                    int drawY = cell_y * CellSize;
                    int mX = drawX + CellSize / 2;
                    int mY = drawY + CellSize / 2;
                    Rectangle rect = new Rectangle(drawX, drawY, mX, mY);

                    CvInvoke.Rectangle(matrix, rect, new MCvScalar(0, 255, 0), 1);

                    for(int bin = 0; bin < Buckets; bin++)
                    {
                        float currentGradient = gradients[cell_y, cell_x, bin];
                        if(currentGradient == 0)
                        {
                            continue;
                        }

                        float currentRad = (bin * radiansRangeBin) + (radiansRangeBin / 2);
                        float directionVectorX = currentRad;
                        float directionVectorY = currentRad;
                        float maxVectorLength = CellSize / 2;

                        float xVector = directionVectorX * currentGradient * maxVectorLength;
                        float yVector = directionVectorY * currentGradient * maxVectorLength;
                        int x1 = mX - (int)xVector;
                        int x2 = mX + (int)xVector;
                        int y1 = mY - (int)yVector;
                        int y2 = mY + (int)yVector;

                        Point one = new Point(x1, y1);
                        Point two = new Point(x2, y2);
                        CvInvoke.Line(matrix, one, two, new MCvScalar(255, 0, 0), 1);
                    }
                }
            }

            return matrix;
        }
    }
}
