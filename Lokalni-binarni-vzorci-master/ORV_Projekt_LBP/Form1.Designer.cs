namespace ORV_Projekt_LBP
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
            this.iB_original = new Emgu.CV.UI.ImageBox();
            this.iB_post = new Emgu.CV.UI.ImageBox();
            this.btn_Open = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.iB_post_U = new Emgu.CV.UI.ImageBox();
            this.iB_post_d = new Emgu.CV.UI.ImageBox();
            ((System.ComponentModel.ISupportInitialize)(this.iB_original)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.iB_post)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.iB_post_U)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.iB_post_d)).BeginInit();
            this.SuspendLayout();
            // 
            // iB_original
            // 
            this.iB_original.Location = new System.Drawing.Point(12, 12);
            this.iB_original.Name = "iB_original";
            this.iB_original.Size = new System.Drawing.Size(375, 329);
            this.iB_original.TabIndex = 2;
            this.iB_original.TabStop = false;
            // 
            // iB_post
            // 
            this.iB_post.Location = new System.Drawing.Point(393, 12);
            this.iB_post.Name = "iB_post";
            this.iB_post.Size = new System.Drawing.Size(375, 329);
            this.iB_post.TabIndex = 3;
            this.iB_post.TabStop = false;
            // 
            // btn_Open
            // 
            this.btn_Open.Location = new System.Drawing.Point(774, 28);
            this.btn_Open.Name = "btn_Open";
            this.btn_Open.Size = new System.Drawing.Size(75, 23);
            this.btn_Open.TabIndex = 4;
            this.btn_Open.Text = "Open";
            this.btn_Open.UseVisualStyleBackColor = true;
            this.btn_Open.Click += new System.EventHandler(this.btn_Open_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(774, 12);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(0, 13);
            this.label1.TabIndex = 7;
            // 
            // iB_post_U
            // 
            this.iB_post_U.Location = new System.Drawing.Point(12, 348);
            this.iB_post_U.Name = "iB_post_U";
            this.iB_post_U.Size = new System.Drawing.Size(375, 329);
            this.iB_post_U.TabIndex = 8;
            this.iB_post_U.TabStop = false;
            // 
            // iB_post_d
            // 
            this.iB_post_d.Location = new System.Drawing.Point(393, 348);
            this.iB_post_d.Name = "iB_post_d";
            this.iB_post_d.Size = new System.Drawing.Size(375, 329);
            this.iB_post_d.TabIndex = 9;
            this.iB_post_d.TabStop = false;
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(876, 684);
            this.Controls.Add(this.iB_post_d);
            this.Controls.Add(this.iB_post_U);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.btn_Open);
            this.Controls.Add(this.iB_post);
            this.Controls.Add(this.iB_original);
            this.Name = "Form1";
            this.Text = "Form1";
            ((System.ComponentModel.ISupportInitialize)(this.iB_original)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.iB_post)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.iB_post_U)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.iB_post_d)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private Emgu.CV.UI.ImageBox iB_original;
        private Emgu.CV.UI.ImageBox iB_post;
        private System.Windows.Forms.Button btn_Open;
        private System.Windows.Forms.Label label1;
        private Emgu.CV.UI.ImageBox iB_post_U;
        private Emgu.CV.UI.ImageBox iB_post_d;
    }
}

