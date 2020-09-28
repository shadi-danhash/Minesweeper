package minesweeper.ui;

import javafx.stage.Stage;
import minesweeper.game.Board;
import minesweeper.game.Setting;
import minesweeper.player.Move;
import minesweeper.player.Player;

import java.io.Serializable;
import java.util.ArrayList;

public interface UserInterface<E> extends Serializable {
    void showInterface();

    //  public  Integer getChoice();
    Setting getSettings();

    Move waitMove(Board board) throws Exception;

    void showExtraInfo(Object infoObject, Board board);

    void showPlayerLose(Player p);

    void showBoard(Board board) throws Exception;

    void closeInterface();

    Integer getNumberOfPlayers();

    Player getPlayerInfo() throws Exception;

    void setCurrentPlayerTurn(Player player);

    void setGsetting(Setting gsetting);

    String editAccount(String Accountٍ);

    //public Integer getMenuChoice();
    void ShowWrongMessage(String string);

    void showScoreBoard();
}
