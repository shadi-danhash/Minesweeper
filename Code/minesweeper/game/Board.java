package minesweeper.game;

import minesweeper.player.Move;
import minesweeper.player.Player;

import java.io.Serializable;
import java.util.ArrayList;

import static sample.Main.log;

public class Board implements Serializable {
    public Square[][] squares;
    private BoardRules boardRules;
    ArrayList<Square> FlyingShields = new ArrayList<>();

    Integer getNumberOfMinesArround(int I, int J) {
        int sum = 0;
        for (int i = -1; i <= +1; i++)
            for (int j = -1; j <= +1; j++) {
                if (i == j && j == 0 || !isValid(I + i, J + j)) continue;
                if (squares[I + i][J + j].getMine()) sum++;
            }
        return sum;
    }

    public Board(BoardRules boardRules, Integer rows, Integer columns) {
        squares = new Square[rows][columns];
        this.boardRules = boardRules;
    }

    public Board(Board board) {
        squares = new Square[board.squares.length][board.squares[0].length];
        FlyingShields = new ArrayList<>();
        for (Square square : board.FlyingShields) FlyingShields.add(new Square(square));
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                squares[i][j] = new Square(board.squares[i][j]);
            }
        }
        boardRules = board.boardRules;
    }

    void setBoardRules(BoardRules boardRules) {
        this.boardRules = boardRules;
    }

    void setSqaure(int i, int j, Square square) {
        squares[i][j] = square;
    }

    Boolean isValid(int i, int j) {
        return !(i < 0 || j < 0 || i >= squares.length || j >= squares[0].length);
    }


    //to check if there is squares dont have mine
    Boolean noMinesLeft() {
        for (Square[] _squares : squares)
            for (Square square : _squares)
                if (!square.getOpened() && !square.getMine()) return false;
        return true;
    }

    private Boolean noEmptySquareLeft() {
        for (Square[] square : squares)
            for (int j = 0; j < squares[0].length; j++)
                if (square[j].getClosed() && !square[j].getMine()) return false;
        return true;
    }

    void replaceMineWithAnotherSquare(int x, int y) {
        if (!squares[x][y].getMine()) return;
        for (int i = 0; i < squares.length; i++)
            for (int j = 0; j < squares[0].length; j++) {
                if (!squares[i][j].getMine()) {
                    Square temp = squares[i][j];
                    squares[i][j] = squares[x][y];
                    squares[x][y] = temp;
                    squares[x][y].setX(x);
                    squares[x][y].setY(y);
                    squares[i][j].setX(i);
                    squares[i][j].setY(j);
                    BoardGenerator.initializeValue(this, squares.length, squares[0].length);
                    return;
                }
            }

    }

    Integer makeMove(Player player, Move move) throws Exception {
        Square square = squares[move.getPosition().getX()][move.getPosition().getY()];
        boardRules.setBoard(this);
        if (square.getOpened()) throw GameExceptions.CANNOT_DO_ACTION_ON_OPENED_SQUARE;
        if (square.getMarked() && move.getMoveType() == Move.MoveType.OPEN)
            throw GameExceptions.CANNOT_DO_ACTION_ON_FLAGED_SQUARE;
        player.addMove(move);
        if (move.getMoveType() == Move.MoveType.FLAG) {
            square.setLastPlayer(player);
            return boardRules.MarkedSquare(player, square);
        }
        Integer score = boardRules.ClickedSquare(player, square);
        square.setLastPlayer(player);
        // flood fill
//        if (square.getEmpty())
//            score += floodFill(player, square.getX(), square.getY());
        // if last empty square
        if (noEmptySquareLeft())
            score += 100;
        return score;
    }

    public void shuffleShields() {
        log(FlyingShields.size() + "");
        for (int indx = 0; indx < FlyingShields.size(); indx++) {
            try {
                int x = FlyingShields.get(indx).getX();
                int y = FlyingShields.get(indx).getY();
                if (FlyingShields.get(indx).getOpened()) {
                    FlyingShields.remove(indx);
                    continue;
                }
                boolean breaked = false;
                for (int i = -1; i <= 1 && !breaked; i++)
                    for (int j = -1; j <= 1; j++)
                        if (isValid(x + i, y + j) && squares[x + i][y + j].getShieldType() == Square.Shield.NoShield
                                && !squares[x + i][y + j].getMine() && squares[x + i][y + j].getClosed()) {
                            FlyingShields.get(indx).setShieldType(Square.Shield.NoShield);
                            squares[x + i][y + j].setShieldType(Square.Shield.Flying);
                            FlyingShields.remove(indx);
                            FlyingShields.add(squares[x + i][y + j]);
                            log(indx + " " + FlyingShields.get(indx));
                            breaked = true;
                            break;
                        }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
