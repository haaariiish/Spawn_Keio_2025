import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

public class Home_screen extends JPanel{
    public Home_screen(JButton new_game) {
        // Implementation of the home screen
        new_game.setBounds(250, 300, 200, 50);
        this.add(new_game);
    }
    
}
