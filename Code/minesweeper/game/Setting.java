package minesweeper.game;

import java.io.Serializable;

public class Setting implements Serializable {
    private Integer Row,Col,Mine,WrongFlag,RighttFlag,MineClick,EmptyClick,Warning,Winner,WhenToLose,scoreToEarnWhenAShieldIsUser,numShield,scoreShield,roledurationOfPlayer;
    public enum BoardType {Random,Fixed};
    private BoardType boardType;
    public void setRow(Integer row) {
        Row = row;
    }
    private Integer numberOfFlyingShields, numberOfInitialsShields;

    public void setNumberOfInitialsShields(Integer numberOfInitialsShields) {
        this.numberOfInitialsShields = numberOfInitialsShields;
    }

    public Integer getNumberOfInitialsShields() {
        return numberOfInitialsShields;
    }

    public void setScoreToEarnWhenAShieldIsUsed(Integer scoreToEarnWhenAShieldIsUser) {
        this.scoreToEarnWhenAShieldIsUser = scoreToEarnWhenAShieldIsUser;
    }

    public void setNumberOfFlyingShields(Integer numberOfFlyingShields) {
        this.numberOfFlyingShields = numberOfFlyingShields;
    }

    public Integer getNumberOfFlyingShields() {
        return numberOfFlyingShields;
    }

    public Integer getScoreToEarnWhenAShieldIsUser() {
        return scoreToEarnWhenAShieldIsUser;
    }

    public void setCol(Integer col) {
        Col = col;
    }

    public void setMine(Integer mine) {
        Mine = mine;
    }

    public void setWrongFlag(Integer wrongFlag) {
        WrongFlag = wrongFlag;
    }

    public void setRighttFlag(Integer righttFlag) {
        RighttFlag = righttFlag;
    }

    public void setMineClick(Integer mineClick) {
        MineClick = mineClick;
    }

    public void setEmptyClick(Integer emptyClick) {
        EmptyClick = emptyClick;
    }

    public void setWarning(Integer warning) {
        Warning = warning;
    }

    public void setWinner(Integer winner) {
        Winner = winner;
    }

    public void setWhenToLose(Integer whenToLose) {
        WhenToLose = whenToLose;
    }

    public void setBoardType(BoardType boardType) {
        this.boardType = boardType;
    }
    public void setNumShield(int numShield){this.numShield= numShield;}
    public void setRoleDurationOfPlayer(int roledurationOfPlayer){this.roledurationOfPlayer=roledurationOfPlayer;}
 public  void  setScoreShield(int scoreShield){this.scoreShield=scoreShield;}
    public Integer getRow() {

        return Row;
    }

    public Integer getCol() {
        return Col;
    }

    public Integer getMine() {
        return Mine;
    }

    public Integer getWrongFlag() {
        return WrongFlag;
    }

    public Integer getRighttFlag() {
        return RighttFlag;
    }

    public Integer getMineClick() {
        return MineClick;
    }

    public Integer getEmptyClick() {
        return EmptyClick;
    }

    public Integer getWarning() {
        return Warning;
    }

    public Integer getWinner() {
        return Winner;
    }

    public Integer getWhenToLose() {
        return WhenToLose;
    }

    BoardType getBoardType() {
        return boardType;
    }
    public   Integer getNumShield (){ return  numShield;}
    public  Integer getScoreShield(){return  scoreShield;}
    public  Integer getRoledurationOfPlayer(){return  roledurationOfPlayer;}
    public void setDefault()
    {
        setEmptyClick(10);
        setRighttFlag(5);
        setWrongFlag(1);//make sure if positive or nigative
        setMineClick(250);
        setWarning(10);
        setWhenToLose(0);
        setWinner(100);
        setRoleDurationOfPlayer(10);
        setScoreShield(50);
        setScoreToEarnWhenAShieldIsUsed(250);
        setNumberOfInitialsShields(0);
        setBoardType(Setting.BoardType.Random);
        setRow(9);
        setCol(9);
        setMine(9);
        setNumShield(2);
        setNumberOfFlyingShields(1);
    }
}
