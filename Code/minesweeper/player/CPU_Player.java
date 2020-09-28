package minesweeper.player;

import minesweeper.game.Board;
import minesweeper.game.Square;
import minesweeper.ui.UserInterface;

import java.util.Random;

public abstract class CPU_Player extends Player {
    CPU_Player(String Name, String Color) {
        super(Name, Color);
    }

    public Move makeMove(Board board, UserInterface userInterface) throws Exception {
        return null;
    }

    Move RandomMove(Board board) {
        Move.MoveType m[] = new Move.MoveType[2];
        m[0] = Move.MoveType.FLAG;
        m[1] = Move.MoveType.OPEN;
        int RandomMoveType, x, y;
        while (true) {
            Random random = new Random();
            RandomMoveType = random.nextInt(2);
            x = random.nextInt(board.squares.length);
            y = random.nextInt(board.squares[0].length);
            //ADDED FOR TEST ==> move type only click/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*
            if (!board.squares[x][y].getOpened()) {
                if (m[RandomMoveType] == Move.MoveType.FLAG)
                    break;
                if (m[RandomMoveType] == Move.MoveType.OPEN && !board.squares[x][y].getMarked())
                    break;
            }
        }
        return new Move(m[RandomMoveType], new Square(x, y));
    }

    void Wait() throws InterruptedException {
        Thread.sleep(3000);
    }
}
