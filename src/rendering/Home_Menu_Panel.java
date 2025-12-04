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
    private final Font font30 = new Font("Monospaced", Font.PLAIN, 30);
    private final Font font20 = new Font("Monospaced", Font.PLAIN, 20);
    private final Color dark_gray = new Color(128,128,128);
    private int gap_X = 200; // because I m lazy 

    public Home_Menu_Panel(Frame1 mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(null);
        this.new_game = new Custom_Button("New Game");
        this.new_game.setBounds(mainFrame.getWidth()/2-150,mainFrame.getHeight()/10 + 130,300,50);
        
        this.new_game.addActionListener( new ChangeGameState_toPlay(this.mainFrame.getGame()));
        this.add(new_game);
        
        this.setOpaque(true);
    }


    protected void paintTitle(Graphics g){
        g.setColor(dark_gray);
        g.setFont(font50);
    FontMetrics metrics = g.getFontMetrics(font50);
    g.drawString("Z",mainFrame.getWidth()/2-15,mainFrame.getHeight()/10 + 345);
    g.drawString("S",mainFrame.getWidth()/2-15,mainFrame.getHeight()/10 + 415);
    g.drawString("Q",mainFrame.getWidth()/2-85,mainFrame.getHeight()/10 + 415);
    g.drawString("D",mainFrame.getWidth()/2+55,mainFrame.getHeight()/10 + 415);
    
    int x = Math.max((this.getWidth() - metrics.stringWidth("SPAWN")) / 2, 0);
    g.drawString("SPAWN", x, mainFrame.getHeight()/10);
    

    g.setFont(font20);
    metrics = g.getFontMetrics(font20);
    x = Math.max((this.getWidth()- metrics.stringWidth("For Computer Science Exercice Class")) / 2,0);
    g.drawString("For Computer Science Exercice Class", x,  mainFrame.getHeight()/10 + 50);
    x = Math.max((this.getWidth()- metrics.stringWidth("By Harish Prabakaran")) / 2,0);
    g.drawString("By Harish Prabakaran", x,  mainFrame.getHeight()/10 + 80);
     // INPUT showing
    g.drawRect(mainFrame.getWidth()/2-25,mainFrame.getHeight()/10 + 300,50,50);
    
    g.drawRect(mainFrame.getWidth()/2-25,mainFrame.getHeight()/10 + 370,50,50);
    g.drawRect(mainFrame.getWidth()/2-95,mainFrame.getHeight()/10 + 370,50,50);
    g.drawRect(mainFrame.getWidth()/2+45,mainFrame.getHeight()/10 + 370,50,50);
}

    
@Override

    

    protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(dark_gray);
    g.drawString("The game is opened since "+this.mainFrame.getGame().getOpenTime()+" frames", 10, this.mainFrame.getHeight()- this.mainFrame.getHeight()/10);
    g.setFont(font30);
    FontMetrics metrics = g.getFontMetrics(font30);
    g.drawString("Z",mainFrame.getWidth()/2-75-gap_X,mainFrame.getHeight()/10 + 325);
    g.drawString("W",mainFrame.getWidth()/2-45-gap_X,mainFrame.getHeight()/10 + 348);

    g.drawString("S",mainFrame.getWidth()/2-62-gap_X,mainFrame.getHeight()/10 + 412);
    
    g.drawString("Q",mainFrame.getWidth()/2-145-gap_X,mainFrame.getHeight()/10 + 395);
    g.drawString("A",mainFrame.getWidth()/2-115-gap_X,mainFrame.getHeight()/10 + 418);
    
    g.drawString("D",mainFrame.getWidth()/2+8-gap_X,mainFrame.getHeight()/10 + 412);
    
    g.drawString("ESC",mainFrame.getWidth()/2+170-gap_X,mainFrame.getHeight()/10 + 385);
    
    
    g.setFont(font50);
    metrics = g.getFontMetrics(font50);
    int x = Math.max((this.getWidth()-600 - metrics.stringWidth("INPUT :")) / 2, 0);
    g.drawString("INPUT :",x -gap_X,mainFrame.getHeight()/10 + 370);
    
    
    x = Math.max((this.getWidth() - metrics.stringWidth("SPAWN")) / 2, 0);
    g.drawString("SPAWN", x, mainFrame.getHeight()/10);
    

    g.setFont(font20);
    metrics = g.getFontMetrics(font20);
    x = Math.max((this.getWidth()- metrics.stringWidth("For Computer Science Exercice Class")) / 2,0);
    g.drawString("For Computer Science Exercice Class", x,  mainFrame.getHeight()/10 + 50);
    x = Math.max((this.getWidth()- metrics.stringWidth("By Harish Prabakaran")) / 2,0);
    g.drawString("By Harish Prabakaran", x,  mainFrame.getHeight()/10 + 80);
     // INPUT showing
    
    g.drawRect(mainFrame.getWidth()/2-75-gap_X,mainFrame.getHeight()/10 + 300,50,50);
    g.drawLine(mainFrame.getWidth()/2-75-gap_X,mainFrame.getHeight()/10 + 350,mainFrame.getWidth()/2-25-gap_X, mainFrame.getHeight()/10 + 300);
    g.drawRect(mainFrame.getWidth()/2-75-gap_X,mainFrame.getHeight()/10 + 370,50,50);
    g.drawLine(mainFrame.getWidth()/2-145-gap_X, mainFrame.getHeight()/10 + 420,mainFrame.getWidth()/2-95-gap_X,mainFrame.getHeight()/10+370);
    g.drawRect(mainFrame.getWidth()/2-145-gap_X,mainFrame.getHeight()/10 + 370,50,50);
    g.drawRect(mainFrame.getWidth()/2-5-gap_X,mainFrame.getHeight()/10 + 370,50,50);

    // Walking
    g.drawString("UP", mainFrame.getWidth()/2-57-gap_X, mainFrame.getHeight()/10 + 290);
    g.drawString("DOWN", mainFrame.getWidth()/2-75-gap_X, mainFrame.getHeight()/10 + 450);
    g.drawString("LEFT", mainFrame.getWidth()/2-145-gap_X, mainFrame.getHeight()/10 + 450);
    g.drawString("RIGHT", mainFrame.getWidth()/2-5-gap_X, mainFrame.getHeight()/10 + 450);
    
    // Arrows
    g.drawString("   OR    ARROWS", mainFrame.getWidth()/2+520-gap_X,mainFrame.getHeight()/10 + 400);
    g.drawString("AIM AND SHOOT",mainFrame.getWidth()/2+490-gap_X,mainFrame.getHeight()/10 + 250);

    // Pause text
    g.drawRect(mainFrame.getWidth()/2+160-gap_X,mainFrame.getHeight()/10 + 350,80,50);
    g.drawString("PAUSE",mainFrame.getWidth()/2+170-gap_X,mainFrame.getHeight()/10 + 340);

    //MOUSE Design
    g.drawRect(mainFrame.getWidth()/2+400-gap_X,mainFrame.getHeight()/10 + 300,100,200);
    g.drawLine(mainFrame.getWidth()/2+400-gap_X,mainFrame.getHeight()/10 + 366,mainFrame.getWidth()/2+500-gap_X,mainFrame.getHeight()/10 + 366);
    g.fillRect(mainFrame.getWidth()/2+400-gap_X,mainFrame.getHeight()/10 + 300,50,66);

    

    
}

   

}
