package rendering;

import core.Frame1;
import map.Map;
import entities.Player;
import entities.Projectiles;

import javax.swing.JPanel;
import javax.swing.LookAndFeel;

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

public class Loading_Panel extends JPanel{

    private final Font font30 = new Font("Monospaced", Font.PLAIN, 30);
    private Frame1 mainFrame;
    private int progress = 0;
    private String currentTask = "Chargement...";

    public Loading_Panel(Frame1 mainFrame){
        this.mainFrame = mainFrame;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
    
    public void setCurrentTask(String task) {
        this.currentTask = task;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        
        // Barre de progression
        int barWidth = 400;
        int barHeight = 30;
        int x = (getWidth() - barWidth) / 2;
        int y = getHeight() / 2;
        
        // Contour
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, barWidth, barHeight);
        
        // Remplissage
        g2d.setColor(Color.BLACK);
        int fillWidth = (barWidth * progress) / 100;
        g2d.fillRect(x , y , fillWidth , barHeight );
        
        // Texte
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String text = progress + "% - " + currentTask;
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (getWidth() - textWidth) / 2, y - 20);
    }
}
