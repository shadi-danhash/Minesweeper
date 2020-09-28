package minesweeper.player;

import minesweeper.game.Board;
import minesweeper.game.Square;
import minesweeper.ui.UserInterface;

public class CPU_NoLose extends CPU_Player {
    public CPU_NoLose(String Name, String Color) {
        super(Name, Color);
    }
    @Override
    public Move makeMove(Board board, UserInterface userInterface) throws Exception
    {
        Wait();
        //flag mine
        for(int i=0;i<board.squares.length;i++)
            for(int j=0;j<board.squares[0].length;j++)
                if(board.squares[i][j].getMine() && !board.squares[i][j].getMarked() && board.squares[i][j].getClosed())
                    return new Move(Move.MoveType.FLAG,new Square(i,j));
        //open square without mine
        for (int i=0;i<board.squares.length;i++)
            for (int j=0;j<board.squares[0].length;j++)
                if(board.squares[i][j].getClosed() && !board.squares[i][j].getMine() && !board.squares[i][j].getMarked())
                    return new Move(Move.MoveType.OPEN,new Square(i,j));
        //if there is only marked and not mined squares
        for (int i=0;i<board.squares.length;i++)
            for (int j=0;j<board.squares[0].length;j++)
                if(board.squares[i][j].getClosed() && !board.squares[i][j].getMine() && board.squares[i][j].getMarked())
                    return new Move(Move.MoveType.FLAG,new Square(i,j));
        return RandomMove(board);
    }
}
