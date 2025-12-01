package actions;
import core.Game;
import core.GameState;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ChangeGameState_toPlay implements ActionListener {
    private Game game;

    public ChangeGameState_toPlay(Game game) {
        this.game = game;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Changing game state to: Playing");
        game.changeGameState(GameState.PLAYING);
        game.reset();
    }
    
}
