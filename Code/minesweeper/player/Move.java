package minesweeper.player;


import minesweeper.game.Square;

import java.io.Serializable;

public class Move implements Serializable {

    private MoveType moveType;
    private Square position;

    public Move(MoveType moveType, Square position) {
        this.moveType = moveType;
        this.position = position;
    }


    public MoveType getMoveType() {
        return moveType;
    }

    public Square getPosition() {
        return position;
    }

    public enum MoveType {
        OPEN, FLAG
    }
}
