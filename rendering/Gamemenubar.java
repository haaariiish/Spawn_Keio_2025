package rendering;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.KeyEvent;
import actions.Close;


public class Gamemenubar extends JMenuBar {
    private JMenu gameMenu;
    private JMenuItem newGameItem;
    private JMenuItem homeScreenItem;
    private JMenuItem quitItem;

    private JMenu optionMenu;


    public Gamemenubar() {

        /* Game menu setup */
        gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_A);

        newGameItem = new JMenuItem("New Game",KeyEvent.VK_SPACE);
        homeScreenItem =new JMenuItem("Back to home screen",KeyEvent.VK_3);
        quitItem = new JMenuItem("Quit",KeyEvent.VK_4);

        quitItem.addActionListener(new Close());

        gameMenu.add(newGameItem);
        gameMenu.add(homeScreenItem);
        gameMenu.add(quitItem);

        this.add(gameMenu);

        /* Option menu setup */
        optionMenu = new JMenu("Option");
        optionMenu.setMnemonic(KeyEvent.VK_B);
        this.add(optionMenu);
    }
    
}
