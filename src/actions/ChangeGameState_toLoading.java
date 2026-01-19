package actions;
import core.Game;
import core.GameState;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ChangeGameState_toLoading implements ActionListener {
    private Game game;

    public ChangeGameState_toLoading(Game game) {
        this.game = game;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Changing game state to: Playing by loading");
        game.changeGameState(GameState.LOADING);
        game.reset();
    }
    
}
