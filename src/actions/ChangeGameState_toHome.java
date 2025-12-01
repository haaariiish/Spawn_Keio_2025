package actions;
import core.Game;
import core.GameState;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ChangeGameState_toHome implements ActionListener {
    private Game game;

    public ChangeGameState_toHome(Game game) {
        this.game = game;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        System.out.println("Changing game state to: Home");
        game.changeGameState(GameState.HOME);
        game.reset();
        if (game.getPreviousGameState() == GameState.PLAYING) {
            // Reset main panel when returning to home from playing state
            game.getFrame().getGamePanel().reset();
        }
    }
    
}

