package minesweeper.game;

import java.io.Serializable;

abstract class BoardGenerator implements Serializable {

    class DefaultBoardRules extends CustomBoardRules {

        DefaultBoardRules() {
            super(new Setting());
            setting = new Setting();
            setting.setEmptyClick(10);
            setting.setScoreToEarnWhenAShieldIsUsed(250);
            setting.setRighttFlag(5);
            setting.setWrongFlag(1);//make sure if positive or nigative
            setting.setMineClick(250);
            setting.setWarning(10);
            setting.setWhenToLose(0);
            setting.setWinner(100);
        }
    }

    abstract Board generate(int row, int colw, int mines, int shields, int flyingShields);

    static void initializeValue(Board board, int Rows, int Columns) {
        for (int i = 0; i < Rows; i++)
            for (int j = 0; j < Columns; j++) {
                Square square = board.squares[i][j];
                square.setEmpty(false);
                if (!square.getMine()) {
                    Integer num = board.getNumberOfMinesArround(i, j);
                    if (num == 0) square.setEmpty(true);
                    else square.setNumber(num);
                    if (square.getShieldType() == Square.Shield.Flying) {
                        board.FlyingShields.add(square);
                    }
                }
            }
    }
}
