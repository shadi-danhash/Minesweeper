package minesweeper.game;

import java.io.Serializable;

interface ShowBoardRules extends Serializable {
    String getRepresentive(Square square) throws Exception;
}
