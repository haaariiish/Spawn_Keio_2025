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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Pause_Menu_Panel extends JPanel {
    private Frame1 mainFrame;

    public Pause_Menu_Panel(Frame1 mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
    }

    private void initUI() {
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 200));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = createLabel("Pause", 48);
        JLabel subtitle = createLabel("Le jeu est en pause", 20);

        JButton resumeButton = new JButton("Reprendre");
        resumeButton.setAlignmentX(CENTER_ALIGNMENT);
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.getGame().resumeGame();
            }
        });

        JButton restartButton = new JButton("Restart");
        restartButton.setAlignmentX(CENTER_ALIGNMENT);
        restartButton.addActionListener(new ChangeGameState_toPlay(mainFrame.getGame()));

        JButton homeButton = new JButton("Menu Principal");
        homeButton.setAlignmentX(CENTER_ALIGNMENT);
        homeButton.addActionListener(new ChangeGameState_toHome(mainFrame.getGame()));

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 16)));
        add(subtitle);
        add(Box.createRigidArea(new Dimension(0, 32)));
        add(resumeButton);
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(restartButton);
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
}

