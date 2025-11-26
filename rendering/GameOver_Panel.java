package rendering;

import actions.ChangeGameState_toHome;
import actions.ChangeGameState_toPlay;
import core.Frame1;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class GameOver_Panel extends JPanel {
    private Frame1 mainFrame;
    private JLabel scoreLabel;
    private JLabel timeLabel;

    public GameOver_Panel(Frame1 mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
    }

    private void initUI() {
        setOpaque(true);
        setBackground(new Color(30, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = createLabel("Game Over", 48);
        JLabel subtitle = createLabel("Vous avez été vaincu", 20);
        scoreLabel = createLabel("Score : 0", 24);
        timeLabel = createLabel("Temps : 0s", 24);

        JButton retryButton = new JButton("Réessayer");
        retryButton.setAlignmentX(CENTER_ALIGNMENT);
        retryButton.addActionListener(new ChangeGameState_toPlay(mainFrame.getGame()));

        JButton homeButton = new JButton("Menu Principal");
        homeButton.setAlignmentX(CENTER_ALIGNMENT);
        homeButton.addActionListener(new ChangeGameState_toHome(mainFrame.getGame()));

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(subtitle);
        add(Box.createRigidArea(new Dimension(0, 24)));
        add(scoreLabel);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(timeLabel);
        add(Box.createRigidArea(new Dimension(0, 32)));
        add(retryButton);
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(homeButton);
        add(Box.createVerticalGlue());
    }

    private JLabel createLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        return label;
    }

    public void refreshStats() {
        int score = 0;
        int timeSeconds = 0;
        if (mainFrame.getGame().getGameWorld() != null) {
            score = mainFrame.getGame().getGameWorld().getScore();
        }
        timeSeconds = mainFrame.getGame().getInGameTime() / 1000;
        scoreLabel.setText("Score : " + score);
        timeLabel.setText("Temps : " + timeSeconds + "s");
    }
}

