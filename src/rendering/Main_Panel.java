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

import java.util.Random;




public class Main_Panel extends JPanel{

    // Map coloring
    
    private BufferedImage backgroundImage;// the image loaded from resources
    private int lastWidth = -1;
    private int lastHeight = -1; //to avoid unnecessary rescaling of the image, the cache code will check if the size has changed before recalculate size
    private BufferedImage scaledBackground; // cached scaled image

    private static final Color COLOR_EMPTY = new Color(220, 215, 205);  
    private static final Color COLOR_WALL = new Color(45, 50, 55);    
    private static final Color COLOR_DOOR = new Color(139, 69, 19);     
    private static final Color COLOR_SPIKE = new Color(255, 140, 0);    
    private static final Color COLOR_WATER = new Color(30, 100, 180);   
    private static final Color COLOR_SPAWN = new Color(100, 200, 100);  
    private static final Color COLOR_SPAWN_ENEMY = new Color(200, 30, 30); 

    private Color[][][]  interpolatedColorCache = null;
    private static final int CACHE_BRIGHTNESS_LEVELS = 50;
    private boolean cacheInitialized = false;


    /*private static final Color COLOR_EMPTY = new Color(240, 240, 240);
    private static final Color COLOR_WALL = new Color(80, 80, 80);
    private static final Color COLOR_DOOR = new Color(139, 69, 19);
    private static final Color COLOR_SPIKE = Color.ORANGE;
    private static final Color COLOR_WATER = new Color(100, 149, 237);
    private static final Color COLOR_SPAWN = new Color(144, 238, 144);
    private static final Color COLOR_SPAWN_ENEMY = new Color(178, 34, 34);*/


    private int subTileSize;
    private int subDivision;
    
    // Pre-calculated color arrays to avoid creating Color objects every frame (fixes CodeCache)
    private static final int BRIGHTNESS_LEVELS = 500;
    private static final int COLOR_VARIATIONS = 4;
    
    private Color[][][][][] colorCache;
    //Obsolete
    private Color[][] brightnessColors = new Color[7][BRIGHTNESS_LEVELS]; // 7 tile types, 101 brightness levels (0-100)


    



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


    public Main_Panel(Frame1 mainFrame, int subDivision) { 
        this.mainFrame = mainFrame;
        this.subDivision = subDivision;
        setFocusable(true);
        setRequestFocusEnabled(true);
        
        initializeBrightnessColors(); // Pre-calculate all color variants
        loadResources(this.getWidth(), this.getHeight());
        setDoubleBuffered(true); // Enable double buffering for smoother rendering ( Not sure if needed )
    }

    public void setSubTileSize(int tileSize){
        this.subTileSize = tileSize/this.subDivision;
    }

    private void initializeInterpolatedCache(Map map) {
        if (cacheInitialized) return;
        
        int w = map.getWidthInTiles() * subDivision;
        int h = map.getHeightInTiles() * subDivision;
        interpolatedColorCache = new Color[h][w][CACHE_BRIGHTNESS_LEVELS + 1];
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                for (int b = 0; b <= CACHE_BRIGHTNESS_LEVELS; b++) {
                    double brightness = b / (double)CACHE_BRIGHTNESS_LEVELS;
                    interpolatedColorCache[y][x][b] = getSmoothedTileColor(map, x, y, brightness);
                }
            }
        }
        cacheInitialized = true;
    }
    

    

    private Color getBaseTileColor(int tileType) {
        switch (tileType) {
            case Map.EMPTY: return COLOR_EMPTY;
            case Map.WALL: return COLOR_WALL;
            case Map.DOOR: return COLOR_DOOR;
            case Map.SPIKE: return COLOR_SPIKE;
            case Map.WATER: return COLOR_WATER;
            case Map.SPAWN: return COLOR_SPAWN;
            case Map.ENEMY_SPAWN: return COLOR_SPAWN_ENEMY;
            default: return COLOR_EMPTY;
        }
    }

    private Color getCachedInterpolatedColor(Map map, int x, int y, double brightness) {
        if (!cacheInitialized) initializeInterpolatedCache(map);
        
        if (x < 0 || x >= interpolatedColorCache[0].length ||
            y < 0 || y >= interpolatedColorCache.length) {
            return Color.BLACK;
        }
        
        int bIndex = (int)(brightness * CACHE_BRIGHTNESS_LEVELS);
        return interpolatedColorCache[y][x][bIndex]; // O(1) !
    }
    
    // Pre-calculate all brightness variations of colors to avoid creating Color objects in render loop
    private void initializeBrightnessColors() {
     
        final int NUM_TILE_TYPES = 7;
        
      
        colorCache = new Color[NUM_TILE_TYPES][NUM_TILE_TYPES][NUM_TILE_TYPES][BRIGHTNESS_LEVELS + 1][COLOR_VARIATIONS];
        
        
        Color[] baseColors = {
            COLOR_EMPTY,        // 0
            COLOR_WALL,         // 1
            COLOR_DOOR,         // 2
            COLOR_SPIKE,        // 3
            COLOR_WATER,        // 4
            COLOR_SPAWN,        // 5
            COLOR_SPAWN_ENEMY   // 6
        };
        
        
        for (int typeIdx = 0; typeIdx < NUM_TILE_TYPES; typeIdx++) {
            Color base = baseColors[typeIdx];
            int baseR = base.getRed();
            int baseG = base.getGreen();
            int baseB = base.getBlue();
            for (int typeIdx2 = 0; typeIdx2 < NUM_TILE_TYPES; typeIdx2++) {
                    Color base2 = baseColors[typeIdx2];
                    int baseR2 = base2.getRed();
                    int baseG2 = base2.getGreen();
                    int baseB2 = base2.getBlue();
                    for (int typeIdx3 = 0; typeIdx3 < NUM_TILE_TYPES; typeIdx3++){
                        Color base3 = baseColors[typeIdx2];
                        int baseR3 = base3.getRed();
                        int baseG3 = base3.getGreen();
                        int baseB3 = base3.getBlue();
                    
                    for (int brightness = 0; brightness <= BRIGHTNESS_LEVELS; brightness++) {
                        //Calculate according to brightness level
                        double factor = brightness / (double)BRIGHTNESS_LEVELS;
                        
                        //old
                        /*int r = (int)((baseR*0.75+baseR2*0.1875 +baseR3*0.0625 )* factor);
                        int g = (int)((baseG*0.75+baseG2*0.1875 +baseG3*0.0625 )* factor);
                        int b = (int)((baseB*0.75+baseB2*0.1875 +baseB3*0.0625 )* factor);*/
                        int r = (int)((baseR*0.85 + baseR2*0.12 + baseR3*0.03) * factor);
                        int g = (int)((baseG*0.85 + baseG2*0.12 + baseG3*0.03) * factor);
                        int b = (int)((baseB*0.85 + baseB2*0.12 + baseB3*0.03) * factor);
                        
                        // VARIATION 0 : standard
                        colorCache[typeIdx][typeIdx2][typeIdx3][brightness][0] = new Color(r, g, b);
                        
                        // VARIATION 1: brighter +8%
                        
                        /*int r1 = Math.min(255, (int)(r * 1.08));
                        int g1 = Math.min(255, (int)(g * 1.08));
                        int b1 = Math.min(255, (int)(b * 1.08));*/
                        int r1 = Math.min(255, (int)(r * 1.15));
                        int g1 = Math.min(255, (int)(g * 1.15));
                        int b1 = Math.min(255, (int)(b * 1.15));
                        colorCache[typeIdx][typeIdx2][typeIdx3][brightness][1] = new Color(r1, g1, b1);
                        
                        // VARIATION 2: darker -8%
                        
                        /*int r2 = (int)(r * 0.92);
                        int g2 = (int)(g * 0.92);
                        int b2 = (int)(b * 0.92);*/
                        int r2 = (int)(r * 0.85);
                        int g2 = (int)(g * 0.85);
                        int b2 = (int)(b * 0.85);
                        colorCache[typeIdx][typeIdx2][typeIdx3][brightness][2] = new Color(r2, g2, b2);
                        
                        // VARIATION 3: deepness effect
                        
                        float[] hsb = Color.RGBtoHSB(r, g, b, null);
                        Color desaturated = Color.getHSBColor(
                            hsb[0],   
                            hsb[1] * 0.7f,      
                            Math.min(1.0f, hsb[2] * 1.05f)          
                            //hsb[1] * 0.85f,           // slightly gray
                            //hsb[2]                     
                        );
                        colorCache[typeIdx][typeIdx2][typeIdx3][brightness][3] = desaturated;
                    }
                }
            }
            }
            System.out.println("Color cache initialized: " + 
            (NUM_TILE_TYPES * NUM_TILE_TYPES * (BRIGHTNESS_LEVELS + 1) * COLOR_VARIATIONS) + 
            " colors pre-calculated");
        }

    
    // Get pre-calculated color based on tile type and brightness (0.0 to 1.0)
    private Color getBrightnessColor(int tileType, double brightness) {
        int typeIdx;
        switch (tileType) {
            case Map.EMPTY: typeIdx = 0; break;
            case Map.WALL: typeIdx = 1; break;
            case Map.DOOR: typeIdx = 2; break;
            case Map.SPIKE: typeIdx = 3; break;
            case Map.WATER: typeIdx = 4; break;
            case Map.SPAWN: typeIdx = 5; break;
            case Map.ENEMY_SPAWN: typeIdx = 6; break;
            default: typeIdx = 0; break;
        }
        
        int brightnessIndex = Math.max(0, Math.min(100, (int)(brightness * 100)));
        return brightnessColors[typeIdx][brightnessIndex];
    }

    private Color getBrightnessColorWithContrast(int tileType, int tileType2,int tiletype3 ,double brightness, int x, int y) {
        
        int brightnessIdx = Math.max(0, Math.min(BRIGHTNESS_LEVELS, 
                                      (int)(brightness * BRIGHTNESS_LEVELS)));
        
        // Call the function that choose the variation
        int variationIdx = getVariationIndex(x, y);
        
        
        // lookup
        return colorCache[tileType][tileType2][tiletype3][brightnessIdx][variationIdx];
    }

    private Color getSmoothedTileColor(Map map, int subtileX, int subtileY, double brightness) {
        // Position du subtile en coordonnées de tiles (avec décimales)
        double exactTileX = subtileX / (double)subDivision;
        double exactTileY = subtileY / (double)subDivision;
        
        // Tiles entiers autour
        int tileX0 = (int)Math.floor(exactTileX);
        int tileY0 = (int)Math.floor(exactTileY);
        int tileX1 = tileX0 + 1;
        int tileY1 = tileY0 + 1;
        
        // Position relative dans le tile (0.0 à 1.0)
        double fx = exactTileX - tileX0;  // Fraction X
        double fy = exactTileY - tileY0;  // Fraction Y
        
        // Récupérer les 4 tiles aux coins
        int tile00 = map.getTileAt(tileX0, tileY0);  // Top-left
        int tile10 = map.getTileAt(tileX1, tileY0);  // Top-right
        int tile01 = map.getTileAt(tileX0, tileY1);  // Bottom-left
        int tile11 = map.getTileAt(tileX1, tileY1);  // Bottom-right
        
        // Poids d'interpolation (bilinéaire)
        double w00 = (1.0 - fx) * (1.0 - fy);  // Top-left
        double w10 = fx * (1.0 - fy);          // Top-right
        double w01 = (1.0 - fx) * fy;          // Bottom-left
        double w11 = fx * fy;                  // Bottom-right
        
        // Obtenir les couleurs de base pour chaque tile
        Color c00 = getBaseTileColor(tile00);
        Color c10 = getBaseTileColor(tile10);
        Color c01 = getBaseTileColor(tile01);
        Color c11 = getBaseTileColor(tile11);
        
        // Interpoler les composantes RGB
        int r = (int)(c00.getRed()   * w00 + c10.getRed()   * w10 + 
                      c01.getRed()   * w01 + c11.getRed()   * w11);
        int g = (int)(c00.getGreen() * w00 + c10.getGreen() * w10 + 
                      c01.getGreen() * w01 + c11.getGreen() * w11);
        int b = (int)(c00.getBlue()  * w00 + c10.getBlue()  * w10 + 
                      c01.getBlue()  * w01 + c11.getBlue()  * w11);
        
        // Appliquer la luminosité
        r = Math.min(255, Math.max(0, (int)(r * brightness)));
        g = Math.min(255, Math.max(0, (int)(g * brightness)));
        b = Math.min(255, Math.max(0, (int)(b * brightness)));
        
        return new Color(r, g, b);
    }

    private int getVariationIndex(int x, int y) {
        //regular pattern
    return ((x ) + (y )) % COLOR_VARIATIONS;
        //int hash = (x * 7393) ^ (y * 1663);
        //return Math.abs(hash) % COLOR_VARIATIONS;
        //Random random = new Random(); 
        //return Math.min(Math.max((int)(random.nextGaussian() * 1 + 0)*3,0),3) ;
        //return (3*mainFrame.getGame().getInGameTime())% COLOR_VARIATIONS;
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
        //int xplayer_tile =(int)(xplayer/map.getTileSize());
        //int yplayer_tile =(int)(yplayer/map.getTileSize());
        int xplayer_subtile = (int)(xplayer/subTileSize);
        int yplayer_subtile = (int)(yplayer/subTileSize);



        int startTileX = Math.max(0, cameraX_tile);
        int startTileY = Math.max(0, cameraY_tile);
        int endTileX = Math.min(map.getWidthInTiles(), (cameraX + screenWidth) / map.getTileSize()+ 1);
        int endTileY = Math.min(map.getHeightInTiles(), (cameraY + screenHeight) / map.getTileSize() + 1);
        
        double shadowDistance = player.getShadowDistance();
        boolean[][] VisibilityMap = map.getVisibilityMapTileCached(player);
        
        // Iterate through visible tiles using subdivision
        for (int y = startTileY * subDivision; y < endTileY * subDivision; y++) {
            for (int x = startTileX * subDivision; x < endTileX * subDivision; x++) {
                //int tileX = x / subDivision;
                //int tileY = y / subDivision;
                
                // Check visibility at TILE level (not subtile)
                if (x >= 0 && x < map.getWidthInTiles()*subDivision && 
                    y >= 0 && y < map.getHeightInTiles()*subDivision && 
                    VisibilityMap[y][x]) {
                    
                    //int tileType = map.getTileAt1(x, y);
                    //int tileType2 = map.getTileAt2(x, y); // here categories are subtiles
                    //int tileType3 = map.getTileAt3(x, y);
                    // Calculate distance from player in pixels using subtiles for smooth gradient
                    int dx = x - xplayer_subtile;
                    int dy = y - yplayer_subtile;
                    double dist_to_player = Math.sqrt(dx * dx + dy * dy) * subTileSize;
                    
                    // Only render if within shadow distance
                    if (dist_to_player < shadowDistance) {
                        // Calculate brightness (1.0 = full bright, 0.0 = black)
                       // double brightness = Math.max(1.0 - (dist_to_player / shadowDistance), 0.0);
                        double brightness = Math.max(1.0 - Math.pow((dist_to_player / shadowDistance),0.7), 0.0);
                        
                        g.setColor(getCachedInterpolatedColor(map, x, y, brightness));
                        //INTERPOLATED
                        //g.setColor(getSmoothedTileColor(map, x, y, brightness));

                        // Use pre-calculated color instead of creating new Color object
                        //g.setColor(getBrightnessColorWithContrast(tileType, tileType2, tileType3, brightness, x, y));
                        
                        // Calculate screen position
                        int screenX = x * subTileSize - cameraX;
                        int screenY = y * subTileSize - cameraY;
                        
                        g.fillRect(screenX, screenY, subTileSize, subTileSize);
                    } else {
                        // Beyond shadow distance, render as black
                        g.setColor(Color.BLACK);
                        int screenX = x * subTileSize - cameraX;
                        int screenY = y * subTileSize - cameraY;
                        g.fillRect(screenX, screenY, subTileSize, subTileSize);
                    }
                } else {
                    // Not visible or out of bounds, render as black
                    g.setColor(Color.BLACK);
                    int screenX = x * subTileSize - cameraX;
                    int screenY = y * subTileSize - cameraY;
                    g.fillRect(screenX, screenY, subTileSize, subTileSize);
                }
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
            enemyRenderList.get(i).render(g, cameraX, cameraY,screenHeight,screenWidth,player.getShadowDistance(), VisibilityMap, subDivision,subTileSize);
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

    public int getSubDivision(){
        return this.subDivision;
    }

    // Custom color
    public Color defineRainbow(){
        return new Color(((50+ this.mainFrame.getGame().getInGameTime())/20)%255,(this.mainFrame.getGame().getInGameTime()/20)%255 ,(300+this.mainFrame.getGame().getInGameTime()/50)%255);
    };

}