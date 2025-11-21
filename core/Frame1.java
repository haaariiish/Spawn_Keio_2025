

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;




public class Frame1 extends JFrame{
    private Main_Panel hm1;
    public Frame1(String title){
        super(title);
        setSize(700,700);
        setPreferredSize(new Dimension(700,700));
        setLocation(MAXIMIZED_HORIZ, MAXIMIZED_VERT);

        JButton new_game = new JButton("Start new game"); // bouton de nouvelle partie
        hm1 = new Main_Panel(new_game); // fenetre d'accueil
        add(hm1);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);

        // Même idée que le bouton de nouvelle partie , mais cette fois accessible en jeu
        JMenuItem menuItem = new JMenuItem("New Game",KeyEvent.VK_1);
        menu.add(menuItem);

        // changer des paramètres comme la musique ou la couleur des pions  
        JMenuItem menuItem2 = new JMenuItem("Back to home screen",KeyEvent.VK_3);
        menu.add(menuItem2);
        // menuItem2.addActionListener(new QuitGame(this));

        // Bouton supplémentaire pour quitter le jeu
        JMenuItem menuItem3 = new JMenuItem("Quit",KeyEvent.VK_4);
        menu.add(menuItem3);
        menuItem3.addActionListener(new Close());

        // Menu d'option
        JMenu menu2 = new JMenu("Option");
        menu2.setMnemonic(KeyEvent.VK_B);

    

        menuBar.add(menu);
        menuBar.add(menu2);
        setJMenuBar(menuBar);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
