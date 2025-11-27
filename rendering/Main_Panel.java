package rendering;

import core.Frame1;
import map.Map;
import entities.Player;
import entities.Projectiles;

import javax.swing.JPanel;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;




public class Main_Panel extends JPanel{

    // Map coloring
    
    private BufferedImage backgroundImage;// the image loaded from resources
    private int lastWidth = -1;
    private int lastHeight = -1; //to avoid unnecessary rescaling of the image, the cache code will check if the size has changed before recalculate size
    private BufferedImage scaledBackground; // cached scaled image

    private static final Color COLOR_EMPTY = new Color(240, 240, 240);
    private static final Color COLOR_WALL = new Color(80, 80, 80);
    private static final Color COLOR_DOOR = new Color(139, 69, 19);
    private static final Color COLOR_SPIKE = Color.ORANGE;
    private static final Color COLOR_WATER = new Color(100, 149, 237);
    private static final Color COLOR_SPAWN = new Color(144, 238, 144);
    private static final Color COLOR_SPAWN_ENEMY = new Color(178, 34, 34);

    // Entity color





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

        Player player = mainFrame.getGame().getPlayer();

        int xplayer = (int) Math.round(player.getX());
        int yplayer = (int) Math.round(player.getY());

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        int cameraX = xplayer - screenWidth / 2 + player.getWidthInPixels() / 2;
        int cameraY = yplayer - screenHeight / 2 + player.getHeightInPixels() / 2;

        int startTileX = Math.max(0, cameraX / map.getTileSize());
        int startTileY = Math.max(0, cameraY / map.getTileSize());
        int endTileX = Math.min(map.getWidthInTiles(), (cameraX + screenWidth) / map.getTileSize()+ 1);
        int endTileY = Math.min(map.getHeightInTiles(), (cameraY + screenHeight) / map.getTileSize() + 1);

        for (int y = startTileY; y < endTileY; y++) {
            for (int x = startTileX; x<endTileX; x++) {
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
                // To draw the map, according to the player position 
                int screenX = x * map.getTileSize() - cameraX;
                int screenY = y * map.getTileSize() - cameraY; 
                g.fillRect(screenX, screenY, 
                           map.getTileSize(), map.getTileSize());
                g.setColor(Color.RED);
                g.drawRect(screenX, screenY, map.getTileSize(), map.getTileSize());
                
            }
        }
        // TO center the camera to the exact position of the player
        int playerScreenX = screenWidth / 2 - player.getWidthInPixels() / 2;
        int playerScreenY = screenHeight / 2 - player.getHeightInPixels() / 2;

        // Enemies drawing

        
        for (int i = mainFrame.getGame().getGameWorld().getEnemy().size()-1;i>=0;i--){
             mainFrame.getGame().getGameWorld().getEnemy().get(i).render(g, cameraX,cameraY);
        }
        
        // Draw the player 

        
        mainFrame.getGame().getPlayer().render(g, playerScreenX, playerScreenY);
        //g.fillOval(playerScreenX,playerScreenY,mainFrame.getGame().getPlayer().getWidthInPixels(),mainFrame.getGame().getPlayer().getHeightInPixels());
        
        // projectiles
        List<Projectiles> projectilesList = mainFrame.getGame().getGameWorld().getListProjectiles();
        for (int i = projectilesList.size() - 1; i >= 0; i--){
            projectilesList.get(i).render(g, cameraX,cameraY);
        }

        // DEBUG information just in case
        drawDebugInfo(g, player, cameraX, cameraY);
        
    }

    private void drawDebugInfo(Graphics2D g, Player player, int cameraX, int cameraY) {
        
        g.setColor(Color.GRAY);
        g.fillRoundRect(0,0, 300, 230, 5, 5);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        g.drawString(String.format("Player: (%.0f, %.0f)", player.getX(), player.getY()), 10, 20);
        g.drawString(String.format("Camera: (%d, %d)", cameraX, cameraY), 10, 35);
        g.drawString(String.format("Screen: %dx%d", getWidth(), getHeight()), 10, 50);

        // SHOW stats
        //System.out.println("Facing: "+this.mainFrame.getGame().getPlayer().getFacing());
        //System.out.println("HP"+ this.mainFrame.getGame().getPlayer().getHP());
        g.drawString("Facing: "+this.mainFrame.getGame().getPlayer().getFacing(), 10, 65);
        g.drawString("HP: "+this.mainFrame.getGame().getPlayer().getHP() ,10, 80);
        g.drawString("Defense: "+this.mainFrame.getGame().getPlayer().getDefense() ,10, 95);
        g.drawString("Attack: "+this.mainFrame.getGame().getPlayer().getAttack() ,10, 110);
        g.drawString("Speed: "+this.mainFrame.getGame().getPlayer().getSpeed() ,10, 125);
        g.drawString(String.format("Velocity (x,y): (%.0f, %.0f)",this.mainFrame.getGame().getPlayer().getVelocityX() ,this.mainFrame.getGame().getPlayer().getVelocityY() ),10, 140);
        g.drawString("Opened Time: "+this.mainFrame.getGame().getOpenTime() ,10, 165);
        g.drawString("In Game Time: "+this.mainFrame.getGame().getInGameTime() ,10, 180);
        g.drawString("Number of Enemy in the Area "+this.mainFrame.getGame().getGameWorld().getEnemy().size(),10, 195);
        g.drawString("Score: "+this.mainFrame.getGame().getGameWorld().getScore(),10, 210);
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

    // Custom color
    public Color defineRainbow(){
        return new Color(((50+ this.mainFrame.getGame().getInGameTime())/20)%255,(this.mainFrame.getGame().getInGameTime()/20)%255 ,(300+this.mainFrame.getGame().getInGameTime()/50)%255);
    };

}