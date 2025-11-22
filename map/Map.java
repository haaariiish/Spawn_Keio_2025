package map;

import rendering.Main_Panel;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Map{

    private int Length=1024; 
    private int Height=1024;

    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int DOOR = 2;
    public static final int SPIKE = 3;
    public static final int WATER = 4;
    public static final int SPAWN = 5;
    
    private static final Color COLOR_EMPTY = new Color(240, 240, 240);
    private static final Color COLOR_WALL = new Color(80, 80, 80);
    private static final Color COLOR_DOOR = new Color(139, 69, 19);
    private static final Color COLOR_SPIKE = Color.RED;
    private static final Color COLOR_WATER = new Color(100, 149, 237);
    private static final Color COLOR_SPAWN = new Color(144, 238, 144);


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
            return true; // Hors limites = mur solide
        }
        return tiles[tileY][tileX] == WALL;
    }

    public boolean isWallAtPixel(int pixelX, int pixelY) {
        return isWall(pixelX / tileSize, pixelY / tileSize);
    }

    public boolean collidesWithWall(int x, int y, int width, int height) {
        // Vérifier les 4 coins + milieux des côtés. /Corners
        int[][] checkPoints = {
            {x, y},                          // Coin haut-gauche
            {x + width, y},                  // Coin haut-droit
            {x, y + height},                 // Coin bas-gauche
            {x + width, y + height},         // Coin bas-droit
            {x + width/2, y},                // Milieu haut
            {x + width/2, y + height},       // Milieu bas
            {x, y + height/2},               // Milieu gauche
            {x + width, y + height/2}        // Milieu droit
        };
        
        for (int[] point : checkPoints) {
            if (isWallAtPixel(point[0], point[1])) {
                return true;
            }
        }
        return false;
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

    // Utils ------------------------------------------------------------------------------
    private boolean isValidTile(int tileX, int tileY) {
        return tileX >= 0 && tileX < widthInTiles && tileY >= 0 && tileY < heightInTiles;
    }

    public Point getSpawnPoint() {
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                if (tiles[y][x] == SPAWN) {
                    return new Point(x * tileSize, y * tileSize);
                }
            }
        }
        return new Point(tileSize, tileSize); // Par défaut
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
