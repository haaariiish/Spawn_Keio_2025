package rendering;

import core.Frame1;
import map.Map;
import entities.Player;
import entities.Projectiles;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.BasicStroke;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;




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

    public int subTileSize;
    public int subDivision = 4;
    public static final int SHADOW_DISTANCE = 300;
    // Entity color





    private Frame1 mainFrame;
    
    // Reusable lists to avoid allocations during rendering
    private final List<entities.Enemy> enemyRenderList = new ArrayList<>();
    private final List<Projectiles> projectilesRenderList = new ArrayList<>();
    
    // Reusable objects for debug info to avoid allocations
    private final AlphaComposite alphaComposite06 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
    private final AlphaComposite alphaComposite08 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
    private final Font font12 = new Font("Monospaced", Font.PLAIN, 12);
    private final Font font30 = new Font("Monospaced", Font.PLAIN, 30);
    private final BasicStroke stroke3 = new BasicStroke(3.0f);
    private final Color colorYellow = Color.YELLOW;
    private final Color colorGray = Color.GRAY;
    private final Color colorBlack = Color.BLACK;
    private final Color colorWhite = Color.WHITE;
    private final Color colorHP1 = new Color(140, 27, 35);
    private final Color colorHP2 = new Color(227, 27, 35);
    private final StringBuilder stringBuilder = new StringBuilder(64);


    public Main_Panel(Frame1 mainFrame) { 
        this.mainFrame = mainFrame;
        setFocusable(true);
        setRequestFocusEnabled(true);
        
        loadResources(this.getWidth(), this.getHeight());
        setDoubleBuffered(true); // Enable double buffering for smoother rendering ( Not sure if needed )
    }

    public void setSubTileSize(int tileSize){
        this.subTileSize = tileSize/this.subDivision;
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
        // draw game world
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
        int cameraX_tile =(int)( cameraX/map.getTileSize());
        int cameraY_tile =(int)( cameraY/map.getTileSize());
        int xplayer_tile =(int)(xplayer/map.getTileSize());
        int yplayer_tile =(int)(yplayer/map.getTileSize());

        int startTileX = Math.max(0, cameraX_tile);
        int startTileY = Math.max(0, cameraY_tile);
        int endTileX = Math.min(map.getWidthInTiles(), (cameraX + screenWidth) / map.getTileSize()+ 1);
        int endTileY = Math.min(map.getHeightInTiles(), (cameraY + screenHeight) / map.getTileSize() + 1);
        double brighness;
        for (int y = startTileY; y < endTileY; y++) {
            for (int x = startTileX; x<endTileX; x++) {
                int tileType = map.getTileAt(x, y);
                //brighness = Math.max((1 - 2*Math.sqrt((x-xplayer_tile) *(x-xplayer_tile)  + (yplayer_tile-y)*(yplayer_tile-y))*2*map.getTileSize() / (screenWidth+screenHeight)),0);
                brighness = Math.max((1 - Math.sqrt((x-xplayer_tile) *(x-xplayer_tile)  + (yplayer_tile-y)*(yplayer_tile-y))*map.getTileSize() / SHADOW_DISTANCE),0);
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
                g.setColor(new Color((int)(brighness*g.getColor().getRed()), (int) (g.getColor().getGreen()*brighness), (int) (brighness*g.getColor().getBlue())));
                // To draw the map, according to the player position 
                int screenX = x * map.getTileSize() - cameraX;
                int screenY = y * map.getTileSize() - cameraY; 
                g.fillRect(screenX, screenY, 
                           map.getTileSize(), map.getTileSize());
                /*g.setColor(Color.RED );
                g.drawRect(screenX, screenY, map.getTileSize(), map.getTileSize());*/
                
            }
        }
        // TO center the camera to the exact position of the player
        int playerScreenX = screenWidth / 2 - player.getWidthInPixels() / 2;
        int playerScreenY = screenHeight / 2 - player.getHeightInPixels() / 2;

        // Enemies drawing - reuse list to avoid allocations
        List<entities.Enemy> enemies = mainFrame.getGame().getGameWorld().getEnemy();
        enemyRenderList.clear();
        enemyRenderList.addAll(enemies);
        int enemySize = enemyRenderList.size();
        for (int i = enemySize - 1; i >= 0; i--) {
            enemyRenderList.get(i).render(g, cameraX, cameraY,screenHeight,screenWidth,SHADOW_DISTANCE);
        }
        
        // Draw the player 
        mainFrame.getGame().getPlayer().render(g, playerScreenX, playerScreenY);
        
        // projectiles - reuse list to avoid allocations
        List<Projectiles> projectiles = mainFrame.getGame().getGameWorld().getListProjectiles();
        projectilesRenderList.clear();
        projectilesRenderList.addAll(projectiles);
        int projSize = projectilesRenderList.size();
        for (int i = projSize - 1; i >= 0; i--) {
            projectilesRenderList.get(i).render(g, cameraX, cameraY);
        }

        // DEBUG information just in case
        drawDebugInfo(g, player, cameraX, cameraY);
        
    }

    private void drawDebugInfo(Graphics2D g, Player player, int cameraX, int cameraY) {
        // Cache frequently accessed objects
        core.Game game = mainFrame.getGame();
        core.GameWorld gameWorld = game.getGameWorld();
        int screenWidth = getWidth();
        
        // HP BAR - use simple rectangles instead of roundRect to reduce CodeCache usage
        g.setComposite(alphaComposite06);
        g.setColor(colorHP1);
        g.fillRect(screenWidth - 310, 10, 300, 30);
        g.setColor(colorYellow);
        g.setStroke(stroke3);
        g.drawRect(screenWidth - 310, 10, 300, 30);
        g.setColor(colorHP2);
        int hpBarWidth = (int)(300.0 * player.getHP() / player.getMaxHP());
        if (hpBarWidth > 0) {
            g.fillRect(screenWidth - 310, 10, hpBarWidth, 30);
        }
        
        // Score box - use simple rectangles instead of roundRect to reduce CodeCache usage
        g.setComposite(alphaComposite08);
        g.setColor(colorGray);
        g.fillRect(screenWidth - 310, 45, 300, 45);
        g.setColor(colorWhite);
        g.setFont(font30);
        stringBuilder.setLength(0);
        stringBuilder.append("Score: ").append(gameWorld.getScore());
        g.drawString(stringBuilder.toString(), screenWidth - 310, 80);
        
        // Debug info rectangle - use simple rectangle instead of roundRect
        g.setColor(colorGray);
        g.setComposite(alphaComposite06);
        g.fillRect(0, 0, 300, 230);
        
        g.setComposite(alphaComposite08);
        g.setColor(colorBlack);
        g.setFont(font12);
        
        // Build strings without String.format to avoid CodeCache fragmentation
        stringBuilder.setLength(0);
        stringBuilder.append("Player: (").append((int)player.getX()).append(", ").append((int)player.getY()).append(")");
        g.drawString(stringBuilder.toString(), 10, 20);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Camera: (").append(cameraX).append(", ").append(cameraY).append(")");
        g.drawString(stringBuilder.toString(), 10, 35);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Screen: ").append(screenWidth).append("x").append(getHeight());
        g.drawString(stringBuilder.toString(), 10, 50);
        
        // Stats
        stringBuilder.setLength(0);
        stringBuilder.append("Facing: ").append(player.getFacing());
        g.drawString(stringBuilder.toString(), 10, 65);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Defense: ").append(player.getDefense());
        g.drawString(stringBuilder.toString(), 10, 95);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Attack: ").append(player.getAttack());
        g.drawString(stringBuilder.toString(), 10, 110);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Speed: ").append(player.getSpeed());
        g.drawString(stringBuilder.toString(), 10, 125);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Velocity (x,y): (").append((int)player.getVelocityX()).append(", ").append((int)player.getVelocityY()).append(")");
        g.drawString(stringBuilder.toString(), 10, 140);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Opened Time: ").append(game.getOpenTime());
        g.drawString(stringBuilder.toString(), 10, 165);
        
        stringBuilder.setLength(0);
        stringBuilder.append("In Game Time: ").append(game.getInGameTime());
        g.drawString(stringBuilder.toString(), 10, 180);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Number of Enemy in the Area ").append(gameWorld.getEnemy().size());
        g.drawString(stringBuilder.toString(), 10, 195);
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