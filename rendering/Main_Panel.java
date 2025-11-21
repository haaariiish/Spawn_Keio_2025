package rendering;

import core.Frame1;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;

public class Main_Panel extends JPanel{
    private JButton new_game;
    private Frame1 mainFrame;
    public Main_Panel(Frame1 mainFrame) { 
        this.mainFrame = mainFrame;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    switch (this.mainFrame.getGame().getGameState()) { 
        case HOME:
            drawHomeScreen(g);
            break;
        case PAUSE:
            drawPAUSE(g);
            break;
        case PLAYING:
            drawGameWorld(g);
            break;
        case GAMEOVER:
            drawGameOverScreen(g);
            break;
        case LOADING:
            drawLoading(g);
            break;
    }
    Toolkit.getDefaultToolkit().sync();
    }
    private void drawHomeScreen(Graphics g) {
        // Dessiner l'écran d'accueil
        g.drawString("Welcome to Spawn Keio 2025!", 300, 350);
        
    }
    private void drawPAUSE(Graphics g) {
        // Dessiner l'écran de pause
        g.drawString("Game Paused", 320, 350);      
    }
    private void drawGameWorld(Graphics g) {
        // Dessiner le monde du jeu
        g.drawString("Game is Running...", 300, 350);      
    }
    private void drawGameOverScreen(Graphics g) {
        // Dessiner l'écran de fin de jeu
        g.drawString("Game Over!", 320, 350);      
    }
    private void drawLoading(Graphics g) {
        // Dessiner l'écran de chargement
        g.drawString("Loading...", 320, 350);      
    }
}
