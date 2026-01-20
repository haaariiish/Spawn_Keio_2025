package rendering;

import core.Frame1;


import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;


public class Wave_Loading_Panel extends JPanel{
    private Font[] CacheFont = new Font[100];
    private AlphaComposite[] alphaComposites= new AlphaComposite[100];
    private Frame1 mainFrame;
    private int progress = 0;
    
    private String currentTask = "";
    

    public Wave_Loading_Panel(Frame1 mainFrame){
        this.mainFrame = mainFrame;
        
        initialiseCacheFont();
    }

    public void initialiseCacheFont(){
        for(int i=0;i<100;i++){
            float percentage = ((float) i )/(100);
            CacheFont[i] = new Font("Arial", Font.BOLD, 2*(i+1));
            alphaComposites[i]= AlphaComposite.getInstance(AlphaComposite.SRC_OVER, percentage);
        }
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
        g2d.setFont(CacheFont[progress]);
        g2d.setComposite(alphaComposites[progress]);
        String text =  currentTask;
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text , (getWidth()/2 - textWidth) , y - 20);

    }

}
