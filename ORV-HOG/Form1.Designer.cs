namespace ORV_HOG
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
            this.originalImage = new Emgu.CV.UI.ImageBox();
            this.btn_upload = new System.Windows.Forms.Button();
            this.newImage = new Emgu.CV.UI.ImageBox();
            this.grayImage = new Emgu.CV.UI.ImageBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.originalImage)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.newImage)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.grayImage)).BeginInit();
            this.SuspendLayout();
            // 
            // originalImage
            // 
            this.originalImage.Location = new System.Drawing.Point(12, 63);
            this.originalImage.Name = "originalImage";
            this.originalImage.Size = new System.Drawing.Size(377, 375);
            this.originalImage.TabIndex = 2;
            this.originalImage.TabStop = false;
            // 
            // btn_upload
            // 
            this.btn_upload.Location = new System.Drawing.Point(713, 12);
            this.btn_upload.Name = "btn_upload";
            this.btn_upload.Size = new System.Drawing.Size(75, 23);
            this.btn_upload.TabIndex = 3;
            this.btn_upload.Text = "Upload";
            this.btn_upload.UseVisualStyleBackColor = true;
            this.btn_upload.Click += new System.EventHandler(this.btn_upload_Click);
            // 
            // newImage
            // 
            this.newImage.Location = new System.Drawing.Point(827, 63);
            this.newImage.Name = "newImage";
            this.newImage.Size = new System.Drawing.Size(393, 375);
            this.newImage.TabIndex = 4;
            this.newImage.TabStop = false;
            // 
            // grayImage
            // 
            this.grayImage.Location = new System.Drawing.Point(413, 63);
            this.grayImage.Name = "grayImage";
            this.grayImage.Size = new System.Drawing.Size(393, 375);
            this.grayImage.TabIndex = 5;
            this.grayImage.TabStop = false;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(13, 49);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(99, 17);
            this.label1.TabIndex = 6;
            this.label1.Text = "Original Image";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(410, 49);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(71, 17);
            this.label2.TabIndex = 7;
            this.label2.Text = "Optimized";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(824, 49);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(40, 17);
            this.label3.TabIndex = 8;
            this.label3.Text = "HOG";
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1232, 450);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.grayImage);
            this.Controls.Add(this.newImage);
            this.Controls.Add(this.btn_upload);
            this.Controls.Add(this.originalImage);
            this.Name = "Form1";
            this.Text = "Form1";
            ((System.ComponentModel.ISupportInitialize)(this.originalImage)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.newImage)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.grayImage)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private Emgu.CV.UI.ImageBox originalImage;
        private System.Windows.Forms.Button btn_upload;
        private Emgu.CV.UI.ImageBox newImage;
        private Emgu.CV.UI.ImageBox grayImage;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
    }
}

