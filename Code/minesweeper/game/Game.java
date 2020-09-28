package minesweeper.game;

import minesweeper.player.Move;
import minesweeper.player.Player;
import minesweeper.ui.UserInterface;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import static sample.Main.log;

public class Game implements Serializable {

    private ArrayList<Player> mPlayers;
    private transient UserInterface mUserInterface;
    private Board mBoard;
    private Board initialBoard;
    private Setting settings;
    private Boolean isFirstMove;
    private Integer currentPlayer = 0;
    private String Account;
    private ArrayList<Move> gameMoves = new ArrayList<>();
    private ArrayList<Long> moveTime = new ArrayList<>();
    private Boolean isFinished = false;
    private Integer moveCounter = 0;
    private Boolean displayMode = false;
    private Long beginTime;


    public Integer getNumberOfExistMines() {
        Integer sum = 0;
        for (int i = 0; i < mBoard.squares.length; i++)
            for (int j = 0; j < mBoard.squares[0].length; j++)
                if (mBoard.squares[i][j].getMine() && mBoard.squares[i][j].getClosed())
                    sum++;
        return sum;
    }

    public Integer getNumberOfExistSheilds() {
        Integer sum = 0;
        for (int i = 0; i < mBoard.squares.length; i++)
            for (int j = 0; j < mBoard.squares[0].length; j++)
                if (mBoard.squares[i][j].getShieldType() != Square.Shield.NoShield && mBoard.squares[i][j].getClosed())
                    sum++;
        return sum;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(Long endTime) {
        Long endTime1 = endTime;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setUserInterface(UserInterface userInterface) {
        this.mUserInterface = userInterface;
    }

    public void setDisplayMode(Boolean displayMode) {
        this.displayMode = displayMode;
    }

    public Boolean getDisplayMode() {
        return displayMode;
    }

    public Game(UserInterface userInterface) {
        isFirstMove = true;
        mPlayers = new ArrayList<>();
        mUserInterface = userInterface;
    }

    public void addPlayer(Player p) {
        for (int i = 0; i < settings.getNumberOfInitialsShields(); i++) p.increaseShield();
        mPlayers.add(p);
        System.out.println(mPlayers.size());

    }

    public void startGame() {
        isFirstMove = true;
        try {
            Account = (String) GameStorageManager.load(GameStorageManager.accountPath);
        } catch (Exception e) {
            Account = mUserInterface.editAccount(Account);
            try {
                GameStorageManager.save(Account, GameStorageManager.accountPath);
            } catch (FileNotFoundException ex) {
                mUserInterface.ShowWrongMessage("Error :\n " + ex.getMessage() + ", \nPlease check permission or run as administrator \n or move the game to \n another directory");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        if (displayMode) {
            currentPlayer = 0;
            for (Player p : mPlayers) {
                p.setLost(false);
                p.setWarning(false);
                while (p.getNumberOfShields() > 0) {
                    try {
                        p.useShield();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                p.updateScore(-p.getScore());
            }
        }
        log("Got settings");
        try {
            settings = (Setting) GameStorageManager.load("Settings.bin");
            if (settings == null) throw new Exception();
        } catch (Exception e) {
            settings = new Setting();
            settings.setDefault();
        }
        mUserInterface.setGsetting(settings);
        BoardGenerator boardGenerator = new FixedBoardGenerator();
        if (settings.getBoardType() == Setting.BoardType.Fixed)
            boardGenerator = new FixedBoardGenerator();
        else if (settings.getBoardType() == Setting.BoardType.Random)
            boardGenerator = new RandomBoardGenerator();
        if (mBoard == null) {
            mBoard = boardGenerator.generate(settings.getRow(), settings.getCol(), settings.getMine(), settings.getNumShield(), settings.getNumberOfFlyingShields());
            mBoard.setBoardRules(new CustomBoardRules(settings));
            initialBoard = new Board(mBoard);
        }
    }

    public Player getCurrentPlayer() throws Exception {
        if (mPlayers.isEmpty()) throw GameExceptions.NO_PLAYERS;
        if (currentPlayer == null) currentPlayer = 0;
        return mPlayers.get(currentPlayer);
    }

    private void reallyMakePlayerMove() throws Exception {
        Player player = getCurrentPlayer();
        if (!player.getLost()) {
            Move move;
            long before, after;
            try {
                before = System.currentTimeMillis();
                move = player.makeMove(mBoard, mUserInterface);
                after = System.currentTimeMillis();
            } catch (InterruptedException ie) {
                do {
                    if (currentPlayer == mPlayers.size() - 1) currentPlayer = 0;
                    else currentPlayer++;
                } while (getCurrentPlayer().getLost() && !NoOneLeft());
                throw ie;
            }
            if (move == null)
                throw GameExceptions.INCORRECT_CLOSE;
            if (move.getMoveType() == Move.MoveType.OPEN && isFirstMove) {
                mBoard.replaceMineWithAnotherSquare(move.getPosition().getX(), move.getPosition().getY());
                isFirstMove = false;
            }
            int scoreChange = mBoard.makeMove(player, move);
            player.updateScore(scoreChange);
            if (player.getScore() <= settings.getWarning()) player.setWarning(true);
            if (player.getScore() < settings.getWhenToLose()) player.setLost(true);
            if (player.getScore() > settings.getWarning()) player.setWarning(false);
            gameMoves.add(move);
            moveTime.add(after - before);
        } else {
            makePlayerMove();
        }
        do {
            if (currentPlayer == mPlayers.size() - 1) currentPlayer = 0;
            else currentPlayer++;
        } while (getCurrentPlayer().getLost() && !NoOneLeft());

    }

    private void simulateMakingPlayerMove() throws Exception {
        Thread.sleep(moveTime.get(moveCounter));
        Player player = getCurrentPlayer();
        if (!player.getLost()) {
            Move move;
            try {
                move = gameMoves.get(moveCounter);
                moveCounter++;
            } catch (Exception ie) {
                do {
                    if (currentPlayer == mPlayers.size() - 1) currentPlayer = 0;
                    else currentPlayer++;
                } while (getCurrentPlayer().getLost() && !NoOneLeft());
                throw ie;
            }
            if (move == null)
                throw GameExceptions.INCORRECT_CLOSE;
            if (move.getMoveType() == Move.MoveType.OPEN && isFirstMove) {
                initialBoard.replaceMineWithAnotherSquare(move.getPosition().getX(), move.getPosition().getY());
                isFirstMove = false;
            }
            int scoreChange = initialBoard.makeMove(player, move);
            player.updateScore(scoreChange);
            if (player.getScore() <= settings.getWarning()) player.setWarning(true);
            if (player.getScore() < settings.getWhenToLose()) player.setLost(true);
            if (player.getScore() > settings.getWarning()) player.setWarning(false);
        } else {
            simulateMakingPlayerMove();
        }
        do {
            if (currentPlayer == mPlayers.size() - 1) currentPlayer = 0;
            else currentPlayer++;
        } while (getCurrentPlayer().getLost() && !NoOneLeft());

    }

    public void makePlayerMove() throws Exception {
        if (displayMode) simulateMakingPlayerMove();
        else reallyMakePlayerMove();
    }

    private Boolean NoOneLeft() {
        int c = 0;
        for (Player mPlayer : mPlayers) {
            if (!mPlayer.getLost())
                c++;
        }
        return c == 0;
    }

    public void updateAndShowBoard() {
        try {
            mUserInterface.showBoard(getmBoard());
        } catch (Exception ignored) {

        }
    }

    public void showExtraInfo() {
        mUserInterface.showExtraInfo(getWinner(), mBoard);

    }

    public Boolean isGameOver() {
        // If All Players score is below 0
        if (displayMode) {
            return moveCounter == gameMoves.size();
        }
        boolean done = false;
        int numberOfPlayerBelowScoreToLose = 0;
        for (Player player : mPlayers)
            if (player.getScore() < settings.getWhenToLose()) numberOfPlayerBelowScoreToLose++;

        if (mPlayers.size() == numberOfPlayerBelowScoreToLose) done = true;
        if (done) return true;

        // if no mines left on board ( all closed or falgged )
        if (mBoard.noMinesLeft()) done = true;

        return done;
    }

    /**
     * ++++++++++++++++++++++++
     */
    public ArrayList<Player> getWinner() {
        ArrayList<Player> winners = new ArrayList<>();
        if (mPlayers.size() == 0)
            return null;
        for (Player player : mPlayers) {
            player.updateScore(settings.getScoreShield() * player.getNumberOfShields());
            while (player.getNumberOfShields() > 0) {
                try {
                    log("used a shield for player " + player.getName());
                    player.useShield();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Player winner = mPlayers.get(0);
        for (int i = 1; i < mPlayers.size(); i++)
            if (winner.getScore() < mPlayers.get(i).getScore()) {
                winner = mPlayers.get(i);
            }
        for (Player mPlayer : mPlayers) {
            if (winner.getScore() == mPlayer.getScore())
                winners.add(mPlayer);
        }
        return winners;
    }

    /***shglna*/
    public Board getmBoard() {
        if (displayMode) return initialBoard;
        return mBoard;
    }

    public Integer getTimer() {
        return settings.getRoledurationOfPlayer();
    }

    public void setSettings() {
        settings = mUserInterface.getSettings();
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getAccount() {
        return Account;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public ArrayList<Game> getScoreBoard() {
        try {
            return (ArrayList<Game>) GameStorageManager.load("ScoreBoard.bin");
        } catch (Exception e) {
            return new ArrayList<>(10);
        }
    }

    public void updateScoreBoard() throws Exception {
        ArrayList<Game> games = getScoreBoard();
        if (this.getWinner().get(0) != null)
            games.add(this);
        sortByScore(games);
        if (games.size() > 10)
            games = new ArrayList<>(games.subList(0, 10));
        GameStorageManager.save(games, "ScoreBoard.bin");
    }

    public Setting getSettings() {
        return settings;
    }

    public static void sortByScore(ArrayList<Game> games) {
        games.sort(Comparator.comparingInt(game -> -game.getWinner().get(0).getScore()));
    }

    public static void sortByName(ArrayList<Game> games) {
        games.sort(Comparator.comparing(game -> game.getWinner().get(0).getName()));
    }

    public static void sortByDate(ArrayList<Game> games) {
        games.sort(Comparator.comparing(game -> game.beginTime));
    }
}

