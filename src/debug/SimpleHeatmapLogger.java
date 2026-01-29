package debug;

import entities.Enemy;
import java.io.*;
import java.util.List;

public class SimpleHeatmapLogger {
    private PrintWriter writer;
    private int frameCounter = 0;
    private int logInterval = 30; // Logger toutes les 60 frames par défaut
    private int cellSize;
    private int mapWidth;
    private int mapHeight;

    public SimpleHeatmapLogger(String filename, int mapWidth, int mapHeight, int cellSize) throws IOException {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.cellSize = cellSize;

        writer = new PrintWriter(new FileWriter(filename));
        writer.println("frame,gridX,gridY,distance,enemyCount");
        System.out.println("Heatmap logger started: " + filename);
    }

    /**
     * Définir l'intervalle de logging (en frames)
     */
    public void setLogInterval(int interval) {
        this.logInterval = interval;
    }

    /**
     * Appeler à chaque frame
     */
    public void log(int[][] distances, List<Enemy> enemies) {
        frameCounter++;

        // Logger seulement à intervalle régulier
        if (frameCounter % logInterval != 0) return;

        // Calculer la densité d'ennemis
        int[][] density = new int[mapWidth][mapHeight];
        for (Enemy e : enemies) {
            int x = (int)(e.getX() / cellSize);
            int y = (int)(e.getY() / cellSize);
            if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
                density[x][y]++;
            }
        }

        // Écrire les données (seulement les cases avec des ennemis ou des distances valides)
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (density[x][y] > 0 || distances[x][y] < 1000) {
                    writer.printf("%d,%d,%d,%d,%d%n",
                            frameCounter, x, y, distances[x][y], density[x][y]);
                }
            }
        }
        writer.flush(); // Important pour ne pas perdre de données en cas de crash
    }

    /**
     * Fermer le logger (à appeler en fin de partie)
     */
    public void close() {
        if (writer != null) {
            writer.close();
            System.out.println("Heatmap logger closed. Total frames logged: " + frameCounter);
        }
    }

    public int getFrameCount() {
        return frameCounter;
    }
}