namespace Detekcija_kljucnih_tock
{
    partial class diff
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.imgbox_final = new Emgu.CV.UI.ImageBox();
            this.txt_foundPairs = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.imgbox_final)).BeginInit();
            this.SuspendLayout();
            // 
            // imgbox_final
            // 
            this.imgbox_final.Location = new System.Drawing.Point(12, 64);
            this.imgbox_final.Name = "imgbox_final";
            this.imgbox_final.Size = new System.Drawing.Size(1628, 751);
            this.imgbox_final.TabIndex = 5;
            this.imgbox_final.TabStop = false;
            // 
            // txt_foundPairs
            // 
            this.txt_foundPairs.AutoSize = true;
            this.txt_foundPairs.Location = new System.Drawing.Point(83, 27);
            this.txt_foundPairs.Name = "txt_foundPairs";
            this.txt_foundPairs.Size = new System.Drawing.Size(260, 17);
            this.txt_foundPairs.TabIndex = 6;
            this.txt_foundPairs.Text = "Number of dots found in second image: ";
            // 
            // diff
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1652, 827);
            this.Controls.Add(this.txt_foundPairs);
            this.Controls.Add(this.imgbox_final);
            this.Name = "diff";
            this.Text = "diff";
            ((System.ComponentModel.ISupportInitialize)(this.imgbox_final)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private Emgu.CV.UI.ImageBox imgbox_final;
        private System.Windows.Forms.Label txt_foundPairs;
    }
}