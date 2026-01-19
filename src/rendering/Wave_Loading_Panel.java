package rendering;

import core.Frame1;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Wave_Loading_Panel extends JPanel{
    private final Font font30 = new Font("Monospaced", Font.PLAIN, 30);
    private Frame1 mainFrame;
    private int progress = 0;
    private String currentTask;

    public Wave_Loading_Panel(Frame1 mainFrame){
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
        
        int y = getHeight() / 2;
        
        // Texte
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, progress));
        String text =  currentTask;
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (getWidth() - textWidth) / 2, y - 20);
    }

}
