package minesweeper.game;

import java.util.Random;

import static sample.Main.log;

public class RandomBoardGenerator extends BoardGenerator {
    @Override
    public Board generate(int row,int colw,int mines,int shields, int flyingShields) {
        {
            Random rand=new Random();
            Board board = new Board(new DefaultBoardRules(), row, colw);
            for (int i = 0; i < row; i++)
                for (int j = 0; j < colw; j++)
                    board.setSqaure(i, j, new Square(i, j));
            for (int i = 0; i < mines; i++)
            {
                int x,y;
                do {
                    x = rand.nextInt(row);
                    y = rand.nextInt(colw);
                }while (board.squares[x][y].getMine());
                board.squares[x][y] = new Square(x, y, true);
            }
            for (int i = 0 ;i < shields ;) {
                try {
                    int x  = rand.nextInt(row);
                    int y = rand.nextInt(colw);
                    if (board.squares[x][y].getShieldType()==Square.Shield.NoShield) {
                        board.squares[x][y].setShieldType(Square.Shield.Normal);
                        i++;
                    }
                }
                catch(Exception ignored) {}
            }
            for (int i= 0 ;i < flyingShields;) {
                try {
                    int x  = rand.nextInt(row);
                    int y = rand.nextInt(colw);
                    if (board.squares[x][y].getShieldType()==Square.Shield.NoShield) {
                        board.squares[x][y].setShieldType(Square.Shield.Flying);
                        i++;
                    }
                }
                catch(Exception ignored) {}
            }
            initializeValue(board, row, colw);
            return board;
        }
    }
}
