package core;

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
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.smartcardio.Card;
import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import rendering.Main_Panel;
import rendering.Gamemenubar;
import rendering.Home_Menu_Panel;




public class Frame1 extends JFrame{
    private Main_Panel game_panel;
    private Home_Menu_Panel homeMenuPanel;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);
    private Game game;
    
    public Frame1(String title, Game game) {
        super(title);
        this.game = game;
        setSize(700,700);
        setPreferredSize(new Dimension(700,700));
        setLocation(MAXIMIZED_HORIZ, MAXIMIZED_VERT);

        
        this.game_panel = new Main_Panel(this); // game panel
        this.homeMenuPanel = new Home_Menu_Panel(this); // home menu panel

        this.mainContainer.add(this.homeMenuPanel, "HomeMenu");
        this.mainContainer.add(this.game_panel, "GamePanel");
        this.cardLayout.show(this.mainContainer, "HomeMenu");
        add(this.mainContainer);
        /* Game Menu bar */
        Gamemenubar menuBar = new Gamemenubar();
        this.setJMenuBar(menuBar);
  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Main_Panel getGamePanel() {
        return this.game_panel;
    }

    public Home_Menu_Panel getHomeMenuPanel() {
        return this.homeMenuPanel;
    }

    public void showPanel(String panelName) {
        this.cardLayout.show(this.mainContainer, panelName);
    }

    public void refresh() {
        this.revalidate();
        this.repaint();
    }

    public void changeToGamePanel() {
        this.cardLayout.show(this.mainContainer, "GamePanel");
    }

    public void changeToHomeMenuPanel() {
        this.cardLayout.show(this.mainContainer, "HomeMenu");
    }

    
    public Game getGame() {
        return this.game;
    }


}
