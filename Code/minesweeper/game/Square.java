package minesweeper.game;

import minesweeper.player.Player;

import static minesweeper.game.GameExceptions.CANT_HAVE_MINE_AND_SHIELD;

public class Square extends Point {
    public enum Shield {NoShield, Flying, Normal}

    private Boolean isOpened = false, isMine = false, isClosed = true, isMarked = false, isEmpty = false;
    private Shield shield = Shield.NoShield;
    private Integer number;
    private Player lastPlayer;

    public Square(Integer x, Integer y) {
        super(x, y);
    }

    public Square(Square cloned) {
        this(cloned.getX(), cloned.getY());
        isMine = cloned.isMine;
        isOpened = cloned.isOpened;
        isClosed = cloned.isClosed;
        isMarked = cloned.isMarked;
        isEmpty = cloned.isEmpty;
        shield = cloned.shield;
        lastPlayer = cloned.lastPlayer;
        number = cloned.number;

    }

    public Square(Integer x, Integer y, Boolean isMine) {
        super(x, y);
        this.isMine = isMine;
    }

    void setLastPlayer(Player lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    void setShieldType(Shield shield) throws Exception {
        if (shield != Shield.NoShield && this.getMine()) throw CANT_HAVE_MINE_AND_SHIELD;
        this.shield = shield;
    }

    public Shield getShieldType() {
        return shield;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public Boolean getMarked() {
        return isMarked;
    }

    public Boolean getMine() {
        return isMine;
    }

    public Boolean getOpened() {
        return isOpened;
    }

    void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    void setEmpty(Boolean empty) {
        isEmpty = empty;
    }


    void setOpened(Boolean opened) {
        isOpened = opened;
        isClosed = !opened;
    }

    public Boolean getEmpty() {
        return isEmpty;
    }

    void setMarked(Boolean marked) {
        isMarked = marked;
    }

    @Override
    public String toString() {
        return getX() + " " + getY();
    }
}
