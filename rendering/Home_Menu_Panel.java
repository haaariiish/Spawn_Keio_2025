package rendering;

import core.Frame1;
import actions.ChangeGameState_toPlay;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;

public class Home_Menu_Panel extends JPanel {
    private JButton new_game;
    private Frame1 mainFrame;
    public Home_Menu_Panel(Frame1 mainFrame) {
        this.mainFrame = mainFrame;
        this.new_game = new JButton("New Game");
        this.new_game.addActionListener( new ChangeGameState_toPlay(this.mainFrame.getGame()));
        this.add(new_game);
    }

@Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawString("The game is opened since "+this.mainFrame.getGame().getOpenTime()+" frames", this.mainFrame.getWidth()/2, this.mainFrame.getHeight()/2);
    }
}
