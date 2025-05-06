package pack1;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;



public class Test extends JPanel {
    
    public static void main(String[] args) throws IOException {
        javax.swing.JFrame frame = new javax.swing.JFrame("Image Background Panel");
        Test panel = new Test("imgs/logo.png");

        frame.setContentPane(panel);
        frame.setSize(600, 400); // You can change the size
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    private static final long serialVersionUID = 1L;
private Image backgroundImage;

  // Some code to initialize the background image.
  // Here, we use the constructor to load the image. This
  // can vary depending on the use case of the panel.
  public Test(String fileName) throws IOException {
    backgroundImage = ImageIO.read(new File(fileName));
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Draw the background image.
    g.drawImage(backgroundImage, 0, 0, this);
  }
  
 

}








