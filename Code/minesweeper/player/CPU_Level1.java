package minesweeper.player;

import minesweeper.game.Board;
import minesweeper.game.Point;
import minesweeper.game.Square;
import minesweeper.ui.UserInterface;
//import java.awt.*;
import java.util.Arrays;

public class CPU_Level1 extends CPU_Player {
    private Integer[][] DangerBoard;
    private Boolean[][] FlagBoard;
    private Integer[][] NumberBoard;

    public CPU_Level1(String Name, String Color) {
        super(Name, Color);
    }

    //to know number of closed squares around i,j
    private Integer getClosedAround(Board board, int x, int y) {
        Integer c = 0;
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if (x + i < 0 || x + i >= board.squares.length || y + j < 0 || y + j >= board.squares[0].length)
                    continue;
                if (!board.squares[x + i][y + j].getOpened())
                    c++;
                    ////ADDED
                else if (board.squares[x + i][y + j].getMine() && board.squares[x + i][y + j].getOpened())
                    c++;
            }
        return c;
    }

    //to mark FlagBoard on closed squares around i,j
    private void MarkAround(Integer x, Integer y, Board board) {
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if (x + i < 0 || x + i >= board.squares.length || y + j < 0 || y + j >= board.squares[0].length)
                    continue;
                if (!board.squares[x + i][y + j].getOpened())
                    FlagBoard[x + i][y + j] = true;
            }
    }

    private void UpdateNumberBoard(Board board) {
        for (int i = 0; i < board.squares.length; i++)
            for (int j = 0; j < board.squares[0].length; j++) {
                NumberBoard[i][j] = 0;
            }
        for (int i = 0; i < board.squares.length; i++)
            for (int j = 0; j < board.squares[0].length; j++) {
                if (board.squares[i][j].getOpened() && !board.squares[i][j].getEmpty()) {
                    if (!board.squares[i][j].getMine())
                        NumberBoard[i][j] = board.squares[i][j].getNumber();
                    //else
                } else
                    NumberBoard[i][j] = 0;
            }
        for (int i = 0; i < board.squares.length; i++)
            for (int j = 0; j < board.squares[0].length; j++)
                if (FlagBoard[i][j])
                    MinusFromNumberBoard(i, j, board);
    }

    private void MinusFromNumberBoard(int x, int y, Board board) {
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if (x + i < 0 || x + i >= board.squares.length || y + j < 0 || y + j >= board.squares[0].length)
                    continue;
                if (!board.squares[x + i][y + j].getEmpty() && board.squares[x + i][y + j].getOpened())
                    NumberBoard[x + i][y + j]--;
            }
    }

    //to know if squares around i,j are realy mines
    private void UpdateFlageBoard(Board board) {


        for (int i = 0; i < board.squares.length; i++) {
            for (int j = 0; j < board.squares[0].length; j++) {
                FlagBoard[i][j] = false;
            }
        }
        for (int i = 0; i < board.squares.length; i++) {
            for (int j = 0; j < board.squares[0].length; j++) {
                if (board.squares[i][j].getOpened() && !board.squares[i][j].getEmpty()/*??true condition??*/) {
                    Integer ClosedAround;
                    ClosedAround = getClosedAround(board, i, j);
                    if (ClosedAround.equals(board.squares[i][j].getNumber()) && !board.squares[i][j].getMarked())
                        MarkAround(i, j, board);
                }
            }
        }
    }

    private Integer getNumberAround(Board board, int x, int y) {
        int c = 0;
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if (x + i < 0 || x + i >= board.squares.length || y + j < 0 || y + j >= board.squares[0].length)
                    continue;
                if (board.squares[x + i][y + j].getOpened() && !board.squares[x + i][y + j].getEmpty())
                    c += NumberBoard[x + i][y + j] == 0 ? 0 : 1;//or c+=board.squares[x+i][y+j].getNumber()??
            }
        return c;
    }

    private void UpdateDangerBoard(Board board) {
        for (int i = 0; i < board.squares.length; i++)
            for (int j = 0; j < board.squares[0].length; j++) {
                DangerBoard[i][j] = 0;
            }
        for (int i = 0; i < DangerBoard.length; i++)
            for (int j = 0; j < DangerBoard[0].length; j++) {
                if (board.squares[i][j].getOpened())
                    DangerBoard[i][j] = -1;
                else if (board.squares[i][j].getMarked())
                    DangerBoard[i][j] = -2;
                else
                    DangerBoard[i][j] = getNumberAround(board, i, j);
            }
    }

    private Point getBestPoint(Board board) {
        Point point = null;
        Integer min = 10000000;
        for (int i = 0; i < board.squares.length; i++)
            for (int j = 0; j < board.squares[0].length; j++)
                if (DangerBoard[i][j] < min && DangerBoard[i][j] >= 0) {
                    min = DangerBoard[i][j];
                    point = new Point(i, j);
                }
        return point;
    }

    @Override
    public Move makeMove(Board board, UserInterface userInterface) throws Exception {
        /*
        FIRST Move
         */
        Wait();
        NumberBoard = new Integer[board.squares.length][board.squares[0].length];
        DangerBoard = new Integer[board.squares.length][board.squares[0].length];
        FlagBoard = new Boolean[board.squares.length][board.squares[0].length];
        UpdateFlageBoard(board);
        //flag mine
        for (int i = 0; i < FlagBoard.length; i++)
            for (int j = 0; j < FlagBoard[0].length; j++)
                if (FlagBoard[i][j] && !board.squares[i][j].getMarked() && !board.squares[i][j].getOpened())
                    return new Move(Move.MoveType.FLAG, new Square(i, j));
        UpdateNumberBoard(board);
        UpdateDangerBoard(board);
        //open square
        Point point = getBestPoint(board);

        if (point != null) {
            return new Move(Move.MoveType.OPEN, new Square(point.getX(), point.getY()));
        }
        //if no move returned
        return RandomMove(board);
    }

}
