package minesweeper.game;

import minesweeper.player.Player;

import java.io.Serializable;

interface BoardRules extends Serializable {
    Integer MarkedSquare(Player player, Square square) throws Exception;
    void setBoard(Board board);
    Integer ClickedSquare(Player player, Square square) throws Exception;
}
