package rendering;

import core.Frame1;
import actions.ChangeGameState_toPlay;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.FontMetrics;

public class Home_Menu_Panel extends JPanel {
    private JButton new_game;
    private Frame1 mainFrame;
    private final Font font50 = new Font("Monospaced", Font.PLAIN, 50);
    private final Font font20 = new Font("Monospaced", Font.PLAIN, 20);
    private final Color dark_gray = new Color(128,128,128);

    public Home_Menu_Panel(Frame1 mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(null);
        this.new_game = new Custom_Button("New Game");
        this.new_game.setBounds(mainFrame.getWidth()/2-150,mainFrame.getHeight()/10 + 130,300,50);
        
        this.new_game.addActionListener( new ChangeGameState_toPlay(this.mainFrame.getGame()));
        this.add(new_game);
        
        this.setOpaque(true);
    }

@Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);


    g.setColor(dark_gray);
    g.drawString("The game is opened since "+this.mainFrame.getGame().getOpenTime()+" frames", 10, this.mainFrame.getHeight()- this.mainFrame.getHeight()/5);
    g.setFont(font50);
    FontMetrics metrics = g.getFontMetrics(font50);
    
    int x = Math.max((this.getWidth() - metrics.stringWidth("SPAWN")) / 2, 0);
    g.drawString("SPAWN", x, mainFrame.getHeight()/10);
    

    g.setFont(font20);
    metrics = g.getFontMetrics(font20);
    x = Math.max((this.getWidth()- metrics.stringWidth("For Computer Science Exercice Class")) / 2,0);
    g.drawString("For Computer Science Exercice Class", x,  mainFrame.getHeight()/10 + 50);
    x = Math.max((this.getWidth()- metrics.stringWidth("By Harish Prabakaran")) / 2,0);
    g.drawString("By Harish Prabakaran", x,  mainFrame.getHeight()/10 + 80);

    }
}
