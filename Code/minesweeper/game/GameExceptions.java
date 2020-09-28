package minesweeper.game;

import java.io.Serializable;

public class GameExceptions implements Serializable {
    public static Exception ILLEGAL_MOVE = new Exception();
    public static Exception NO_PLAYERS = new Exception();
    public static Exception CANNOT_DO_ACTION_ON_OPENED_SQUARE = new Exception();
    public static Exception UNKNOWN_SQUARE_STATE = new Exception();
    public static Exception CANNOT_DO_ACTION_ON_FLAGED_SQUARE = new Exception();
    public static Exception INVALID_DATA = new Exception();
    public static Exception INCORRECT_CLOSE = new Exception();
    public static Exception NO_SHIELDS_AVAILABLE = new Exception();
    public static Exception CANT_HAVE_MINE_AND_SHIELD = new Exception();
    public static Exception TOOL_BAR_EXIT = new Exception();

}
