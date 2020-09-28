package minesweeper.player;


import minesweeper.game.Board;
import minesweeper.ui.UserInterface;

public class HumanPlayer extends Player {

    public HumanPlayer() {
        super("Rita", "Rayan");
    }

    @Override
    public Move makeMove(Board board, UserInterface userInterface) throws Exception {
        return userInterface.waitMove(board);

    }
}
