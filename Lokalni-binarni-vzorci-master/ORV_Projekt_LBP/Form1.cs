using System;
using System.Windows;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Emgu.CV;
using Emgu.Util;
using Emgu.CV.UI;
using Emgu.CV.Structure;
using System.IO;

namespace ORV_Projekt_LBP
{
    public partial class Form1 : Form
    {
        string FullPath;
        string FileName;
        VideoCapture capture;
        //Image<Emgu.CV.Structure.Bgr, Byte> MainImg;
        EventHandler handler;

        public Form1()
        {
            InitializeComponent();
            FullPath = "";
            FileName = "";
        }

        private void btn_Open_Click(object sender, EventArgs e)
        {
            OpenFileDialog op = new OpenFileDialog();
            op.Filter = "Image Files|*.jpg;*.jpeg;*.png;*.gif;*.bmp;|Video Files|*.mp4;*.m4p;*.m4v;*.mpg;*.amv;*.mpeg;*.avi;*.webm;*.ogg;*.wmv;|All files (*.*)|*.*";
            if (op.ShowDialog() == DialogResult.OK)
            {
                try
                {
                    if (op.OpenFile() != null)
                    {
                        FullPath = op.FileName;
                        FileName = op.SafeFileName;
                        label1.Text = FileName;
                        if (Path.GetExtension(op.FileName) == ".mp4" || Path.GetExtension(op.FileName) == ".mpg" || Path.GetExtension(op.FileName) == ".avi" || Path.GetExtension(op.FileName) == ".m4p" || Path.GetExtension(op.FileName) == ".webm" || Path.GetExtension(op.FileName) == ".ogg" || Path.GetExtension(op.FileName) == ".w4v" || Path.GetExtension(op.FileName) == ".amv") // Not the most sophisticated piece of code, but it gets the job done... For the most part.
                        {
                            capture = new VideoCapture(op.FileName);
                            Application.Idle += handler;
                        }
                        else
                        {
                            iB_original.Image = new Image<Bgr, Byte>(op.FileName);
                            calculate_Basic_LBP(new Image<Gray, Byte>((iB_original.Image.Bitmap)));
                            calculate_uniform_LBP(new Image<Gray, Byte>(iB_original.Image.Bitmap));
                            calculate_distance_LBP(new Image<Gray, Byte>(iB_original.Image.Bitmap), 5);
                        }
                    }
                }
                catch (Exception wtf)
                {
                    MessageBox.Show(wtf.Message);
                }
            }
        }

        private void calculate_Basic_LBP(Image<Gray, Byte> modImg)
        {
            Image<Gray, Byte> final = new Image<Gray, byte>(modImg.Bitmap);
            int[] data = new int[8];
            for (int i = 1; i < modImg.Rows - 1; i++) {
                for (int j = 1; j < modImg.Cols - 1; j++) {
                    var center = modImg.Data[i, j, 0];
                    data[0] = (modImg.Data[i - 1, j - 1, 0] > center) ? 1 : 0;
                    data[1] = (modImg.Data[i - 1, j, 0] > center) ? 1 : 0;
                    data[2] = (modImg.Data[i - 1, j + 1, 0] > center) ? 1 : 0;
                    data[3] = (modImg.Data[i, j + 1, 0] > center) ? 1 : 0;
                    data[4] = (modImg.Data[i + 1, j + 1, 0] > center) ? 1 : 0;
                    data[5] = (modImg.Data[i + 1, j, 0] > center) ? 1 : 0;
                    data[6] = (modImg.Data[i + 1, j - 1, 0] > center) ? 1 : 0;
                    data[7] = (modImg.Data[i, j - 1, 0] > center) ? 1 : 0;
                    int val = Convert.ToInt32(string.Join("", data), 2);
                    final.Data[i, j, 0] = (byte)val;
                }
            }
            //int val = Convert.ToInt32(string.Join("", data), 2);
            iB_post.Image = new Image<Bgr, Byte>(final.Bitmap);
            //return final;
        }

        private void calculate_uniform_LBP(Image<Gray, Byte> modImg)
        {
            Image<Gray, Byte> final = new Image<Gray, byte>(modImg.Bitmap);
            int[] data = new int[8];

            int[] lookup = { // precalculated lookuptable
                0, 1, 2, 3, 4, 58, 5, 6, 7, 58, 58, 58, 8, 58, 9, 10,
                11, 58, 58, 58, 58, 58, 58, 58, 12, 58, 58, 58, 13, 58, 14, 15,
                16, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
                17, 58, 58, 58, 58, 58, 58, 58, 18, 58, 58, 58, 19, 58, 20, 21,
                22, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
                58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
                23, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
                24, 58, 58, 58, 58, 58, 58, 58, 25, 58, 58, 58, 26, 58, 27, 28,
                29, 30, 58, 31, 58, 58, 58, 32, 58, 58, 58, 58, 58, 58, 58, 33,
                58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 34,
                58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
                58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 35,
                36, 37, 58, 38, 58, 58, 58, 39, 58, 58, 58, 58, 58, 58, 58, 40,
                58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 41,
                42, 43, 58, 44, 58, 58, 58, 45, 58, 58, 58, 58, 58, 58, 58, 46,
                47, 48, 58, 49, 58, 58, 58, 50, 51, 52, 58, 53, 54, 55, 56, 57
            };

            for (int i = 1; i < modImg.Rows - 1; i++)
            {
                for (int j = 1; j < modImg.Cols - 1; j++)
                {
                    var center = modImg.Data[i, j, 0];
                    data[0] = (modImg.Data[i - 1, j - 1, 0] > center) ? 1 : 0;
                    data[1] = (modImg.Data[i - 1, j, 0] > center) ? 1 : 0;
                    data[2] = (modImg.Data[i - 1, j + 1, 0] > center) ? 1 : 0;
                    data[3] = (modImg.Data[i, j + 1, 0] > center) ? 1 : 0;
                    data[4] = (modImg.Data[i + 1, j + 1, 0] > center) ? 1 : 0;
                    data[5] = (modImg.Data[i + 1, j, 0] > center) ? 1 : 0;
                    data[6] = (modImg.Data[i + 1, j - 1, 0] > center) ? 1 : 0;
                    data[7] = (modImg.Data[i, j - 1, 0] > center) ? 1 : 0;
                    int val = Convert.ToInt32(string.Join("", data), 2);
                    val = lookup[val];
                    final.Data[i, j, 0] = (byte)val;

                    // If pattern has 2 or less transitions it's labeled as uniform.
                    // If pattern has more then 2 transitions it's labeled as non-uniform and gets value 58 automatically.
                    // Ko gledaš pattern recimo 0010 ima 2 circularno tranzicijo 0 >> 1 al 1 >> 0 in je torej labeled kot uniform
                    // Ko gledaš pattern recimo 0101 ima sam eno tranzicijo ter mu podaš isti label name kot vsi ostali ki nimajo
                    // https://stackoverflow.com/questions/29304276/what-is-the-intuition-behind-uniform-and-non-uniform-patterns-in-lbp
                }
            }
            //int val = Convert.ToInt32(string.Join("", data), 2);
            iB_post_U.Image = new Image<Bgr, Byte>(final.Bitmap);
            //return final;
        }

        private void calculate_distance_LBP(Image<Gray, Byte> modImg, int distance)
        {
            // It's essentially just basic LBP, but the checking of the neighbouring value is based on user input, but you're also calculating the center value instead of using it as a threshold.
            Image<Gray, Byte> final = new Image<Gray, byte>(modImg.Bitmap);
            int[] data = new int[8];
            int buffIndex = distance;
            int[] buff = new int[8];
            for (int i = 1; i < modImg.Rows - 1; i++)
            {
                for (int j = 1; j < modImg.Cols - 1; j++)
                {
                    //var center = modImg.Data[i, j, 0];
                    buff[0] = modImg.Data[i - 1, j, 0];
                    buff[1] = modImg.Data[i - 1, j + 1, 0];
                    buff[2] = modImg.Data[i, j + 1, 0];
                    buff[3] = modImg.Data[i + 1, j + 1, 0];
                    buff[4] = modImg.Data[i + 1, j, 0];
                    buff[5] = modImg.Data[i + 1, j - 1, 0];
                    buff[6] = modImg.Data[i, j - 1, 0];
                    buff[7] = modImg.Data[i - 1, j - 1, 0];
                               // Base Position               // + Distance Position
                    data[0] = (modImg.Data[i - 1, j, 0] >     buff[buffIndex]) ? 1 : 0;
                    buffIndex += distance;
                    if(buffIndex > 7)
                    {
                        buffIndex -= 7;
                        buffIndex -= 1;
                    }
                    data[1] = (modImg.Data[i - 1, j + 1, 0] > buff[buffIndex]) ? 1 : 0;
                    buffIndex += distance;
                    if (buffIndex > 7)
                    {
                        buffIndex -= 7;
                        buffIndex -= 1;
                    }
                    data[2] = (modImg.Data[i, j + 1, 0] >     buff[buffIndex]) ? 1 : 0;
                    buffIndex += distance;
                    if (buffIndex > 7)
                    {
                        buffIndex -= 7;
                        buffIndex -= 1;
                    }
                    data[3] = (modImg.Data[i + 1, j + 1, 0] > buff[buffIndex]) ? 1 : 0;
                    buffIndex += distance;
                    if (buffIndex > 7)
                    {
                        buffIndex -= 7;
                        buffIndex -= 1;
                    }
                    data[4] = (modImg.Data[i + 1, j, 0] >     buff[buffIndex]) ? 1 : 0;
                    buffIndex += distance;
                    if (buffIndex > 7)
                    {
                        buffIndex -= 7;
                        buffIndex -= 1;
                    }
                    data[5] = (modImg.Data[i + 1, j - 1, 0] > buff[buffIndex]) ? 1 : 0;
                    buffIndex += distance;
                    if (buffIndex > 7)
                    {
                        buffIndex -= 7;
                        buffIndex -= 1;
                    }
                    data[6] = (modImg.Data[i, j - 1, 0] >     buff[buffIndex]) ? 1 : 0;
                    buffIndex += distance;
                    if (buffIndex > 7)
                    {
                        buffIndex -= 7;
                        buffIndex -= 1;
                    }
                    data[7] = (modImg.Data[i - 1, j - 1, 0] > buff[buffIndex]) ? 1 : 0;
                    buffIndex = 0;
                    
                    int val = Convert.ToInt32(string.Join("", data), 2);
                    final.Data[i, j, 0] = (byte)val;
                }
            }
            //int val = Convert.ToInt32(string.Join("", data), 2);
            iB_post_d.Image = new Image<Bgr, Byte>(final.Bitmap);
            //return final;
        }

        private void saveToDatabase() {
            // pretvorimo rezultatne podatke ki jih prikazujem v final.Bitmap v nekakšen string zapis
            // let's say this is where we convert data into some kind of string based on the data saved withink final.Bitmap
            // povezava na bazo
            // connection to DB
            // pa shranimo
            // saved.
        }
    }
}
