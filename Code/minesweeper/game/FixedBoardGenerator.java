package minesweeper.game;

public class FixedBoardGenerator extends BoardGenerator {
    @Override
    public Board generate(int row, int colw, int mines, int shields , int flyingShields) {
        int N = 9;
        Board board = new Board(new DefaultBoardRules(), N, N);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                board.setSqaure(i, j, new Square(i, j));
        for (int i = 0; i < N; i++)
            board.squares[i][i] = new Square(i, i, true);
        try {
//            board.squares[0][1].setShieldType(Square.Shield.Normal);
              board.squares[7][0].setShieldType(Square.Shield.Normal);
            board.squares[6][0].setShieldType(Square.Shield.Flying);
//            board.squares[5][0].setShieldType(Square.Shield.Normal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeValue(board, N, N);
        return board;
    }

//    @Override
//    public Board generate(int row,int colw,int mines) {
//        Integer N = 9;
//        Board board = new Board(new DefaultBoardRules(), N, N);
//        for (int i = 0; i < N; i++)
//            for (int j = 0; j < N; j++)
//                board.setSqaure(i, j, new Square(i, j));
//
//            board.squares[8][8] = new Square(8,8, true);
//        board.squares[7][7] = new Square(0,0, true);
//        initializeValue(board, N, N);
//        return board;
//    }

}
/*public class FixedBoardGenerator extends BoardGenerator {
    @Override
    public Board generate(int row,int colw,int mines) {
        Integer N = 9;
        Board board = new Board(new DefaultBoardRules(), N, N);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                board.setSqaure(i, j, new Square(i, j));
        for (int i = 0; i < N; i++)
            board.squares[i][i] = new Square(i, i, true);

        initializeValue(board, N, N);
        return board;
    }
}*/