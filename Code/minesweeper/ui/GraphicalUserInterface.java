package minesweeper.ui;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import minesweeper.game.*;
import minesweeper.player.*;
import sample.Main;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static sample.Main.log;

public class GraphicalUserInterface implements UserInterface {
    private Scene globslScene;
    private Game game;
    private Integer numberOfPlayers;
    private static Stage window;
    private ProgressBar progressBar;
    private Player player;

    enum ActiveWindow {Settings, Game, NotActive}

    private ActiveWindow activeWindow = ActiveWindow.NotActive;
    private HashMap<String, Boolean> usedColors = new HashMap<>();
    private Player currentPlayer;
    private ToggleButton[][] toggleButtons;
    private volatile Move move;
    private Boolean boardCreated = false;
    private Label playerName, playerScore;
    private Main main = null;

    public void setMain(Main main) {
        this.main = main;
    }

    private Label timer;
    private Label playerShields;
    private Label autoSaveStat;
    private Background defaultBackground;
    private Boolean ShowedNormalShield[][];
    private Group globalGroup;
    private Setting Gsetting;
    private Boolean isGameLocked = false;
    private Boolean autoSave = true;

    public synchronized Boolean getGameLocked() {
        return isGameLocked;
    }

    public void lockGame() {
        isGameLocked = true;
        Platform.runLater(() -> progressBar.setVisible(true));
        Main.timerThread.interrupt();
        Main.gameThread.interrupt();
        log("GAME LOCKED");
    }

    public void unlockGame() {
        isGameLocked = false;
        Platform.runLater(() -> progressBar.setVisible(false));
        log("GAME UNLOCKED");
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private String choice = null;

    class AlertBox {
        AlertBox(String message) {
            Stage stage = new Stage();
            stage.setTitle("Alert");
            VBox vBox = new VBox(
                    20
            );
            Label label = new Label(message);
            Button button = new Button("Close");
            vBox.getChildren().addAll(label, button);
            button.setOnAction(e -> stage.close());
            stage.setScene(new Scene(vBox));
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.setMinWidth(200);
            stage.setMinHeight(200);
            stage.showAndWait();
        }
    }

    private class ShieldBox {
        ShieldBox(Image image) {
            Label label = new Label();
            label.setGraphic(new ImageView(image));
            TranslateTransition translateTransition = new TranslateTransition();
            translateTransition.setDuration(Duration.millis(1000));
            translateTransition.setToX(window.getWidth());
            translateTransition.setByY(window.getHeight());
            translateTransition.setNode(label);
            translateTransition.play();
            Thread waitingThread = new Thread(
                    () ->
                    {
                        try {
                            Thread.sleep(1000);
                            Platform.runLater(() -> globalGroup.getChildren().remove(label));

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
            );
            waitingThread.start();
            globalGroup.getChildren().add(label);
            window.setScene(globslScene);
        }

        ShieldBox(Image image, String string) {
            Label label = new Label();
            label.setGraphic(new ImageView(image));
            label.setTranslateX(window.getWidth() / 2);
            label.setTranslateY(window.getHeight() / 2);
            RotateTransition rotateTransition = new RotateTransition();
            rotateTransition.setDuration(Duration.millis(1000));
            rotateTransition.setToAngle(360 * 2);
            rotateTransition.setNode(label);
            rotateTransition.play();
            Thread waitingThread = new Thread(
                    () ->
                    {
                        try {
                            Thread.sleep(1000);
                            Platform.runLater(() -> globalGroup.getChildren().remove(label));

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
            );
            waitingThread.start();
            globalGroup.getChildren().add(label);
            window.setScene(globslScene);
        }
    }

    @Override
    public void showInterface() {
        log("started");
        if (window == null) {
            window = new Stage();
            window.setTitle("Minesweeper v1Beta");
        }
        window.setOnCloseRequest(
                e -> {
                    try {
                        Main.gameThread.interrupt();
                        Main.timerThread.interrupt();
                    } catch (NullPointerException nPE) {
                        window.close();
                    }
                }
        );
    }

    @Override
    public Setting getSettings() {
        Setting savedSetting;
        try {
            savedSetting = (Setting) GameStorageManager.load("Settings.bin");
        } catch (Exception e) {
            savedSetting = new Setting();
            savedSetting.setDefault();
            try {
                GameStorageManager.save(savedSetting, "Settings.bin");
            } catch (Exception ignored) {
            }
        }
        final Stage settingsWindow = window;
        final Setting setting = savedSetting;
        activeWindow = ActiveWindow.Settings;
        GridPane grid1 = new GridPane();
        GridPane grid2 = new GridPane();
        GridPane grid3 = new GridPane();
        GridPane grid4 = new GridPane();
        Group group = new Group();
        ToggleGroup toggleGroup = new ToggleGroup();
        ToggleGroup toggleGroup1 = new ToggleGroup();
        settingsWindow.setTitle("SETTINGS");
        Label label = new Label("Enter number of columns");
        TextField textField1 = new TextField(savedSetting.getCol() + ""), textField2, textField3;
        grid4.add(label, 0, 0);

        grid4.add(textField1, 2, 0);
        label = new Label("Enter number of rows");
        textField2 = new TextField(savedSetting.getRow() + "");
        grid4.add(label, 0, 1);
        grid4.add(textField2, 2, 1);
        label = new Label("Enter number of mines");
        textField3 = new TextField(savedSetting.getMine() + "");
        grid4.add(label, 0, 3);
        grid4.add(textField3, 2, 3);
        label = new Label("Number of shields");
        TextField numShield = new TextField(savedSetting.getNumShield() + "");
        grid4.add(label, 0, 4);
        grid4.add(numShield, 2, 4);
        label = new Label("Number Of Flying Shields");
        TextField numberOfFlyingShields = new TextField(savedSetting.getNumberOfFlyingShields() + "");
        grid4.add(label, 0, 5);
        grid4.add(numberOfFlyingShields, 2, 5);

        label = new Label("Score to lose when a flag is misplaced ");
        TextField wrongFlag = new TextField(savedSetting.getWrongFlag() + "");
        grid2.add(label, 0, 0);
        grid2.add(wrongFlag, 2, 0);
        label = new Label("Score to lose when a mine is opened ");
        TextField openMine = new TextField(savedSetting.getMineClick() + "");
        grid2.add(label, 0, 1);
        grid2.add(openMine, 2, 1);
        label = new Label("Score to get when a flag is placed correctly :");
        TextField correctFlag = new TextField(savedSetting.getRighttFlag() + "");
        grid2.add(label, 0, 2);
        grid2.add(correctFlag, 2, 2);
        label = new Label("Score to get when an empty square is opened : ");
        TextField empty = new TextField(savedSetting.getEmptyClick() + "");
        grid2.add(label, 0, 3);
        grid2.add(empty, 2, 3);
        label = new Label("max score to lose : ");
        TextField lose = new TextField(savedSetting.getWhenToLose() + "");
        grid2.add(label, 0, 4);
        grid2.add(lose, 2, 4);
        label = new Label("max score to warn :");
        TextField warn = new TextField(savedSetting.getWarning() + "");
        grid2.add(label, 0, 5);
        grid2.add(warn, 2, 5);
        label = new Label("last move score :");
        TextField lastScore = new TextField(savedSetting.getWinner() + "");
        grid2.add(label, 0, 6);
        grid2.add(lastScore, 2, 6);
        label = new Label("score of shields used");
        TextField scoreShieldUsed = new TextField(savedSetting.getScoreToEarnWhenAShieldIsUser() + "");
        grid2.add(label, 0, 7);
        grid2.add(scoreShieldUsed, 2, 7);
        label = new Label("score of shields ");
        TextField scoreShield = new TextField(savedSetting.getScoreShield() + "");
        grid2.add(label, 0, 8);
        grid2.add(scoreShield, 2, 8);
        label = new Label("Role duration of player");
        TextField roledurationOfPlayer = new TextField(savedSetting.getRoledurationOfPlayer() + "");
        grid2.add(label, 0, 9);
        grid2.add(roledurationOfPlayer, 2, 9);
        label = new Label("Initial Number Of Shields");
        TextField initialShields = new TextField(savedSetting.getNumberOfInitialsShields() + "");
        grid2.add(label, 0, 10);
        grid2.add(initialShields, 2, 10);
        grid1.add(grid2, 0, 3);
        grid3.add(grid4, 0, 4);
        GridPane grid5 = new GridPane();
        grid5.add(grid1, 0, 1);
        grid5.add(grid3, 2, 1);
        grid1.setTranslateX(10);
        grid3.setTranslateX(30);
        Button button = new Button("ENTER");
        grid5.add(button, 0, 2);
        button.setLayoutY(50);
        group.getChildren().add(grid5);
        Scene scene = new Scene(group);
        scene.setFill(Color.SKYBLUE);
        settingsWindow.setScene(scene);

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent) ->
        {
            setting.setBoardType(Setting.BoardType.Random);
            int i = Integer.parseInt(textField1.getText());
            setting.setCol(i);
            i = Integer.parseInt(textField2.getText());
            setting.setRow(i);
            i = Integer.parseInt(textField3.getText());
            setting.setMine(i);
            i = Integer.parseInt(numShield.getText());
            setting.setNumShield(i);
            i = Integer.parseInt(numberOfFlyingShields.getText());
            setting.setNumberOfFlyingShields(i);


            i = Integer.parseInt(wrongFlag.getText());
            setting.setWrongFlag(i);
            i = Integer.parseInt(openMine.getText());
            setting.setMineClick(i);
            i = Integer.parseInt(empty.getText());
            setting.setEmptyClick(i);
            i = Integer.parseInt(correctFlag.getText());
            setting.setRighttFlag(i);
            i = Integer.parseInt(lose.getText());
            setting.setWhenToLose(i);
            i = Integer.parseInt(warn.getText());
            setting.setWarning(i);
            i = Integer.parseInt(lastScore.getText());
            setting.setWinner(i);
            i = Integer.parseInt(scoreShieldUsed.getText());
            setting.setScoreToEarnWhenAShieldIsUsed(i);
            i = Integer.parseInt(roledurationOfPlayer.getText());
            setting.setRoleDurationOfPlayer(i);
            i = Integer.parseInt(scoreShield.getText());
            setting.setScoreShield(i);
            i = Integer.parseInt(initialShields.getText());
            setting.setNumberOfInitialsShields(i);


            Gsetting = setting;
            if (validSettings(setting) != null) {
                new AlertBox(validSettings(setting));
            } else homeBehavior();
        });
        settingsWindow.showAndWait();
        try {
            Gsetting = setting;
            GameStorageManager.save(setting, "Settings.bin");
        } catch (Exception ignored) {
        }
        return setting;
    }

    private String validSettings(Setting setting) {
        if (setting.getMine() + setting.getNumShield() + setting.getNumberOfFlyingShields() > setting.getCol() * setting.getRow())
            return "Override board boundaries";
        if (setting.getRoledurationOfPlayer() <= 0)
            return "Invalid time";
        if (setting.getRow() > 28 || setting.getRow() <= 0)
            return "rows must be between 1 and 28";
        if (setting.getCol() > 48)
            return "columns must be between 1 and 48";
        if (setting.getMine() < 0)
            return "Mines must be positive";
        if (setting.getNumShield() < 0)
            return "Shields must be positive";
        if (setting.getNumberOfFlyingShields() < 0)
            return "Flying shields must be positive";
        if (setting.getWhenToLose() > 0) // to avoid losing from the first move && the board still appear in the screen
            return "Invalid lose limit";
        if (setting.getNumberOfInitialsShields() < 0)
            return "Initials Shields must be positive";
        return null;
    }

    private boolean MouseClickIsPrimary(MouseEvent e) {
        return e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1;
    }

    public void resetTimer() {
        log("RESETED");
        timer.setText(Gsetting.getRoledurationOfPlayer() + "");
    }

    public void decreaseTimer() {
        log("decreased");
        if (timer.getText() != null) {
            Integer time = Integer.parseInt(timer.getText());
            time--;
            timer.setText(time + "");
            log(timer.getText());
        } else timer.setText(Gsetting.getRoledurationOfPlayer() + "");
    }

    public void informTimeOut() {
        new AlertBox("Your time is up, next turn now");
    }

    @Override
    public Move waitMove(Board board) throws Exception {
        move = null;
        Platform.runLater(
                () -> {
                    for (int i = 0; i < board.squares.length; i++)
                        for (int j = 0; j < board.squares[0].length; j++)
                            toggleButtons[i][j].setDisable(false);
                }
        );
        try {
            while (move == null) Thread.sleep(1);
        } catch (InterruptedException ie) {
            Platform.runLater(
                    () -> {
                        for (int i = 0; i < board.squares.length; i++)
                            for (int j = 0; j < board.squares[0].length; j++)
                                toggleButtons[i][j].setDisable(true);
                    }
            );
            throw ie;
        }
        Platform.runLater(
                () -> {
                    for (int i = 0; i < board.squares.length; i++)
                        for (int j = 0; j < board.squares[0].length; j++)
                            toggleButtons[i][j].setDisable(true);
                }
        );
        Move temp = move;
        move = null;
        return temp;
    }

    @Override
    public void showExtraInfo(Object infoObject, Board board) {
        ArrayList<Player> Winners = (ArrayList<Player>) infoObject;
        if (Winners == null) return;

        if (Winners.get(0).getScore() < 0) {
            Stage stage = new Stage();
            Label LoseLabel = new Label();
            LoseLabel.setText("NO ONE HAD WON");
            Button ok = new Button("OK");
            GridPane gridPane = new GridPane();
            gridPane.add(LoseLabel, 0, 0);
            gridPane.add(ok, 0, 1);
            Group group = new Group();
            group.getChildren().add(gridPane);
            Scene scene = new Scene(group);
            stage.setScene(scene);
            ok.setOnMouseClicked((MouseEvent e) -> stage.close());

            stage.showAndWait();
            return;
        } else {
            Stage stage = new Stage();
            GridPane gridPane = new GridPane();
            Label theWinner = new Label("The Winner/s:");
            gridPane.add(theWinner, 0, 0);
            int i = 0;
            for (i = 0; i < Winners.size(); i++) {
                Label wineer = new Label();
                wineer.setText("name " + Winners.get(i).getName() + " score :" + Winners.get(i).getScore());
                gridPane.add(wineer, 0, i + 1);
            }
            Button ok = new Button("OK");
            gridPane.add(ok, 0, i + 1);
            Group group = new Group();
            group.getChildren().add(gridPane);
            Scene scene = new Scene(group);
            stage.setScene(scene);
            ok.setOnMouseClicked((MouseEvent e) -> stage.close());
            stage.showAndWait();
            return;
        }
    }

    @Override
    public void showPlayerLose(Player p) {
        Stage stage = new Stage();
        Label label = new Label(p.getName() + " Has Lost , Score = " + p.getScore());
        Button button = new Button("Ok");
        Group group = new Group();
        GridPane gridPane = new GridPane();
        gridPane.add(label, 0, 1);
        gridPane.add(button, 0, 2);
        group.getChildren().add(gridPane);
        Scene scene = new Scene(group);
        stage.setScene(scene);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent) ->
                stage.close());
        stage.showAndWait();
    }

    @Override
    public void showBoard(Board board) throws Exception {
        window.setTitle("Game");
        if (!boardCreated)
            createBoard(board);
        updateBoard(board);
        boardCreated = true;
    }

    private void updateBoard(Board board) {
        for (int i = 0; i < board.squares.length; i++)
            for (int j = 0; j < board.squares[0].length; j++) {
                try {
                    if (board.squares[i][j].getClosed() && board.squares[i][j].getMarked() || board.squares[i][j].getOpened()) {
                        Color bgColor = Color.valueOf(board.squares[i][j].getLastPlayer().getColor());
                        toggleButtons[i][j].setText(new NormalShowBoardRules().getRepresentive(board.squares[i][j]));
                        toggleButtons[i][j].setBackground(new Background(new BackgroundFill(
                                bgColor
                                , new CornerRadii(5), Insets.EMPTY)));
                        if (bgColor.getBrightness() > 0.178)
                            toggleButtons[i][j].setTextFill(Color.BLACK);
                        else
                            toggleButtons[i][j].setTextFill(Color.WHITE);
                        if (board.squares[i][j].getShieldType() == Square.Shield.Flying && !ShowedNormalShield[i][j]) {
                            // draw shield on buttons

                            ShowedNormalShield[i][j] = true;
                            Image image = new Image(getPathOfImage("Images" + File.separator + "shield1.png"));
                            new ShieldBox(image);

                        } else if (board.squares[i][j].getShieldType() == Square.Shield.Normal && !ShowedNormalShield[i][j]) {
                            ShowedNormalShield[i][j] = true;
                            Image image = new Image(getPathOfImage("Images" + File.separator + "shield1.png"));
                            new ShieldBox(image, "");
                        }
                    } else {
                        if (defaultBackground == null) defaultBackground = toggleButtons[i][j].getBackground();
                        toggleButtons[i][j].setText("");
                        toggleButtons[i][j].setBackground(defaultBackground);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        playerName.setText(currentPlayer.getName());
        playerScore.setText(currentPlayer.getScore() + "");
        playerShields.setText(currentPlayer.getNumberOfShields().toString());

        if (currentPlayer.getWarning() || game.getDisplayMode() || Main.debug) {
            log("player warn");

            playerScore.setVisible(true);
        } else {
            playerScore.setVisible(false);
        }
    }

    private void createBoard(Board board) {
        ShowedNormalShield = new Boolean[board.squares.length][board.squares[0].length];
        for (int i = 0; i < board.squares.length; i++)
            for (int j = 0; j < board.squares[0].length; j++)
                ShowedNormalShield[i][j] = false;
        int n = board.squares.length;
        int m = board.squares[0].length;
        toggleButtons = new ToggleButton[n][m];
        globalGroup = new Group();
        progressBar = new ProgressBar();
        progressBar.setVisible(false);
        GridPane gridPane = new GridPane();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                toggleButtons[i][j] = new ToggleButton();
                double buttonsSize = 30;
                toggleButtons[i][j].setPrefSize(buttonsSize, buttonsSize);
                gridPane.add(toggleButtons[i][j], j, i);
            }
        playerName = new Label(currentPlayer.getName());
        playerScore = new Label(Integer.toString(currentPlayer.getScore()));
        timer = new Label("0 0");
        playerShields = new Label("0");
        autoSaveStat = new Label("Auto save is on");
        playerShields.setTextFill(Color.DARKGREEN);
        timer.setTextFill(Color.RED);
        VBox vBox = new VBox(10);
        for (int i = 0; i < board.squares.length; i++)
            for (int j = 0; j < board.squares[0].length; j++) {
                final int ii = i, jj = j;
                toggleButtons[i][j].setDisable(true);
                toggleButtons[i][j].addEventHandler(MouseEvent.MOUSE_CLICKED, MouseEvent ->
                        {
                            log("Clicked");
                            if (MouseClickIsPrimary(MouseEvent))
                                try {
                                    move = new Move(Move.MoveType.OPEN, new Square(ii, jj));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            else
                                try {
                                    move = new Move(Move.MoveType.FLAG, new Square(ii, jj));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                );
            }
        if (!game.getDisplayMode())
            vBox.getChildren().add(addToolBar("Save", "Quick Save", "Home", "Auto save"));
        HBox hBox = new HBox(5);
        ObservableList<Node> grid2 = hBox.getChildren();
        grid2.add(new Label("Name: "));
        grid2.add(playerName);
        grid2.add(new Label(",Score: "));
        grid2.add(playerScore);
        grid2.add(new Label("Shields: "));
        grid2.add(playerShields);
        grid2.add(new Label("Timer: "));
        grid2.add(timer);
        grid2.add(autoSaveStat);
        vBox.getChildren().add(hBox);
        VBox father = new VBox(10);
        father.getChildren().add(vBox);
        father.getChildren().add(gridPane);
        father.getChildren().add(progressBar);
        globalGroup.getChildren().add(father);
        globslScene = new Scene(globalGroup);
        window.setScene(globslScene);
        globslScene.setFill(Color.SKYBLUE);
        window.show();
    }

    @Override
    public void closeInterface() {
        window.close();
        try {
            Main.gameThread.interrupt();
            Main.timerThread.interrupt();
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public Integer getNumberOfPlayers() {
        VBox vBox = new VBox(20);
        Label label = new Label("Enter number of players");
        final TextField textField = new TextField("1");
        Button button = new Button("Enter");
        vBox.getChildren().addAll(label, textField, button);
        Scene scene = new Scene(vBox);
        window.setScene(scene);
        button.setOnAction((actionEvent) -> {
            try {
                numberOfPlayers = Integer.parseInt(textField.getText());
                window.close();
            } catch (Exception e) {
                new AlertBox("Please enter a valid number");
            }
        });
        scene.setFill(Color.SKYBLUE);
        window.showAndWait();
        return numberOfPlayers;
    }

    @Override
    public Player getPlayerInfo() throws Exception {
        VBox vbox = new VBox();
        ColorPicker colorPicker = new ColorPicker();
        Button enter = new Button();
        enter.setText("ENTER");
        Label name = new Label("enter player name:");
        TextField namePlayer = new TextField(game.getAccount());
        Label color = new Label("enter player color:");
        RadioButton human = new RadioButton("HUMAN");
        human.setSelected(true);
        player = new HumanPlayer();
        RadioButton random = new RadioButton("RANDOM");
        RadioButton easy = new RadioButton("EASY");
        RadioButton hard = new RadioButton("HARD");
        ToggleGroup toggleGroup = new ToggleGroup();
        human.setToggleGroup(toggleGroup);
        random.setToggleGroup(toggleGroup);
        easy.setToggleGroup(toggleGroup);
        hard.setToggleGroup(toggleGroup);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            log(colorPicker.valueProperty().getValue().toString());
            if (newValue == human)
                player = new HumanPlayer();
            else if (newValue == random)
                player = new CPU_Random("shadi", "red");
            else if (newValue == easy)
                player = new CPU_Level1("shadi", "red");
            else
                player = new CPU_NoLose("shadi", "red");

        });
        CheckBox canGetShields = new CheckBox("Can Get Shields?");
        canGetShields.setSelected(true);
        vbox.getChildren().addAll(name, namePlayer, color, colorPicker, human, random, easy, hard, enter, canGetShields);
        Scene scene = new Scene(vbox);
        window.setScene(scene);
        enter.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent) -> {
            String chosenColor = colorPicker.valueProperty().getValue().toString();
            if (usedColors.getOrDefault(chosenColor, false)) {
                new AlertBox("Please choose another color\n this color is used");
            } else {
                player.setName(namePlayer.getText());
                player.setColor(chosenColor);
                if (canGetShields.isSelected()) player.enableShieldAccessibility();
                else player.diableShieldAccessibiltiy();
                usedColors.put(chosenColor, true);
                window.close();
            }
        });
        scene.setFill(Color.SKYBLUE);
        window.showAndWait();


        return player;
    }

    @Override
    public void setCurrentPlayerTurn(Player player) {
        currentPlayer = player;
    }


    private void setDefaultRules(Setting settings) {
        settings.setEmptyClick(10);
        settings.setRighttFlag(5);
        settings.setWrongFlag(1);//make sure if positive or nigative
        settings.setMineClick(250);
        settings.setWarning(10);
        settings.setWhenToLose(0);
        settings.setWinner(100);
        settings.setRoleDurationOfPlayer(10);
        settings.setScoreShield(50);
        settings.setScoreToEarnWhenAShieldIsUsed(250);
        settings.setNumberOfInitialsShields(0);
    }


    private void setDefaultBoard(Setting settings) {
        settings.setBoardType(Setting.BoardType.Random);
        settings.setRow(9);
        settings.setCol(9);
        settings.setMine(9);
        settings.setNumShield(2);
        settings.setNumberOfFlyingShields(1);
    }

    private void setFixedBoard(Setting settings) {
        settings.setBoardType(Setting.BoardType.Fixed);
        settings.setRow(10);
        settings.setCol(10);
        settings.setMine(20);
        settings.setNumShield(3);
        settings.setNumberOfFlyingShields(1);
    }

    public String MainMenu() {
        choice = null;
        Stage stage = new Stage();
        stage.setTitle("Minesweeper v1Beta");
        Label MinSweeper = new Label("Mine Sweeper");
        MinSweeper.setFont(new Font(70));
        ToggleButton startNewGame = new ToggleButton("Start new game");
        ToggleButton loadGame = new ToggleButton("Load Game");
        ToggleButton loadQuickGame = new ToggleButton("Load Quick Game");
        ToggleButton settings = new ToggleButton("Settings");
        ToggleButton scoreBoard = new ToggleButton("Score board");
        ToggleButton displayGame = new ToggleButton("Display old game");
        ToggleButton Account = new ToggleButton("Account");
        ToggleButton exit = new ToggleButton("Exit game");
        final int height = 60;
        final int width = 500;
        int i = 1;
        setMenuButtonStuff(startNewGame, width, height, Color.BISQUE, stage);
        setMenuButtonStuff(loadGame, width - 40 * i++, height, Color.BLUEVIOLET, stage);
        setMenuButtonStuff(loadQuickGame, width - 40 * i++, height, Color.BROWN, stage);
        setMenuButtonStuff(settings, width - 40 * i++, height, Color.GREENYELLOW, stage);
        setMenuButtonStuff(scoreBoard, width - 40 * i++, height, Color.GOLDENROD, stage);
        setMenuButtonStuff(displayGame, width - 40 * i++, height, Color.BROWN, stage);
        setMenuButtonStuff(Account, width - 40 * i++, height, Color.BLUE, stage);
        setMenuButtonStuff(exit, width - 40 * i++, height, Color.RED, stage);
        VBox vBox = new VBox();
        vBox.setSpacing(12);
        vBox.setBackground(setColor(Color.LIGHTSKYBLUE, 10));
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(MinSweeper, startNewGame, loadGame, loadQuickGame, settings, scoreBoard, displayGame, Account, exit);
        Scene scene = new Scene(vBox);
        scene.setFill(Color.SKYBLUE);
        stage.setScene(scene);
        stage.showAndWait();
        return choice;
    }

    private void setMenuButtonStuff(ToggleButton button, int width, int height, Color color, Stage stage) {
        button.setBackground(setColor(color, 10));
        button.setPrefSize(width, height);
        button.setFont(new Font("", 20));
        button.setOnMouseEntered((MouseEvent e) -> {
            button.setTextFill(Color.WHITE);
            button.setScaleX(1.2);
            button.setScaleY(1.2);
            if (button.getText() == "Exit game") {

                Image image = new Image(getPathOfImage("Images" + File.separator + "SurprisedFace.png"));
                button.setGraphic(new ImageView(image));
            }
        });
        button.setOnMouseClicked((MouseEvent e) ->
        {
            choice = button.getText();
            stage.close();
        });
        button.setOnMouseExited((MouseEvent e) -> {
            button.setTextFill(Color.BLACK);
            button.setScaleX(1);
            button.setScaleY(1);
            if (button.getText() == "Exit game")
                button.setGraphic(null);
        });
    }

    private Background setColor(Color color, Integer radius) {
        return new Background(new BackgroundFill(
                color
                , new CornerRadii(radius), Insets.EMPTY));
    }

    private void homeBehavior() {
        closeInterface();
        if (activeWindow == ActiveWindow.Settings) return;
        activeWindow = ActiveWindow.Game;
        main.resetGame();
        try {
            main.start(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Node addToolBar(String... strings) {
        ToolBar toolBar = new ToolBar();
        int cnt = 0;
        for (String s : strings) {
            Button button = new Button(s);
            button.setOnMouseClicked((event) -> {
                try {
                    switch (button.getText()) {
                        case "Save":
                            GameStorageManager.save(game);
                            break;
                        case "Exit":
                            closeInterface();
                            break;
                        case "Home":
                            homeBehavior();
                            break;
                        case "Quick Save":
                            GameStorageManager.save(game, "Quick.bin");
                            break;
                        case "Auto save":
                            autoSave = !autoSave;
                            autoSaveStat.setText(getAutoSave() ? "Auto save is on" : "Auto save is off");

                            break;
                        default:
                            break;


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            button.setPrefHeight(15);
            toolBar.getItems().add(cnt++, button);
        }
        toolBar.setBackground(setColor(Color.GRAY, 1));
        return toolBar;
    }

    public Boolean makeDirIfItDoesntExist(File file) {
        if (!file.exists())
            if (!file.mkdir()) {
                ShowWrongMessage("Couldn't Create Directory, Please run app as administrator \n and give the correct permissions");
                return false;
            }
        return true;
    }

    private InputStream getPathOfImage(String relativePath) {

        try {
            return new FileInputStream("." + File.separator + relativePath);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public String getSavePath() {

        String path = "!";
        File currentDirFile = new File("." + File.separator + "saved");
        Boolean canReach = makeDirIfItDoesntExist(currentDirFile);
        FileChooser fileChooser = new FileChooser();
        if (canReach) fileChooser.setInitialDirectory(currentDirFile);
        fileChooser.setInitialFileName("MineSweeper-Game-" + System.currentTimeMillis());
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            ArrayList<String> fullPath = new ArrayList<>(Arrays.asList(file.toString().split("\\.")));
            if (fullPath.size() == 1) fullPath.add("bin");
            else if (!fullPath.get(fullPath.size() - 1).equals("bin")) {
                fullPath.add("bin");
            }
            path = String.join(".", fullPath);
        }
        return path;

    }

    public String getOpenPath() {
        String path = null;
        File currentDirFile = new File(".");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(currentDirFile);
        File file = fileChooser.showOpenDialog(null);
        if (file != null)
            path = file.toString();
        return path;
    }

    @Override
    public void setGsetting(Setting gsetting) {
        Gsetting = gsetting;
    }

    @Override
    public String editAccount(String Account) {
        Stage stage = new Stage();
        Label yourAccount = new Label("Your Account : ");
        yourAccount.setBackground(setColor(Color.RED, 10));
        TextField editAccount = new TextField();
        if (Account != null)
            editAccount.setText(Account);
        Button Save = new Button("Save");
        Save.setOnMouseClicked((MouseEvent e) ->
        {
            try {
                GameStorageManager.save(editAccount.getText(), GameStorageManager.accountPath);
            } catch (FileNotFoundException ex) {
                ShowWrongMessage("Error :\n " + ex.getMessage() + ", \nPlease check permission or run as administrator \n or move the game to \n another directory");
            } catch (Exception e1) {

            }
            stage.close();
        });
        VBox vBox = new VBox();
        vBox.getChildren().addAll(yourAccount, editAccount, Save);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.showAndWait();
        return editAccount.getText();
    }

    public void ShowWrongMessage(String string) {
        new AlertBox(string);
    }

    @Override
    public void showScoreBoard() {
        final Stage scoreBoardWindow = window;
        final Boolean doLosersCount = true;
        VBox scoreBoardVBox = new VBox(20);
        Label label = new Label("- Score Board - ");
        label.setAlignment(Pos.CENTER);
        HBox choiceHBox = new HBox(20);
        choiceHBox.getChildren().add(new Label("Filter : "));
        ChoiceBox choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                "name", "score", "start"
        ));
        choiceHBox.getChildren().add(choiceBox);
        label.setTextFill(Color.RED);
        label.setFont(Font.font(24));
        Label noPreviousGames = new Label("No Games, Start Playing now!");
        noPreviousGames.setFont(Font.font(16));
        scoreBoardVBox.getChildren().addAll(label, choiceHBox);
        ArrayList<Game> scoreBoard = new ArrayList<>(game.getScoreBoard());
        if (scoreBoard.size() == 0)
            scoreBoardVBox.getChildren().add(noPreviousGames);
        else {
            if (!doLosersCount)
                scoreBoard.removeIf(game -> game.getWinner().get(0).getScore() < game.getSettings().getWhenToLose());

        }
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(
                (ov, value, new_value) -> {
                    scoreBoardVBox.getChildren().remove(scoreBoardVBox.getChildren().size() - 1);
                    switch (new_value.intValue()) {
                        case 0:
                            Game.sortByName(scoreBoard);
                            break;
                        case 1:
                            Game.sortByScore(scoreBoard);
                            break;
                        case 2:
                            Game.sortByDate(scoreBoard);
                            break;
                        default:
                            break;
                    }
                    scoreBoardVBox.getChildren().add(getArranged(scoreBoard));
                }
        );


        scoreBoardVBox.getChildren().add(getArranged(scoreBoard));
        scoreBoardWindow.setScene(new Scene(scoreBoardVBox));
        scoreBoardWindow.show();
    }

    private Node getArranged(ArrayList<Game> games) {
        if (games.size() == 0) return new GridPane();
        GridPane gridPane = new GridPane();
        int id = 0;
        Label IDTitle = new Label("ID");
        Label nameTitle = new Label("name");
        Label scoreTitle = new Label("score");
        Label numberOfShieldsTitle = new Label("Number of exist shields");
        Label numberOfMinesTitle = new Label("Number of exist Mines");
        Label beginTitle = new Label("Begin time");
        Label endTitle = new Label("End Time");
        gridPane.add(IDTitle, id++, 0);
        gridPane.add(nameTitle, id++, 0);
        gridPane.add(scoreTitle, id++, 0);
        gridPane.add(numberOfShieldsTitle, id++, 0);
        gridPane.add(numberOfMinesTitle, id++, 0);
        gridPane.add(beginTitle, id++, 0);
        gridPane.add(endTitle, id++, 0);
        gridPane.setHgap(10);
        gridPane.setVgap(20);
        id = 1;
        int col = 0;
        for (final Game curGame : games) {
            col = 0;
            Label name = new Label(curGame.getWinner().get(0).getName());
            Label score = new Label(curGame.getWinner().get(0).getScore() + "");
            Label ID = new Label(id + "");
            Label numberOfShields = new Label(curGame.getNumberOfExistSheilds() + "");
            Label numberOfMines = new Label(curGame.getNumberOfExistMines() + "");
            Date beginDate = new Date(curGame.getBeginTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Label beginTime = new Label(dateFormat.format(beginDate));
            Date endDate = new Date(curGame.getBeginTime());
            Label endTime = new Label(dateFormat.format(endDate));
            Button ShowGame = new Button("Show Game");
            ShowGame.setOnMouseClicked(
                    e -> {
                        window.close();
                        main.displayGame(curGame);
                    }
            );
            gridPane.add(ID, col++, id);
            gridPane.add(name, col++, id);
            gridPane.add(score, col++, id);
            gridPane.add(numberOfShields, col++, id);
            gridPane.add(numberOfMines, col++, id);
            gridPane.add(beginTime, col++, id);
            gridPane.add(endTime, col++, id);
            gridPane.add(ShowGame, col, id++);
        }
        return gridPane;

    }

    public Boolean getAutoSave() {
        return autoSave;
    }
}
