package minesweeper.game;


public class NormalShowBoardRules implements ShowBoardRules {

    public NormalShowBoardRules() {
    }

    @Override
    public String getRepresentive(Square square) throws Exception {
        if (square.getOpened()) {
            if (square.getMine()) return "B";
            if (square.getEmpty()) {
                if (square.getLastPlayer() == null || square.getLastPlayer().getColor().length() > 1) return " ";
                else return square.getLastPlayer().getColor();
            }
            if (square.getNumber() != null) return String.valueOf(square.getNumber());
            throw GameExceptions.UNKNOWN_SQUARE_STATE;
        } else if (square.getMarked()) return "P";
        else if (square.getClosed()) return "O";
        throw GameExceptions.UNKNOWN_SQUARE_STATE;
    }
}
