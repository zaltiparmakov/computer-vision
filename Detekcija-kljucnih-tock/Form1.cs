using Emgu.CV;
using Emgu.CV.Structure;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Emgu.CV.Features2D;
using Emgu.CV.Util;

namespace Detekcija_kljucnih_tock
{
    public partial class Form1 : Form
    {
        public static Image<Bgr, Byte> img_final;
        private Image<Gray, Byte>[] imgs;
        public static int numberFoundPairs;

        public Form1()
        {
            // two images for comparison
            imgs = new Image<Gray, byte>[2];
            InitializeComponent();
        }

        private void detectImgFeatures()
        {
            ORBDetector detector = new ORBDetector(100, 1.2f, 8);

            MKeyPoint[] img0_keyPoints = detector.Detect(imgs[0]);
            VectorOfKeyPoint img0_vector_keypoints = new VectorOfKeyPoint(img0_keyPoints);
            Matrix<Byte> img0_descriptors = new Matrix<Byte>(img0_vector_keypoints.Size, detector.DescriptorSize);

            MKeyPoint[] img1_keyPoints = detector.Detect(imgs[1]);
            VectorOfKeyPoint img1_vector_keypoints = new VectorOfKeyPoint(img1_keyPoints);
            Matrix<Byte> img1_descriptors = new Matrix<Byte>(img1_vector_keypoints.Size, detector.DescriptorSize);

            detector.Compute(imgs[0], img0_vector_keypoints, img0_descriptors);

            // display keypoints in red
            Image<Bgr, Byte> newImg = new Image<Bgr, Byte>(imgs[0].Width, imgs[0].Height);
            Features2DToolbox.DrawKeypoints(imgs[0], img0_vector_keypoints, newImg, new Bgr(255, 0, 255),
                Features2DToolbox.KeypointDrawType.DrawRichKeypoints);
            imgbox_original.Image = newImg;

            Image<Bgr, Byte> newImg2 = new Image<Bgr, Byte>(imgs[1].Width, imgs[1].Height);
            Features2DToolbox.DrawKeypoints(imgs[1], img1_vector_keypoints, newImg2, new Bgr(255, 0, 255),
                Features2DToolbox.KeypointDrawType.DrawRichKeypoints);
            imgbox_second.Image = newImg2;

            // apply BFMatcher to find matches in two images
            BFMatcher bfMatcher = new BFMatcher(DistanceType.Hamming, true);
            VectorOfVectorOfDMatch matches = new VectorOfVectorOfDMatch();
            numberFoundPairs = matches.Size;
            bfMatcher.Add(img0_descriptors);
            bfMatcher.KnnMatch(img1_descriptors, matches, 1, null);

            // display final image as two merged images with keypoints
            Mat matched_image = new Mat();
            Features2DToolbox.DrawMatches(imgs[0], img0_vector_keypoints, imgs[1], img1_vector_keypoints,
                matches, matched_image, new MCvScalar(255, 0, 255), new MCvScalar(0, 255, 0));
            img_final = matched_image.ToImage<Bgr, Byte>();
        }

        private void btn_chooseImages_Click(object sender, EventArgs e)
        {
            OpenFileDialog ofd = new OpenFileDialog();
            ofd.Filter = "Image Files(*.BMP;*.JPG;*.GIF)|*.BMP;*.JPG;*.GIF|All files (*.*)|*.*";
            ofd.Multiselect = true;
            ofd.Title = "Choose two images";

            if(ofd.ShowDialog() == DialogResult.OK)
            {
                // only 2 image files for comparison
                int i = 0;
                foreach(string filename in ofd.FileNames)
                {
                    imgs[i++] = new Image<Gray, byte>(filename);
                }
            }
            detectImgFeatures();
        }

        private void btn_seeDifferences_Click(object sender, EventArgs e)
        {
            diff diffWindow = new diff();
            diffWindow.Show();
        }
    }
}
