package rendering;


import actions.ChangeGameState_toHome;
import actions.ChangeGameState_toLoading;
import core.Frame1;
import entities.Player;

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



public class Level_Up_Menu extends JPanel{
    private Frame1 mainFrame;
    private Player player;
    public Level_Up_Menu(Frame1 mainFrame1,Player player){
        mainFrame= mainFrame1;
        this.player=player;
        initUI();
    }

    public void initUI(){
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 200));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel title = createLabel("Level UP :", 48);

        return;
    }

    private JLabel createLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        return label;
    }

    
}
