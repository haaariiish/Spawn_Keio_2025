package rendering;

import core.Frame1;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.RenderingHints;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Main_Panel extends JPanel{
    private BufferedImage backgroundImage;// the image loaded from resources
    private int lastWidth = -1;
    private int lastHeight = -1; //to avoid unnecessary rescaling of the image, the chache code will check if the size has changed before recalculate size
    private BufferedImage scaledBackground; // cached scaled image


    private Frame1 mainFrame;
    public Main_Panel(Frame1 mainFrame) { 
        this.mainFrame = mainFrame;
        loadResources(this.getWidth(), this.getHeight());

        setDoubleBuffered(true); // Enable double buffering for smoother rendering ( Not sure if needed )
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g; //better performance with Graphics2D
    drawBackGround(g2d);
    
    Toolkit.getDefaultToolkit().sync();
    }

    private void drawBackGround(Graphics2D g) {
        // Dessiner le monde du jeu
        //System.out.println("Game is Running...");


        if (backgroundImage == null) { // in case the image is not loaded
            g.drawString("Game is Running...", 300, 350);
            return;
        }  

        int currentWidth = getWidth();
        int currentHeight = getHeight();

        if (scaledBackground == null || 
            lastWidth != currentWidth || 
            lastHeight != currentHeight) {
            
            scaledBackground = new BufferedImage(
                currentWidth, currentHeight, BufferedImage.TYPE_INT_RGB
            );
        

        Graphics2D g2 = scaledBackground.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                               RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(backgroundImage, 0, 0, currentWidth, currentHeight, null);
        g2.dispose();

        lastWidth = currentWidth;
        lastHeight = currentHeight;
    
   
    }
     // This way is far more efficient than scaling the image every frame because scaling is costly in term of performance 
    g.drawImage(scaledBackground, 0, 0, null);
   
}

    public void loadResources(int width, int height) {
        try {
            // Utilise le ClassLoader pour trouver la ressource dans le projet
            backgroundImage = ImageIO.read(
                getClass().getResourceAsStream("pixel_art_background_by_isa_draws_d9s3e6d-fullview.jpg")
            );
            System.out.println("Background image loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Background image could not be loaded.");
        }
        // if the background image doesn't exist (eg: print a error message in game)
        catch (IllegalArgumentException e) {
            System.err.println("Error: Image file not found in resources.");
        }
    }

}