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

import javax.swing.SwingConstants;
import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import rendering.Main_Panel;
import rendering.Gamemenubar;




public class Frame1 extends JFrame{
    private Main_Panel hm1;
    private Game game;
    
    public Frame1(String title, Game game) {
        super(title);
        this.game = game;
        setSize(700,700);
        setPreferredSize(new Dimension(700,700));
        setLocation(MAXIMIZED_HORIZ, MAXIMIZED_VERT);


        this.hm1 = new Main_Panel(this); // fenetre d'accueil
        add(this.hm1);

        /* Game Menu bar */
        Gamemenubar menuBar = new Gamemenubar();
        this.setJMenuBar(menuBar);
  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Main_Panel getHomePanel() {
        return this.hm1;
    }

    public Game getGame() {
        return this.game;
    }
}
