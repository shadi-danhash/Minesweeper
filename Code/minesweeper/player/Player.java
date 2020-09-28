package minesweeper.player;

import minesweeper.game.Board;
import minesweeper.ui.UserInterface;

import java.io.Serializable;
import java.util.ArrayList;

import static minesweeper.game.GameExceptions.NO_SHIELDS_AVAILABLE;

public abstract class Player implements Serializable {
    private String name;
    private String color;
    private Integer score;
    private ArrayList<Move> moves = new ArrayList<>();
    private Boolean hasLost;
    private Boolean hasWarning;
    private Integer numberOfShields = 0;
    private Boolean canGetShield = true;

    public void increaseShield() {
        if (canGetShield)
            numberOfShields++;
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public void useShield() throws Exception {
        if (numberOfShields == 0) throw NO_SHIELDS_AVAILABLE;
        numberOfShields--;
    }

    public void enableShieldAccessibility() {
        canGetShield = true;
    }

    public void diableShieldAccessibiltiy() {
        canGetShield = false;
    }

    public Integer getNumberOfShields() {
        return numberOfShields;
    }

    public Boolean getWarning() {
        return hasWarning;
    }

    public void setWarning(Boolean hasWarning) {
        this.hasWarning = hasWarning;
    }

    public Boolean getLost() {
        return hasLost;
    }

    public void setLost(Boolean hasLost) {
        this.hasLost = hasLost;
    }

    public String getColor() {
        return color;
    }

    public Player(String Name, String Color) {
        name = Name;
        color = Color;
        score = 0;
        hasWarning = false;
        hasLost = false;
    }

    abstract public Move makeMove(Board board, UserInterface userInterface) throws Exception;

    public int getScore() {
        return score;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateScore(int scoreDelta) {
        this.score += scoreDelta;
    }
}
