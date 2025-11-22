package rendering;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.KeyEvent;

import actions.ChangeGameState_toPlay;
import actions.ChangeGameState_toHome;
import actions.Close;
import core.Frame1;


public class Gamemenubar extends JMenuBar {
    private Frame1 mainframe;
    private JMenu gameMenu;
    private JMenuItem newGameItem;
    private JMenuItem homeScreenItem;
    private JMenuItem quitItem;
    private JMenu optionMenu;


    public Gamemenubar(Frame1 frame) {
        this.mainframe = frame;

        /* Game menu setup */
        this.gameMenu = new JMenu("Game");
        this.gameMenu.setMnemonic(KeyEvent.VK_A);

        this.newGameItem = new JMenuItem("New Game",KeyEvent.VK_SPACE);
        this.homeScreenItem =new JMenuItem("Back to home screen",KeyEvent.VK_3);
        this.quitItem = new JMenuItem("Quit",KeyEvent.VK_4);

        this.newGameItem.addActionListener( new ChangeGameState_toPlay(this.mainframe.getGame()));
        this.homeScreenItem.addActionListener( new ChangeGameState_toHome(this.mainframe.getGame()));
        this.quitItem.addActionListener(new Close());

        this.gameMenu.add(newGameItem);
        this.gameMenu.add(homeScreenItem);
        this.gameMenu.add(quitItem);

        this.add(gameMenu);

        /* Option menu setup */
        this.optionMenu = new JMenu("Option");
        this.optionMenu.setMnemonic(KeyEvent.VK_B);
        this.add(optionMenu);
    }
    
}
