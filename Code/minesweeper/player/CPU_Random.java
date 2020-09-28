package minesweeper.player;

import minesweeper.game.Board;
import minesweeper.ui.UserInterface;

public class CPU_Random extends CPU_Player {
    public CPU_Random(String Name, String Color) {
        super(Name, Color);
    }

    @Override
    public Move makeMove(Board board, UserInterface userInterface) throws Exception {
        Wait();
        return RandomMove(board);
    }
}
