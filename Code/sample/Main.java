package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import minesweeper.game.Game;
import minesweeper.game.GameExceptions;
import minesweeper.game.GameStorageManager;
import minesweeper.player.Player;
import minesweeper.ui.GraphicalUserInterface;

import java.io.File;
import java.io.FileNotFoundException;


public class Main extends Application {
    final public static Boolean debug = false;

    static public void log(String log) {

        if (debug) {
            System.out.println(log);
        }
    }


    static public void main(String[] args) {
        launch(args);
    }

    private Boolean gameEnded = false;

    private GraphicalUserInterface ui = new GraphicalUserInterface();
    private Game game = new Game(ui);

    private Player player;
    public static Thread gameThread;
    public static Thread timerThread;

    public void resetGame() {
        game = new Game(ui);
    }

    class GameThread extends Thread {
        @Override
        public void run() {
            try {
                game.makePlayerMove();
                if (ui.getAutoSave() && !game.getDisplayMode()) {
                    GameStorageManager.save(game, "Quick.bin");
                    log("game saved in Quick.bin");
                }
            } catch (FileNotFoundException e) {
                Platform.runLater(() ->
                        ui.ShowWrongMessage("Error :\n " + e.getMessage() +
                                ", \nPlease check permission or run as administrator \n or move the game to \n another directory"));
            } catch (InterruptedException ignored) {
                if (debug)
                    ignored.printStackTrace();
                if (ui.getGameLocked()) {
                    waitUntilGameUnlocked();
                    run();
                }
                return;
            } catch (Exception e) {
                if (e == GameExceptions.INCORRECT_CLOSE) {
                    Platform.runLater(Main.this::gameEnd);
                } else if (e == GameExceptions.CANNOT_DO_ACTION_ON_FLAGED_SQUARE
                        || e == GameExceptions.CANNOT_DO_ACTION_ON_OPENED_SQUARE) {
                    gameThread = new GameThread();
                    gameThread.start();
                    return;
                }
                if (debug)
                    e.printStackTrace();
            }
            log("I Got here");
            timerThread.interrupt();
            Platform.runLater(Main.this::afterMove);
        }

    }

    private void waitUntilGameUnlocked() {
        while (ui.getGameLocked()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                if (debug)
                    e.printStackTrace();
            }
        }
    }

    private void initializeGame() {
        ui = new GraphicalUserInterface();
        ui.setMain(this);
        ui.setGame(game);
        game.setUserInterface(ui);
        ui.showInterface();

    }

    public void displayGame(Game game) {
        this.game = game;
        game.setDisplayMode(true);
        initializeGame();
        if (!game.getFinished())
            ui.ShowWrongMessage("The game is not over yet");
        else {
            game.startGame();
            loopGame();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeGame();
        String choice;
        GameStorageManager.setUi(ui);
        choice = ui.MainMenu();
        if (choice == null)
            return;
        switch (choice) {
            case "Start new game":
                game.startGame();
                Integer numberOfPlayers = ui.getNumberOfPlayers();
                for (int i = 0; i < numberOfPlayers; i++) {
                    try {
                        Player player = ui.getPlayerInfo();
                        game.addPlayer(player);
                    } catch (Exception e) {
                        if (e == GameExceptions.INVALID_DATA) i--;
                    }
                }
                game.setBeginTime(System.currentTimeMillis());
                loopGame();
                break;
            case "Load Game":
                String path = ui.getOpenPath();
                if (path == null) {
                    log("NULL FILE");
                    start(primaryStage);
                    break;
                }
                game = (Game) GameStorageManager.load(path);
                initializeGame();
                game.setUserInterface(ui);
                game.startGame();
                loopGame();
                break;
            case "Load Quick Game":
                try {
                    game = (Game) GameStorageManager.load("Quick.bin");
                    initializeGame();
                    game.setUserInterface(ui);
                    game.startGame();
                    loopGame();
                } catch (FileNotFoundException e) {
                    //if the file was empty
                    ui.ShowWrongMessage("File Not Found (\"Quick.bin\")");
                    start(primaryStage);
                } finally {
                    break;
                }

            case "Settings":
                game.setSettings();
                start(null);
                break;
            case "Score board":
                ui.showScoreBoard();
                break;
            case "Display old game":
                String string = ui.getOpenPath();
                try {
                    displayGame((Game) GameStorageManager.load(string));
                } catch (Exception e) {
                    start(primaryStage);
                }
                break;
            case "Account":
                game.setAccount(
                        ui.editAccount(
                                game.getAccount()
                        )
                );
                start(primaryStage);
                break;
            case "Exit game":
                if (timerThread != null) timerThread.interrupt();
                if (gameThread != null) gameThread.interrupt();
                return;
            default:
                break;
        }

    }

    private void beforeMove() throws Exception {
        log("start befre");
        player = game.getCurrentPlayer();
        log("hey");
        ui.setCurrentPlayerTurn(player);
        log("update");
        game.updateAndShowBoard();
        log("la 3njad update");
    }

    private void afterMove() {
        if (!gameEnded) {
            log("i got to after move");
            if (player.getLost())
                ui.showPlayerLose(player);
            log("loop game");
            loopGame();
        }
    }

    private void loopGame() {
        log("statrt loop game");
        if (game.isGameOver()) {
            gameEnd();
            return;
        }
        try {
            beforeMove();
            log("new thread was created");
            Platform.runLater(ui::resetTimer);
            timerThread = new Thread(
                    () -> {
                        for (int i = 0; i < game.getTimer(); i++) {
                            try {
                                Thread.sleep(1000);
                                game.getmBoard().shuffleShields();
                                Platform.runLater(ui::decreaseTimer);
                            } catch (InterruptedException e) {
                                if (debug)
                                    e.printStackTrace();
                                if (ui.getGameLocked()) {
                                    i -= 1;
                                    waitUntilGameUnlocked();
                                } else {
                                    if (debug)
                                        e.printStackTrace();
                                    return;
                                }
                            } catch (Exception e) {
                                if (debug)
                                    e.printStackTrace();
                            }

                        }
                        if (gameThread != null && gameThread.isAlive()) {
                            Platform.runLater(() -> {
                                gameThread.interrupt();
                                ui.informTimeOut();
                                afterMove();
                            });
                        }
                    }
            );
            timerThread.start();
            gameThread = new GameThread();
            gameThread.start();
        } catch (Exception e) {
            if (debug)
                e.printStackTrace();
        }
    }

    private void gameEnd() {
        gameEnded = true;
        game.setFinished(true);
        try {
            if (!game.getDisplayMode()) {
                game.updateScoreBoard();
                game.setEndTime(System.currentTimeMillis());
                File finishedDir = new File("." + File.separator + "finished");
                ui.makeDirIfItDoesntExist(finishedDir);
                GameStorageManager.save(game, finishedDir.toString() + File.separator + System.currentTimeMillis() / 1000 + ".bin");
            }

        } catch (FileNotFoundException e) {
            ui.ShowWrongMessage("Error :\n " + e.getMessage() + ", \nPlease check permission or run as administrator \n or move the game to \n another directory");
        } catch (Exception e) {
            if (debug)
                e.printStackTrace();
        }
        game.showExtraInfo();
        ui.closeInterface();
        try {
            new Main().start(new Stage());
        } catch (Exception ignored) {

        }
    }
}
