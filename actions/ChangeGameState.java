package actions;


import core.Game;
import core.GameState;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ChangeGameState implements ActionListener {
    private Game game;

    public ChangeGameState(Game game) {
        this.game = game;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        System.out.println("Changing game state to: Playing");
        game.changeGameState(GameState.PLAYING);
    }
    
}
