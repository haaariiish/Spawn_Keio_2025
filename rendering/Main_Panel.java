package rendering;

import core.Frame1;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;

public class Main_Panel extends JPanel{
    
    private Frame1 mainFrame;
    public Main_Panel(Frame1 mainFrame) { 
        this.mainFrame = mainFrame;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawGameWorld(g);
    
    Toolkit.getDefaultToolkit().sync();
    }

    private void drawGameWorld(Graphics g) {
        // Dessiner le monde du jeu
        g.drawString("Game is Running...", 300, 350);      
    }
}
