using Emgu.CV.UI;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Detekcija_kljucnih_tock
{
    public partial class diff : Form
    {
        public diff()
        {
            InitializeComponent();

            imgbox_final.Image = Form1.img_final;
            txt_foundPairs.Text = Form1.numberFoundPairs.ToString();
        }
    }
}
