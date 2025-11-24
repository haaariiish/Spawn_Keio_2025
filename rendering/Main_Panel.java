package rendering;

import core.Frame1;
import map.Map;

import javax.swing.JPanel;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;



public class Main_Panel extends JPanel{

    // Map coloring
    
    private BufferedImage backgroundImage;// the image loaded from resources
    private int lastWidth = -1;
    private int lastHeight = -1; //to avoid unnecessary rescaling of the image, the chache code will check if the size has changed before recalculate size
    private BufferedImage scaledBackground; // cached scaled image

    private static final Color COLOR_EMPTY = new Color(240, 240, 240);
    private static final Color COLOR_WALL = new Color(80, 80, 80);
    private static final Color COLOR_DOOR = new Color(139, 69, 19);
    private static final Color COLOR_SPIKE = Color.ORANGE;
    private static final Color COLOR_WATER = new Color(100, 149, 237);
    private static final Color COLOR_SPAWN = new Color(144, 238, 144);
    private static final Color COLOR_SPAWN_ENEMY = new Color(178, 34, 34);

    // Entity color

    private static final Color COLOR_PLAYER = Color.YELLOW;
    private static final Color COLOR_ENEMY = Color.RED;


    private Frame1 mainFrame;


    public Main_Panel(Frame1 mainFrame) { 
        this.mainFrame = mainFrame;
        setFocusable(true);
        setRequestFocusEnabled(true);
        
        loadResources(this.getWidth(), this.getHeight());
        setDoubleBuffered(true); // Enable double buffering for smoother rendering ( Not sure if needed )
    }

    public void reset(){
        // Reset any game-specific variables if needed
        backgroundImage = null;
        scaledBackground = null;
        lastWidth = -1;
        lastHeight = -1;
        loadResources(this.getWidth(), this.getHeight());
        System.gc();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g; //better performance with Graphics2D
    drawBackGround(g2d);
    drawGameWorld(g2d);
    
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

    public void drawGameWorld(Graphics2D g) {
        // Placeholder for drawing the game world
        Map map = mainFrame.getGame().getGameMap(); // get the game map from the game instance to draw the element
        if (map == null) {
            g.drawString("Map not loaded.", 300, 350);
            return;
        }
        for (int y = 0; y < map.getHeightInTiles(); y++) {
            for (int x = 0; x < map.getWidthInTiles(); x++) {
                int tileType = map.getTileAt(x, y);
                switch (tileType) {
                    case Map.WALL:
                        g.setColor(COLOR_WALL);
                        break;
                    case Map.DOOR:
                        g.setColor(COLOR_DOOR);
                        break;
                    case Map.EMPTY:
                        g.setColor(COLOR_EMPTY);
                        break;
                    case Map.SPAWN:
                        g.setColor(COLOR_SPAWN);
                        break;
                    case Map.SPIKE:
                        g.setColor(COLOR_SPIKE);
                        break;
                    case Map.WATER:
                        g.setColor(COLOR_WATER);
                        break;
                    case Map.ENEMY_SPAWN:
                        g.setColor(COLOR_SPAWN_ENEMY);
                        break;
                    default:
                        g.setColor(java.awt.Color.LIGHT_GRAY);
                        break;
                }
                g.fillRect(x * map.getTileSize(), y * map.getTileSize(), 
                           map.getTileSize(), map.getTileSize());
            }
        }
        g.setColor(COLOR_PLAYER);
        g.fillOval((int) Math.round(mainFrame.getGame().getPlayer().getX()),(int) Math.round(mainFrame.getGame().getPlayer().getY()),mainFrame.getGame().getPlayer().getWidthInPixels(),mainFrame.getGame().getPlayer().getHeightInPixels());
        

        
        
    }


    // Load of RESOURCES ------------------------------------------------------
    public void loadResources(int width, int height) {
        try {
            // Utilise le ClassLoader pour trouver la ressource dans le projet
            backgroundImage = ImageIO.read(
                getClass().getResourceAsStream("../assets/background.png")
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

    public Frame1 getMainFrame() {
        return this.mainFrame;
    }

    //Getter
    public BufferedImage getBackgroundImage() {
        return this.backgroundImage;
    }

}