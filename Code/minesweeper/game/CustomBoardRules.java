package minesweeper.game;

import minesweeper.player.Player;

import static sample.Main.log;

public class CustomBoardRules implements BoardRules {
    Setting setting;
    protected Board board;

    CustomBoardRules(Setting settings) {
        this.setting = settings;
    }

    @Override
    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public Integer MarkedSquare(Player player, Square square) throws Exception {
        if (square.getMarked()) {
            square.setMarked(false);
            return 0;
        }
        square.setMarked(true);
        if (square.getMine())
            return setting.getRighttFlag();
        if (!square.getMine())
            return -setting.getWrongFlag();
        square.setMarked(false);
        throw GameExceptions.UNKNOWN_SQUARE_STATE;
    }


    private Integer floodFill(Player player, int I, int J) {
        Square[][] squares = board.squares;
        int sum = 0;
        for (int i = -1; i <= +1; i++) {
            for (int j = -1; j <= +1; j++) {
                if (i == j && j == 0 || !board.isValid(I + i, J + j) || squares[I + i][J + j].getOpened()
                        || squares[I + i][J + j].getMarked()) continue;
                squares[I + i][J + j].setOpened(true);
                squares[I + i][J + j].setLastPlayer(player);
                if (squares[I + i][J + j].getShieldType() != Square.Shield.NoShield)
                    player.increaseShield();
                if (squares[I + i][J + j].getEmpty()) sum += floodFill(player, I + i, J + j);
                sum++;
            }
        }
        return sum;
    }

    @Override
    public Integer ClickedSquare(Player player, Square square) throws Exception {
        square.setOpened(true);
        if (setting == null)
            log("null setting in cust board rules");
        if (square.getMine()) {
            int scoreDelta = 0;
            try {
                player.useShield();
                scoreDelta = setting.getScoreToEarnWhenAShieldIsUser();
            } catch (Exception ignored) {
            }
            scoreDelta -= setting.getMineClick();
            return scoreDelta;
        }
        Integer score = 0;
        //if (!square.getEmpty()) {
            score += handleShieldScore(square);
            if (square.getShieldType() != Square.Shield.NoShield)
                for (int i = 0; i < (square.getShieldType() == Square.Shield.Normal ? 1 : 2); i++)
                    player.increaseShield();
        //}
        if (square.getEmpty()) return score + setting.getEmptyClick() + floodFill(player, square.getX(), square.getY());
        if (square.getNumber() != null) return score + square.getNumber();
        square.setOpened(false);
        throw GameExceptions.UNKNOWN_SQUARE_STATE;
    }

    private Integer handleShieldScore(Square square) {
        switch (square.getShieldType()) {
            case NoShield:
                return 0;
            case Normal:
                return 0;
            case Flying:
                return 1000;
            default:
                return 0;
        }
    }
}
