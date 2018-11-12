namespace Detekcija_kljucnih_tock
{
    partial class Form1
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
            this.imgbox_original = new Emgu.CV.UI.ImageBox();
            this.imgbox_second = new Emgu.CV.UI.ImageBox();
            this.btn_chooseImages = new System.Windows.Forms.Button();
            this.btn_seeDifferences = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.imgbox_original)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.imgbox_second)).BeginInit();
            this.SuspendLayout();
            // 
            // imgbox_original
            // 
            this.imgbox_original.Location = new System.Drawing.Point(12, 88);
            this.imgbox_original.Name = "imgbox_original";
            this.imgbox_original.Size = new System.Drawing.Size(917, 938);
            this.imgbox_original.TabIndex = 2;
            this.imgbox_original.TabStop = false;
            // 
            // imgbox_second
            // 
            this.imgbox_second.Location = new System.Drawing.Point(963, 88);
            this.imgbox_second.Name = "imgbox_second";
            this.imgbox_second.Size = new System.Drawing.Size(917, 938);
            this.imgbox_second.TabIndex = 3;
            this.imgbox_second.TabStop = false;
            // 
            // btn_chooseImages
            // 
            this.btn_chooseImages.Location = new System.Drawing.Point(1426, 12);
            this.btn_chooseImages.Name = "btn_chooseImages";
            this.btn_chooseImages.Size = new System.Drawing.Size(172, 49);
            this.btn_chooseImages.TabIndex = 5;
            this.btn_chooseImages.Text = "Open images";
            this.btn_chooseImages.UseVisualStyleBackColor = true;
            this.btn_chooseImages.Click += new System.EventHandler(this.btn_chooseImages_Click);
            // 
            // btn_seeDifferences
            // 
            this.btn_seeDifferences.Location = new System.Drawing.Point(1619, 12);
            this.btn_seeDifferences.Name = "btn_seeDifferences";
            this.btn_seeDifferences.Size = new System.Drawing.Size(172, 49);
            this.btn_seeDifferences.TabIndex = 6;
            this.btn_seeDifferences.Text = "See differences";
            this.btn_seeDifferences.UseVisualStyleBackColor = true;
            this.btn_seeDifferences.Click += new System.EventHandler(this.btn_seeDifferences_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1899, 1055);
            this.Controls.Add(this.btn_seeDifferences);
            this.Controls.Add(this.btn_chooseImages);
            this.Controls.Add(this.imgbox_second);
            this.Controls.Add(this.imgbox_original);
            this.Name = "Form1";
            this.Text = "Form1";
            ((System.ComponentModel.ISupportInitialize)(this.imgbox_original)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.imgbox_second)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private Emgu.CV.UI.ImageBox imgbox_original;
        private Emgu.CV.UI.ImageBox imgbox_second;
        private System.Windows.Forms.Button btn_chooseImages;
        private System.Windows.Forms.Button btn_seeDifferences;
    }
}

