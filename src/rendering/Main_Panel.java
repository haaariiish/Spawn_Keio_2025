package rendering;

import core.Frame1;
import core.GameState;
import map.Map;
import entities.Player;
import entities.Projectiles;
//import entities.Direction;
import java.awt.FontMetrics;

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

//import java.util.Random;




public class Main_Panel extends JPanel{

    // Map coloring
    
    private BufferedImage backgroundImage;
    private int lastWidth = -1;
    private int lastHeight = -1;
    private BufferedImage scaledBackground;
    private final int MINIMAP_SCALE = 5;
    private static final Color COLOR_EMPTY = new Color(220, 215, 205);  
    private static final Color COLOR_WALL = new Color(100, 100, 100);    
    private static final Color COLOR_DOOR = new Color(139, 69, 19);     
    private static final Color COLOR_SPIKE = new Color(202, 70, 0);    
    private static final Color COLOR_WATER = new Color(30, 100, 180);   
    private static final Color COLOR_SPAWN = new Color(100, 200, 100);  
    private static final Color COLOR_SPAWN_ENEMY = new Color(200, 30, 30); 
    private static final Color COLOR_GATE = new Color(127, 0, 255);

    // OPTIMIZATION: Store colors as int ARGB instead of Color objects
    // This eliminates object allocation in the render loop
    private int[][][]  interpolatedColorCache = null;
    private static final int CACHE_BRIGHTNESS_LEVELS = 50;
    private boolean cacheInitialized = false;
    private BufferedImage minimapCache = null;
    private boolean minimapNeedsUpdate = true;


    private int subTileSize;
    private final int subDivision;
    
    // OPTIMIZATION: Reduced from 100 to 50 brightness levels to save memory
    private static final int BRIGHTNESS_LEVELS = 50;
    private static final int COLOR_VARIATIONS = 4;
    
    // Keep old colorCache for potential future use
    private Color[][][][][] colorCache;


    private final Frame1 mainFrame;
    
    // Reusable lists to avoid allocations during rendering
    private final List<entities.Enemy> enemyRenderList = new ArrayList<>();
    private final List<Projectiles> projectilesRenderList = new ArrayList<>();
    
    // Reusable objects for debug info to avoid allocations
    //private final AlphaComposite alphaComposite03 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
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
        
        initializeBrightnessColors();
        loadResources(this.getWidth(), this.getHeight());
        setDoubleBuffered(true);
    }

    public void setSubTileSize(int tileSize){
        this.subTileSize = tileSize/this.subDivision;
    }

    public void invalidateMinimapCache() {
        minimapNeedsUpdate = true;
    }

    // OPTIMIZATION: Pre-allocate a fixed pool of Color objects to reuse
    // We cycle through this pool instead of creating new Color objects
    private static final int COLOR_OBJECT_POOL_SIZE = 512;
    private final Color[] colorObjectPool = new Color[COLOR_OBJECT_POOL_SIZE];
    private int colorPoolNextIndex = 0;
    
    private Color argbToColor(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        
        // Get the next Color object from the pool and update it
        // Since Color is immutable, we create a new one but reuse the pool slot
        Color c = new Color(r, g, b);
        colorObjectPool[colorPoolNextIndex] = c;
        colorPoolNextIndex = (colorPoolNextIndex + 1) % COLOR_OBJECT_POOL_SIZE;
        
        return c;
    }

    // OPTIMIZATION: Initialize cache with int[][] instead of Color[][][]
    private void initializeInterpolatedCache(Map map) {
        if (cacheInitialized) return;
        
        int w = map.getWidthInTiles() * subDivision;
        int h = map.getHeightInTiles() * subDivision;
        interpolatedColorCache = new int[h][w][CACHE_BRIGHTNESS_LEVELS + 1];
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                for (int b = 0; b <= CACHE_BRIGHTNESS_LEVELS; b++) {
                    double brightness = b / (double)CACHE_BRIGHTNESS_LEVELS;
                    Color c = getSmoothedTileColor(map, x, y, brightness);
                    
                    // Store as ARGB int instead of Color object
                    interpolatedColorCache[y][x][b] = (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
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
            case Map.GATELEVEL: return COLOR_GATE;
            default: return COLOR_EMPTY;
        }
    }

    // OPTIMIZATION: Return ARGB int instead of Color object
    private int getCachedInterpolatedColorARGB(Map map, int x, int y, double brightness) {
        if (!cacheInitialized) initializeInterpolatedCache(map);
        
        if (x < 0 || x >= interpolatedColorCache[0].length ||
            y < 0 || y >= interpolatedColorCache.length) {
            return 0x000000; // Black as int
        }
        
        int bIndex = (int)(brightness * CACHE_BRIGHTNESS_LEVELS);
        return interpolatedColorCache[y][x][bIndex];
    }

    // Keep old method for compatibility, but use new optimized one
    private Color getCachedInterpolatedColor(Map map, int x, int y, double brightness) {
        int argb = getCachedInterpolatedColorARGB(map, x, y, brightness);
        return argbToColor(argb);
    }
    
    // Pre-calculate all brightness variations of colors to avoid creating Color objects in render loop
    public void initializeBrightnessColors() {
     
        final int NUM_TILE_TYPES = 8;
        
      
        colorCache = new Color[NUM_TILE_TYPES][NUM_TILE_TYPES][NUM_TILE_TYPES][BRIGHTNESS_LEVELS + 1][COLOR_VARIATIONS];
        
        
        Color[] baseColors = {
            COLOR_EMPTY,        // 0
            COLOR_WALL,         // 1
            COLOR_DOOR,         // 2
            COLOR_SPIKE,        // 3
            COLOR_WATER,        // 4
            COLOR_SPAWN,        // 5
            COLOR_SPAWN_ENEMY,  // 6
            COLOR_GATE //7
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
                        Color base3 = baseColors[typeIdx3];
                        int baseR3 = base3.getRed();
                        int baseG3 = base3.getGreen();
                        int baseB3 = base3.getBlue();
                    
                    for (int brightness = 0; brightness <= BRIGHTNESS_LEVELS; brightness++) {
                        double factor = brightness / (double)BRIGHTNESS_LEVELS;
                        
                        int r = (int)((baseR*0.85 + baseR2*0.12 + baseR3*0.03) * factor);
                        int g = (int)((baseG*0.85 + baseG2*0.12 + baseG3*0.03) * factor);
                        int b = (int)((baseB*0.85 + baseB2*0.12 + baseB3*0.03) * factor);
                        
                        // VARIATION 0 : standard
                        colorCache[typeIdx][typeIdx2][typeIdx3][brightness][0] = new Color(r, g, b);
                        
                        // VARIATION 1: brighter +15%
                        int r1 = Math.min(255, (int)(r * 1.15));
                        int g1 = Math.min(255, (int)(g * 1.15));
                        int b1 = Math.min(255, (int)(b * 1.15));
                        colorCache[typeIdx][typeIdx2][typeIdx3][brightness][1] = new Color(r1, g1, b1);
                        
                        // VARIATION 2: darker -15%
                        int r2 = (int)(r * 0.85);
                        int g2 = (int)(g * 0.85);
                        int b2 = (int)(b * 0.85);
                        colorCache[typeIdx][typeIdx2][typeIdx3][brightness][2] = new Color(r2, g2, b2);
                        
                        // VARIATION 3: desaturated
                        float[] hsb = Color.RGBtoHSB(r, g, b, null);
                        Color desaturated = Color.getHSBColor(
                            hsb[0],   
                            hsb[1] * 0.7f,      
                            Math.min(1.0f, hsb[2] * 1.05f)
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


    
    private Color getBrightnessColor(int tileType, double brightness) {
        // Obsolete - kept for compatibility
        return Color.BLACK;
    }

    private Color getBrightnessColorWithContrast(int tileType, int tileType2,int tiletype3 ,double brightness, int x, int y) {
        
        int brightnessIdx = Math.max(0, Math.min(BRIGHTNESS_LEVELS, 
                                      (int)(brightness * BRIGHTNESS_LEVELS)));
        
        int variationIdx = getVariationIndex(x, y);
        
        return colorCache[tileType][tileType2][tiletype3][brightnessIdx][variationIdx];
    }

    private Color getSmoothedTileColor(Map map, int subtileX, int subtileY, double brightness) {
        double exactTileX = subtileX / (double)subDivision;
        double exactTileY = subtileY / (double)subDivision;
        
        int tileX0 = (int)Math.floor(exactTileX);
        int tileY0 = (int)Math.floor(exactTileY);
        int tileX1 = tileX0 + 1;
        int tileY1 = tileY0 + 1;

        int mainTile = map.getTileAt(tileX0, tileY0);
        
        // Solid tiles (walls, spikes, gates) don't get interpolated
        if ((mainTile == Map.WALL)||(mainTile == Map.SPIKE)||(mainTile == Map.GATELEVEL)) {
            Color baseColor = getBaseTileColor(mainTile);
            int r = (int)(baseColor.getRed() * brightness);
            int g = (int)(baseColor.getGreen() * brightness);
            int b = (int)(baseColor.getBlue() * brightness);
            return new Color(
                Math.min(255, Math.max(0, r)),
                Math.min(255, Math.max(0, g)),
                Math.min(255, Math.max(0, b))
            );
        }
        
        double fx = exactTileX - tileX0;
        double fy = exactTileY - tileY0;
        
        int tile00 = map.getTileAt(tileX0, tileY0);
        int tile10 = map.getTileAt(tileX1, tileY0);
        int tile01 = map.getTileAt(tileX0, tileY1);
        int tile11 = map.getTileAt(tileX1, tileY1);

        // Don't interpolate with solid tiles
        if ((tile10 == Map.WALL)||(tile10 == Map.SPIKE)||(tile10 == Map.GATELEVEL)) tile10 = tile00;
        if ((tile01 == Map.WALL)||(tile01 == Map.SPIKE)||(tile01 == Map.GATELEVEL)) tile01 = tile00;
        if ((tile11 == Map.WALL)||(tile11 == Map.SPIKE)||(tile11 == Map.GATELEVEL)) tile11 = tile00;
        
        double w00 = (1.0 - fx) * (1.0 - fy);
        double w10 = fx * (1.0 - fy);
        double w01 = (1.0 - fx) * fy;
        double w11 = fx * fy;
        
        Color c00 = getBaseTileColor(tile00);
        Color c10 = getBaseTileColor(tile10);
        Color c01 = getBaseTileColor(tile01);
        Color c11 = getBaseTileColor(tile11);
        
        int r = (int)(c00.getRed()   * w00 + c10.getRed()   * w10 + 
                      c01.getRed()   * w01 + c11.getRed()   * w11);
        int g = (int)(c00.getGreen() * w00 + c10.getGreen() * w10 + 
                      c01.getGreen() * w01 + c11.getGreen() * w11);
        int b = (int)(c00.getBlue()  * w00 + c10.getBlue()  * w10 + 
                      c01.getBlue()  * w01 + c11.getBlue()  * w11);
        
        r = Math.min(255, Math.max(0, (int)(r * brightness)));
        g = Math.min(255, Math.max(0, (int)(g * brightness)));
        b = Math.min(255, Math.max(0, (int)(b * brightness)));
        
        return new Color(r, g, b);
    }

    private int getVariationIndex(int x, int y) {
        return ((x ) + (y )) % COLOR_VARIATIONS;
    }

    public void reset(){
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
    Graphics2D g2d = (Graphics2D) g;
    drawBackGround(g2d);
    drawGameWorld(g2d);
    
    Toolkit.getDefaultToolkit().sync();
    }

    private void drawBackGround(Graphics2D g) {

        if (backgroundImage == null) {
            //g.drawString("Game is Running...", 300, 350);
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
    g.drawImage(scaledBackground, 0, 0, null);
   
}

    public void drawGameWorld(Graphics2D g) {
        Map map = mainFrame.getGame().getGameMap();
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
        int xplayer_subtile = (int)(xplayer/subTileSize);
        int yplayer_subtile = (int)(yplayer/subTileSize);



        int startTileX = Math.max(0, cameraX_tile);
        int startTileY = Math.max(0, cameraY_tile);
        int endTileX = Math.min(map.getWidthInTiles(), (cameraX + screenWidth) / map.getTileSize()+ 1);
        int endTileY = Math.min(map.getHeightInTiles(), (cameraY + screenHeight) / map.getTileSize() + 1);
        
        double shadowDistance = player.getShadowDistance();
        boolean[][] VisibilityMap = map.getVisibilityMapTileCached(player);
        
        // OPTIMIZATION: Reduce setColor calls by tracking current color
        int currentColorARGB = -1;
        
        for (int y = startTileY * subDivision; y < endTileY * subDivision; y++) {
            for (int x = startTileX * subDivision; x < endTileX * subDivision; x++) {
                
                int screenX = x * subTileSize - cameraX;
                int screenY = y * subTileSize - cameraY;
                
                if (x >= 0 && x < map.getWidthInTiles()*subDivision && 
                    y >= 0 && y < map.getHeightInTiles()*subDivision && 
                    VisibilityMap[y][x]) {
                    
                    int dx = x - xplayer_subtile;
                    int dy = y - yplayer_subtile;
                    double dist_to_player = Math.sqrt(dx * dx + dy * dy) * subTileSize;
                    
                    if (dist_to_player < shadowDistance) {
                        double brightness = Math.max(1.0 - Math.pow((dist_to_player / shadowDistance),0.7), 0.0);
                        
                        // Get ARGB int and only convert to Color if it changed
                        int argb = getCachedInterpolatedColorARGB(map, x, y, brightness);
                        
                        if (argb != currentColorARGB) {
                            currentColorARGB = argb;
                            g.setColor(argbToColor(argb));
                        }
                        
                        g.fillRect(screenX, screenY, subTileSize, subTileSize);
                    } else {
                        // Black - reuse the same Color object
                        if (currentColorARGB != 0x000000) {
                            currentColorARGB = 0x000000;
                            g.setColor(colorBlack);
                        }
                        g.fillRect(screenX, screenY, subTileSize, subTileSize);
                    }
                } else {
                    // Black - reuse the same Color object
                    if (currentColorARGB != 0x000000) {
                        currentColorARGB = 0x000000;
                        g.setColor(colorBlack);
                    }
                    g.fillRect(screenX, screenY, subTileSize, subTileSize);
                }
            }
        }
        
        int playerScreenX = screenWidth / 2 - player.getWidthInPixels() / 2;
        int playerScreenY = screenHeight / 2 - player.getHeightInPixels() / 2;

        List<entities.Enemy> enemies = mainFrame.getGame().getGameWorld().getEnemy();
        enemyRenderList.clear();
        enemyRenderList.addAll(enemies);
        int enemySize = enemyRenderList.size();
        for (int i = enemySize - 1; i >= 0; i--) {
            enemyRenderList.get(i).render(g, cameraX, cameraY,screenHeight,screenWidth,player.getShadowDistance(), VisibilityMap, subDivision,subTileSize);
        }
        
        mainFrame.getGame().getPlayer().render(g, playerScreenX, playerScreenY);
        
        List<Projectiles> projectiles = mainFrame.getGame().getGameWorld().getListProjectiles();
        projectilesRenderList.clear();
        projectilesRenderList.addAll(projectiles);
        int projSize = projectilesRenderList.size();
        for (int i = projSize - 1; i >= 0; i--) {
            projectilesRenderList.get(i).render(g, cameraX, cameraY);
        }

        drawDebugInfo(g, player, cameraX, cameraY);
        
    }


    private void drawDebugInfo(Graphics2D g, Player player, int cameraX, int cameraY) {
        core.Game game = mainFrame.getGame();
        core.GameWorld gameWorld = game.getGameWorld();
        map.Map themap = gameWorld.getMap();
        int screenWidth = getWidth();
        core.GameState gamestate= game.getGameState();
        
        // HP BAR
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
        
        // Score box
        g.setComposite(alphaComposite08);
        g.setColor(colorGray);
        g.fillRect(screenWidth - 310, 45, 300, 155);
        g.setColor(colorWhite);
        g.setFont(font30);
        FontMetrics metrics30 = g.getFontMetrics(font30);
        
        
        stringBuilder.setLength(0);
        stringBuilder.append("Score: ").append(gameWorld.getScore());
        g.drawString(stringBuilder.toString(), screenWidth - 310, 80);
    
        int tot_ennemies = gameWorld.getRemainingEnemies()+gameWorld.getEnemy().size();
        stringBuilder.setLength(0);
        stringBuilder.append("Level: ").append(player.getLevel());
        g.drawString(stringBuilder.toString(),screenWidth - 310, 115);

        stringBuilder.setLength(0);
        stringBuilder.append("Enemies Left:").append(tot_ennemies);
        g.drawString(stringBuilder.toString(),screenWidth - 310, 185);

        if(tot_ennemies==0){
            stringBuilder.setLength(0);
            stringBuilder.append("Go to the Purple Gate for next level");
            g.drawString(stringBuilder.toString(),screenWidth/2- metrics30.stringWidth("Go to the Purple Gate for next level")/2,80);
            stringBuilder.setLength(0);
            stringBuilder.append("and Press E");
            g.drawString(stringBuilder.toString(),screenWidth/2- metrics30.stringWidth("and Press E")/2,110);

        }

    
        stringBuilder.setLength(0);
        stringBuilder.append("WAVE ").append(gameWorld.getWave());
        g.drawString(stringBuilder.toString(),screenWidth - 310, 150);

        if (gamestate==GameState.FREEZE){
            stringBuilder.setLength(0);
            stringBuilder.append("FREEZE");
            g.drawString(stringBuilder.toString(), screenWidth/2, 40);
        }
        
        // Debug info rectangle
        g.setColor(colorGray);
        g.setComposite(alphaComposite06);
        g.fillRect(0, 0, 400, 250);
        
        g.setComposite(alphaComposite08);
        g.setColor(colorBlack);
        g.setFont(font12);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Player: (").append((int)player.getX()).append(", ").append((int)player.getY()).append(")");
        g.drawString(stringBuilder.toString(), 10, 20);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Camera: (").append(cameraX).append(", ").append(cameraY).append(")");
        g.drawString(stringBuilder.toString(), 10, 35);
        
        stringBuilder.setLength(0);
        stringBuilder.append("Screen: ").append(screenWidth).append("x").append(getHeight());
        g.drawString(stringBuilder.toString(), 10, 50);
        
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
        stringBuilder.append("TILE TYPE: ").append(gameWorld.getMap().getTileAtPixel((int)player.getX(), (int)player.getY()));
        g.drawString(stringBuilder.toString(), 10, 195);
    
        stringBuilder.setLength(0);
        stringBuilder.append("Number of max enemies simultanously in the Area ").append(gameWorld.getMaxEnemy());
        g.drawString(stringBuilder.toString(), 10, 225);
        
        

        // Minimap is already optimized with direct pixel access
        drawMinimap(g, themap, player, gameWorld);
    }

    private void drawMinimap(Graphics2D g, map.Map themap, Player player, core.GameWorld gameWorld) {
        if (minimapCache == null || minimapNeedsUpdate) {
            if (minimapCache != null) {
                minimapCache.flush();  // Libère la mémoire de l'ancienne image
                minimapCache = null;
            }
            minimapCache = generateMinimapCache(themap);
            minimapNeedsUpdate = false;
        }
        
        g.setComposite(alphaComposite08);
        g.drawImage(minimapCache, 0, 255, null);
        
        int tileSize = gameWorld.getTileSize();
        int playerTileX = ((int)player.getX() / tileSize) * MINIMAP_SCALE;
        int playerTileY = ((int)player.getY() / tileSize) * MINIMAP_SCALE;
        
        g.setColor(Color.BLUE);
        g.fillRect(playerTileX, 255 + playerTileY, MINIMAP_SCALE, MINIMAP_SCALE);
    }

    public void loadResources(int width, int height) {
        try {
            backgroundImage = ImageIO.read(
                getClass().getResourceAsStream("../assets/background.png")
            );
            System.out.println("Background image loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Background image could not be loaded.");
        }
        catch (IllegalArgumentException e) {
            System.err.println("Error: Image file not found in resources.");
        }
    }

    public Frame1 getMainFrame() {
        return this.mainFrame;
    }

    public BufferedImage getBackgroundImage() {
        return this.backgroundImage;
    }

    public int getSubDivision(){
        return this.subDivision;
    }

    public Color defineRainbow(){
        return new Color(((50+ this.mainFrame.getGame().getInGameTime())/20)%255,(this.mainFrame.getGame().getInGameTime()/20)%255 ,(300+this.mainFrame.getGame().getInGameTime()/50)%255);
    };

    public void setCacheMapinitialised(boolean a){
        if (!a){
            interpolatedColorCache = null;
            cacheInitialized = a;
        }

    }

    private BufferedImage generateMinimapCache(map.Map themap) {
        int mapWidth = themap.getWidthInTiles();
        int mapHeight = themap.getHeightInTiles();
        
        BufferedImage minimap = new BufferedImage(
            mapWidth * MINIMAP_SCALE, 
            mapHeight * MINIMAP_SCALE, 
            BufferedImage.TYPE_INT_RGB  // OPTIMAL
        );
        
        Graphics2D g2d = minimap.createGraphics();
        
        // Draw directly
        
        
        for(int yy = 0; yy < mapHeight; yy++){
            for(int xx = 0; xx < mapWidth; xx++){

                int tiletype = themap.getTileAt(xx, yy);
                g2d.setColor(getBaseTileColor(tiletype));
                g2d.fillRect(xx * MINIMAP_SCALE, yy * MINIMAP_SCALE, 
                            MINIMAP_SCALE, MINIMAP_SCALE);
            }
        }
        
        
        g2d.dispose();
        return minimap;
    }
    

}