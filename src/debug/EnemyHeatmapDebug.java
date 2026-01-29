package debug;



import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import entities.Enemy;

public class EnemyHeatmapDebug extends JFrame {
    
    private int mapWidth;
    private int mapHeight;
    private int cellSize;
    private int pixelSize = 10;
    
    private BufferedImage heatmapImage;
    private JLabel imageLabel;
    private int[][] densityGrid;
    
    public EnemyHeatmapDebug(int mapWidth, int mapHeight, int cellSize) {
        super("Enemy Heatmap Debug");
        
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.cellSize = cellSize;
        this.densityGrid = new int[mapHeight][mapWidth];
        
        // Initialiser l'image
        heatmapImage = new BufferedImage(
            mapWidth * pixelSize, 
            mapHeight * pixelSize, 
            BufferedImage.TYPE_INT_RGB
        );
        
        // Configurer la fenêtre
        imageLabel = new JLabel(new ImageIcon(heatmapImage));
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
    }
    
    /**
     * Méthode à appeler à chaque frame pour mettre à jour la heatmap avec les ennemis
     */
    public void update(int[][] dist_ToPlayer, List<Enemy> enemies) {
        // Copier les valeurs
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (x < dist_ToPlayer.length && y < dist_ToPlayer[x].length) {
                    densityGrid[y][x] = dist_ToPlayer[y][x];
                } else {
                    densityGrid[y][x] = Integer.MAX_VALUE;
                }
            }
        }

        // Trouver la distance minimale et maximale
        int minDistance = Integer.MAX_VALUE;
        int maxDistance = 0;

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int dist = densityGrid[y][x];
                if (dist < 1000) {
                    minDistance = Math.min(minDistance, dist);
                    maxDistance = Math.max(maxDistance, dist);
                }
            }
        }

        // Redessiner la heatmap avec les ennemis par-dessus
        renderHeatmap(minDistance, maxDistance, enemies);

        // Rafraîchir l'affichage
        imageLabel.repaint();
    }

    private void renderHeatmap(int minDistance, int maxDistance, List<Enemy> enemies) {
        Graphics2D g2d = heatmapImage.createGraphics();

        // Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int range = maxDistance - minDistance;
        if (range == 0) range = 1;

        // 1. Dessiner la heatmap de distance en fond
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int distance = densityGrid[y][x];

                Color color;
                if (distance >= 1000) {
                    color = Color.BLACK;
                } else {
                    float normalizedDist = (float)(distance - minDistance) / range;
                    color = getHeatColor(normalizedDist);
                }

                g2d.setColor(color);
                g2d.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);

                // Grille
                g2d.setColor(new Color(100, 100, 100, 50));
                g2d.drawRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
            }
        }

        // 2. Calculer et afficher la densité d'ennemis par case (optionnel)
        int[][] enemyDensity = new int[mapWidth][mapHeight];
        for (Enemy enemy : enemies) {
            int gridX = (int) (enemy.getX() / cellSize);
            int gridY = (int) (enemy.getY() / cellSize);
            
            if (gridX >= 0 && gridX < mapWidth && gridY >= 0 && gridY < mapHeight) {
                enemyDensity[gridX][gridY]++;
            }
        }

        // Afficher le nombre d'ennemis par case (en petit)
        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (enemyDensity[x][y] > 0) {
                    g2d.setColor(Color.YELLOW);
                    String text = String.valueOf(enemyDensity[x][y]);
                    g2d.drawString(text, x * pixelSize + 2, y * pixelSize + 10);
                }
            }
        }

        // 3. Dessiner les ennemis individuels en points rouges PAR-DESSUS
        for (Enemy enemy : enemies) {
            // Convertir la position du monde en pixels de la heatmap
            int pixelX = (int) ((enemy.getX() / cellSize) * pixelSize);
            int pixelY = (int) ((enemy.getY() / cellSize) * pixelSize);
            
            // Dessiner un point rouge pour chaque ennemi
            g2d.setColor(Color.RED);
            int pointSize = Math.max(3, pixelSize / 3); // Taille du point adaptative
            g2d.fillOval(pixelX - pointSize/2, pixelY - pointSize/2, pointSize, pointSize);
            
            // Contour blanc pour mieux voir
            g2d.setColor(Color.WHITE);
            g2d.drawOval(pixelX - pointSize/2, pixelY - pointSize/2, pointSize, pointSize);
        }

        g2d.dispose();
    }
    
    private Color getHeatColor(float intensity) {
        if (intensity == 0) return new Color(20, 20, 40);
        
        if (intensity < 0.5f) {
            float t = intensity * 2;
            return new Color((int)(t * 255), (int)(t * 255), (int)((1-t) * 255));
        } else {
            float t = (intensity - 0.5f) * 2;
            return new Color(255, (int)((1-t) * 255), 0);
        }
    }
    
    public void setPixelSize(int size) {
        this.pixelSize = size;
        heatmapImage = new BufferedImage(
            mapWidth * pixelSize, 
            mapHeight * pixelSize, 
            BufferedImage.TYPE_INT_RGB
        );
        imageLabel.setIcon(new ImageIcon(heatmapImage));
    }
}