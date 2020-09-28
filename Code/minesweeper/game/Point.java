package minesweeper.game;

import java.io.Serializable;

public class Point implements Serializable {
    private Integer x, y;

    public Point(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    void setX(Integer x) {
        this.x = x;
    }

    void setY(Integer y) {
        this.y = y;
    }
}
