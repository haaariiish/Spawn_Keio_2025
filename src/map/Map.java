package map;


import java.awt.Point;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import entities.Enemy;



public class Map{

    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int DOOR = 2;
    public static final int SPIKE = 3;
    public static final int WATER = 4;
    
    public static final int ENEMY_SPAWN = 6;

    public static final int SPAWN = 5;
    
    public List<Room> rooms;
   


    private int[][] tiles;
    private int tileSize;
    private int widthInTiles;
    private int heightInTiles;


    public Map(int widthInTiles, int heightInTiles, int tileSize) {
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.tileSize = tileSize;
        this.tiles = new int[heightInTiles][widthInTiles];
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
        this.rooms = generation.getRooms();
        clearSpawnPointsCache(); // Clear cache when map changes
    }

    public boolean hasLineOfSight(double x0, double y0, double x1, double y1) {
        int tileX0 = (int)(x0 / tileSize);
        int tileY0 = (int)(y0 / tileSize);
        int tileX1 = (int)(x1 / tileSize);
        int tileY1 = (int)(y1 / tileSize);
    
        // if seen
        if (tileX0 == tileX1 && tileY0 == tileY1) {
            return true;
        }
    
        int dx = Math.abs(tileX1 - tileX0);
        int dy = Math.abs(tileY1 - tileY0);
    
        int sx = tileX0 < tileX1 ? 1 : -1;
        int sy = tileY0 < tileY1 ? 1 : -1;
    
        int err = dx - dy;
    
        int x = tileX0;
        int y = tileY0;
    
        while (true) {
            // verification if at the start or the beginning
            if (!(x == tileX0 && y == tileY0) && !(x == tileX1 && y == tileY1)) {
                // verify if the tile is a wall
                if (isWall(x, y)) {
                    return false;  //if wall block the view
                }
            }
    
            // reach the end 
            if (x == tileX1 && y == tileY1) {
                break;
            }
    
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        
        return true;  // not wall between them
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
}
