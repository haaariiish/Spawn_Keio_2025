

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




public class Frame1 extends JFrame{
    private Main_Panel hm1;
    public Frame1(String title){
        super(title);
        setSize(700,700);
        setPreferredSize(new Dimension(700,700));
        setLocation(MAXIMIZED_HORIZ, MAXIMIZED_VERT);


        hm1 = new Main_Panel(); // fenetre d'accueil
        add(hm1);

        /* Game Menu bar */
        Gamemenubar menuBar = new Gamemenubar();
        this.setJMenuBar(menuBar);
  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Main_Panel getHomePanel() {
        return hm1;
    }
}
