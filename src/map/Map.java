package map;


import java.awt.Point;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;



import entities.Enemy;
import entities.Basic_Entity;



public class Map{

    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int DOOR = 2;
    public static final int SPIKE = 3;
    public static final int WATER = 4;
    
    public static final int ENEMY_SPAWN = 6;

    public static final int SPAWN = 5;
    public static final int GATELEVEL =7;
    
    public List<Room> rooms;
   
    // parameters for visibilityMap function
    private boolean[][] cachedVisibilityMap;
    private double lastVisibilityX = -1000;
    private double lastVisibilityY = -1000;
    private static final double VISIBILITY_UPDATE_THRESHOLD = 20.0;


    private int subTileSize_Render;
    private int subDivision;
    private int[][] tiles;
    private int[][] tilesPrimary;
    private int[][] tilesSecondary;
    private int[][] tilesTerciary;
    private int tileSize;
    private int widthInTiles;
    private int heightInTiles;


    public Map(int widthInTiles, int heightInTiles, int tileSize, int gameSubdiv) {
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.tileSize = tileSize;
        this.tiles = new int[heightInTiles][widthInTiles];
        this.setSubDivsion(gameSubdiv);
        //this.tilesPrimary = new int[heightInTiles*subDivision][widthInTiles*subDivision];
        //this.tilesSecondary = new int[heightInTiles*subDivision][widthInTiles*subDivision];
        //this.tilesTerciary =  new int[heightInTiles*subDivision][widthInTiles*subDivision];
        
    }

    

    // Collision method --------------------------------------------------------------


    public boolean isWall(int tileX, int tileY) {
        if (!isValidTile(tileX, tileY)) {
            return true; // out of bounds = solid wall
        }
        return tiles[tileY][tileX] == WALL;
    }

    public boolean isWallAtPixel(int pixelX, int pixelY) {
        return isWall(pixelX / tileSize, pixelY / tileSize);
    }

    public boolean collidesWithWall(int x, int y, int width, int height) {
        // Check corners and midpoints without creating array
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int x2 = x + width;
        int y2 = y + height;
        
        // Check all 8 points directly
        if (isWallAtPixel(x, y) ||                    // top-left
            isWallAtPixel(x2, y) ||                   // top-right
            isWallAtPixel(x, y2) ||                   // bottom-left
            isWallAtPixel(x2, y2) ||                  // bottom-right
            isWallAtPixel(x + halfWidth, y) ||        // top-middle
            isWallAtPixel(x + halfWidth, y2) ||      // bottom-middle
            isWallAtPixel(x, y + halfHeight) ||      // left-middle
            isWallAtPixel(x2, y + halfHeight)) {     // right-middle
            return true;
        }
        return false;
    }


    /**
     * Returns true if an enemy of "typical" size can stand with its center
     * in tile (tileX, tileY) without colliding with walls.
     * We approximate all enemies by a bounding box of pathfindingSizePx.
     */
    public boolean isWalkableTileForEnemies(int tileX, int tileY, Enemy enemy) {
        // First, tile itself must not be a wall
        if (isWall(tileX, tileY)) {
            return false;
        }

        // Approximate enemy size for pathfinding.
        //  biggest mobs, ~19 px , tiles = 40 px
        // take a boundary box slighly bigger
        // to avoid colision with wall 
        final int approxEnemySizePx = 20;                // ~size of enemy
        final int pathfindingSizePx = Math.min(tileSize - 4, approxEnemySizePx);

        int centerX = tileX * tileSize + tileSize / 2;
        int centerY = tileY * tileSize + tileSize / 2;

        int half = pathfindingSizePx / 2;
        int x = centerX - half;
        int y = centerY - half;

        return !collidesWithWall(x, y, pathfindingSizePx, pathfindingSizePx);
    }

     public int getTileAt(int tileX, int tileY) {
        if (!isValidTile(tileX, tileY)) {
            return WALL;
        }
        return tiles[tileY][tileX];
    }
    public int getTileAt1(int subtileX, int subtileY) {
        if (subtileX < 0 || subtileX >= widthInTiles * subDivision ||
            subtileY < 0 || subtileY >= heightInTiles * subDivision) {
            return WALL;
        }
        return tilesPrimary[subtileY][subtileX];
    }
    public int getTileAt2(int subtileX, int subtileY) {
        if (subtileX < 0 || subtileX >= widthInTiles * subDivision ||
            subtileY < 0 || subtileY >= heightInTiles * subDivision) {
            return WALL;
        }
        return tilesSecondary[subtileY][subtileX];
    }
    public int getTileAt3(int subtileX, int subtileY) {
        if (subtileX < 0 || subtileX >= widthInTiles * subDivision ||
            subtileY < 0 || subtileY >= heightInTiles * subDivision) {
            return WALL;
        }
        return tilesSecondary[subtileY][subtileX];
    }
    
    public int getTileAtPixel(int pixelX, int pixelY) {
        return getTileAt(pixelX / tileSize, pixelY / tileSize);
    }

    public void setTile(int tileX, int tileY, int type) {
        if (isValidTile(tileX, tileY)) {
            tiles[tileY][tileX] = type;
        }
    }

    // Load a Map ═══════════════════════════════════════════════════════════════
    
    //load a 2D array to load map data
    public void loadFromArray(int[][] mapData) {
        for (int y = 0; y < heightInTiles && y < mapData.length; y++) {
            for (int x = 0; x < widthInTiles && x < mapData[y].length; x++) {
                tiles[y][x] = mapData[y][x];
            }
        }
    }
    
    // Load a txt file to load map data
     // Format: each line = one line in game, each value separated by space
     
    public void loadFromFile(String filepath) {
        try (Scanner scanner = new Scanner(new File(filepath))) {
            int y = 0;
            while (scanner.hasNextLine() && y < heightInTiles) {
                String line = scanner.nextLine().trim();
                String[] values = line.split("\\s+");
                
                for (int x = 0; x < values.length && x < widthInTiles; x++) {
                    tiles[y][x] = Integer.parseInt(values[x]);
                }
                y++;
            }
        } catch (IOException e) {
            System.err.println("Erreur de chargement de map: " + e.getMessage());
            createDefaultMap();
        }
    }
    
    
     // Create a default map layout
     
    public void createDefaultMap() {
        // Border
        for (int x = 0; x < widthInTiles; x++) {
            tiles[0][x] = WALL;
            tiles[heightInTiles - 1][x] = WALL;
        }
        for (int y = 0; y < heightInTiles; y++) {
            tiles[y][0] = WALL;
            tiles[y][widthInTiles - 1] = WALL;
        }
        
        // obstacles
        for (int x = 5; x < 8; x++) {
            tiles[5][x] = WALL;
            tiles[10][x] = WALL;
        }
        
        // Spawn point
        tiles[7][10] = SPAWN;

        tiles[6][10] = ENEMY_SPAWN;
        
        // Door
        tiles[0][10] = DOOR;
    }

    public void createMapPerlinNoise(long seed){
        MapGenPerlin generation = new MapGenPerlin(this.heightInTiles, this.widthInTiles, seed);
        tiles = generation.getTiles();
        /*for (int x = 0; x < widthInTiles*subDivision; x++) {
            for (int y = 0; y < heightInTiles*subDivision; y++) {
                tilesPrimary[y][x]= tiles[y/subDivision][x/subDivision];
            }
        }
        int[] listing_tiles_proximal ;
        int max;
        int idx_max;
        int max2;
        int idx_max2;
        for (int x = 0; x < widthInTiles*subDivision; x++) {
            for (int y = 0; y < heightInTiles*subDivision; y++) {

                listing_tiles_proximal = new int[7];
                for (int xx = -2; xx < 3; xx++) {
                    for (int yy = -2; yy < 3; yy++) {
                        if (xx!=0||yy!=0){
                            listing_tiles_proximal[getTileAt1(x+xx, y+yy)]+= 1;
                            }
                    }
                }

                max= 0;
                max2 = 0;
                idx_max = 0;
                idx_max2 =0;
                for (int tileType2 = 0; tileType2 < 7; tileType2++) {
                    if (listing_tiles_proximal[tileType2]>max){
                        max2 = max;
                        idx_max2=idx_max;

                        max = listing_tiles_proximal[tileType2];
                        idx_max = tileType2;

                    }
                    else if(listing_tiles_proximal[tileType2]>max2){
                        max2 = listing_tiles_proximal[tileType2];
                        idx_max2 = tileType2;
                        
                    }
                }
                tilesSecondary[y][x]= idx_max;
                tilesTerciary[y][x]= idx_max2;
            }
        }*/
        this.rooms = generation.getRooms();
        clearSpawnPointsCache(); // Clear cache when map changes
    }

    

    public boolean hasLineOfSight(double x0, double y0, double x1, double y1) {
        double dx = x1 - x0;
        double dy = y1 - y0;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 1) return true;

        double dirX = dx / distance;
        double dirY = dy / distance;

        double perpX = -dirY;
        double perpY = dirX;

        double stepSize = tileSize / 8.0;
        int numSteps = (int)(distance / stepSize);
        
        double margin = tileSize / 4.0;

        for (int i = 1; i < numSteps; i++) {
            double baseX = x0 + dirX * stepSize * i;
            double baseY = y0 + dirY * stepSize * i;

            for (int offset = -1; offset <= 1; offset++) {
                double checkX = baseX + perpX * margin * offset;
                double checkY = baseY + perpY * margin * offset;

                int tileX = (int)(checkX / tileSize);
                int tileY = (int)(checkY / tileSize);

                if (isWall(tileX, tileY)) {
                    return false;
                }
            }
        }

        return true;
    }
    

    public boolean[][] getVisibilityMapTile(Basic_Entity entity) {
        boolean[][] visibilityMap = new boolean[heightInTiles*subDivision][widthInTiles*subDivision];
        boolean[][] visitedMap = new boolean[heightInTiles*subDivision][widthInTiles*subDivision];
        boolean[][] blockedMap = new boolean[heightInTiles*subDivision][widthInTiles*subDivision];
        
        int entityXtile = (int)(entity.getX() / subTileSize_Render);
        int entityYtile = (int)(entity.getY() / subTileSize_Render);

        
        double shadowDistPx = entity.getShadowDistance();

        int minX = Math.max(0, (int)((entity.getX() - shadowDistPx) / subTileSize_Render));
        int minY = Math.max(0, (int)((entity.getY() - shadowDistPx) / subTileSize_Render));
        int maxX = Math.min(widthInTiles*subDivision - 1, (int)((entity.getX() + shadowDistPx) / subTileSize_Render));
        int maxY = Math.min(heightInTiles*subDivision - 1, (int)((entity.getY() + shadowDistPx) / subTileSize_Render));

        
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {

                
                if (visitedMap[y][x]) {
                continue;
                }

        
                int dx = Math.abs(x - entityXtile);
                int dy = Math.abs(y - entityYtile);

                int sx = entityXtile < x ? 1 : -1;
                int sy = entityYtile < y ? 1 : -1;

                int err = dx - dy;

                int xx = entityXtile;
                int yy = entityYtile;
                
                while (!(xx == x && yy == y)) {

        
                    if (xx < 0 || xx >= widthInTiles*subDivision || yy < 0 || yy >= heightInTiles*subDivision) {
                        blockedMap[yy][xx] = true; 
                        break;
                     // Hors limites, arrêter
                    }

                    if (visitedMap[yy][xx]) {
                        if (blockedMap[yy][xx]) {
                            // Déjà visité et c'est un mur qui bloque
                            //blockedMap[yy][xx] = true;
                            break;
                        }
                        // Déjà visité et visible, continuer
                    } else {
                        // Pas encore visité
                        if (isWall(xx/subDivision, yy/subDivision)) {
                            visibilityMap[yy][xx] = true;
                            visitedMap[yy][xx] = true;
                            blockedMap[yy][xx] = true;
                            break;
                        } else {
                            visibilityMap[yy][xx] = true;
                            visitedMap[yy][xx] = true;
                        }
                    }
                    

                    // Avancer dans la ligne
                    int e2 = 2 * err;
                    if (e2 > -dy) {
                        err -= dy;
                        xx += sx;
                        }
                    if (e2 < dx) {
                        err += dx;
                        yy += sy;
                        }
                }

                
                if (xx >= 0 && xx < widthInTiles*subDivision && yy >= 0 && yy < heightInTiles*subDivision) {
                    if (!visitedMap[yy][xx]) {
                        if (isWall(xx/subDivision, yy/subDivision)) {
                            visibilityMap[yy][xx] = true;
                            blockedMap[yy][xx] = true;
                        } else {
                            visibilityMap[yy][xx] = true;
                        }
                        visitedMap[yy][xx] = true;
                    }
                }
            }
        }

        return visibilityMap;
        }

    public boolean[][] getVisibilityMapTileCached(Basic_Entity entity) {
        double entityX = entity.getX();
        double entityY = entity.getY();

       
        double dx = entityX - lastVisibilityX;
        double dy = entityY - lastVisibilityY;
        double distMoved = Math.sqrt(dx * dx + dy * dy);

        // Si l'entité n'a pas assez bougé, réutiliser le cache
        if (distMoved < VISIBILITY_UPDATE_THRESHOLD && cachedVisibilityMap != null) {
        return cachedVisibilityMap;
        }

        // Recalculer
        cachedVisibilityMap = getVisibilityMapTile(entity);
        lastVisibilityX = entityX;
        lastVisibilityY = entityY;

        return cachedVisibilityMap;
        }
    // Utils ------------------------------------------------------------------------------
    public boolean isValidTile(int tileX, int tileY) {
        return tileX >= 0 && tileX < widthInTiles && tileY >= 0 && tileY < heightInTiles;
    }

    public Point getSpawnPoint() {
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                if (tiles[y][x] == SPAWN) {
                    return new Point(x * tileSize +tileSize/2, y * tileSize + tileSize/2);
                }
            }
        }
        return new Point(tileSize, tileSize); // Default
    }

    private List<Point> cachedSpawnPoints = null;
    
    public List<Point> getEnemySpawnPoints(){
        // Cache the spawn points list to avoid recreating it
        if (cachedSpawnPoints == null) {
            cachedSpawnPoints = new ArrayList<>();
            int halfTile = tileSize / 2;
            for (int y = 0; y < heightInTiles; y++) {
                for (int x = 0; x < widthInTiles; x++) {
                    if (tiles[y][x] == ENEMY_SPAWN) {
                        cachedSpawnPoints.add(new Point(x * tileSize + halfTile, y * tileSize + halfTile));
                    }
                }
            }
        }
        return cachedSpawnPoints;
    }
    
    // Call this when map is regenerated to clear cache
    public void clearSpawnPointsCache() {
        cachedSpawnPoints = null;
    }

    // Getters
    public int getTileSize() { return tileSize; }
    public int getWidthInTiles() { return widthInTiles; }
    public int getHeightInTiles() { return heightInTiles; }
    public int getWidthInPixels() { return widthInTiles * tileSize; }
    public int getHeightInPixels() { return heightInTiles * tileSize; }
    public int getSubDiv(){return subDivision;}


    public void printToConsole() {
        System.out.println("═══ MAP DEBUG ═══");
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                System.out.print(tiles[y][x] + " ");
            }
            System.out.println();
        }
        System.out.println("═════════════════");
    }

    public void setSubTileSizeRender(){  
        this.subTileSize_Render = tileSize/subDivision;
    }

    public void setSubDivsion(int sbd){  
        this.subDivision = sbd;
        this.setSubTileSizeRender();
    }
}
