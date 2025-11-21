

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Close implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Code pour quitter l'application
        System.exit(0);
    }
}