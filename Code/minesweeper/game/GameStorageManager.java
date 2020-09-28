package minesweeper.game;

import javafx.application.Platform;
import javafx.stage.Stage;
import minesweeper.ui.GraphicalUserInterface;

import java.io.*;
import java.nio.file.Path;
import java.sql.Time;
import java.time.Instant;
import java.util.Date;
import java.util.Timer;

import static sample.Main.log;

public class GameStorageManager {
    private GameStorageManager() {
    }

    private static Thread storageThread;
    public static final String accountPath = "Account.bin";

    private static GraphicalUserInterface ui;

    public static void setUi(GraphicalUserInterface ui) {
        GameStorageManager.ui = ui;
    }

    private static String filePath;

    private static String getFilePath() {
        return filePath;
    }

    private static synchronized void setFilePath(String path) {
        log(filePath);
        filePath = path;
        log(filePath);
    }

    public static void save(final Object object) throws Exception {
        storageThread = new Thread(
                () -> {
                    try {
                        ui.lockGame();
                        setFilePath(null);
                        Platform.runLater(
                                () -> {
                                    String path = ui.getSavePath();
                                    setFilePath(path);
                                }
                        );
                        while (getFilePath() == null) Thread.sleep(1);
                        if (!filePath.equals("!")) {
                            log(filePath);
                            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                            objectOutputStream.writeObject(object);
                            objectOutputStream.close();
                            fileOutputStream.close();
                        }
                    } catch (Exception ignored) {

                    } finally {
                        ui.unlockGame();
                    }
                }
        );
        storageThread.start();
    }

    public static void save(final Object object, String path) throws Exception {
        log("3mlna save");
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    public static Object load(String filePath) throws Exception {
        log("3mlna load");
        FileInputStream fileInputStream = new FileInputStream(filePath);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        return objectInputStream.readObject();
    }
}