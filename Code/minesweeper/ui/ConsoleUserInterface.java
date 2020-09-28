
package minesweeper.ui;

import minesweeper.game.*;
import minesweeper.player.*;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Deprecated
public class ConsoleUserInterface implements UserInterface {

    @Override
    public void showInterface() {

    }

    @Override
    public Move waitMove(Board board) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String state = scanner.nextLine();
        Integer rows = board.squares.length;
        Integer columns = board.squares[0].length;
        Pattern p = Pattern.compile("^-?([a-zA-Z]+)([1-9][0-9]*)$");
        Matcher matcher = p.matcher(state);
        if (matcher.matches()) {
            Boolean flag = state.charAt(0) == '-';
            String _rowIdentifier = matcher.group(1).toLowerCase();
            Integer rowIdentifier = 0;
            for (int i = 0; i < _rowIdentifier.length(); i++) {
                rowIdentifier += ((_rowIdentifier.charAt(i) - 'a') + 1) *
                        ((int) Math.pow(26, _rowIdentifier.length() - i - 1));
            }
            Integer columnIdentifier = Integer.parseInt(matcher.group(2));
            if (rowIdentifier > rows || columnIdentifier > columns) throw GameExceptions.ILLEGAL_MOVE;
            return new Move(flag ? Move.MoveType.FLAG : Move.MoveType.OPEN,
                    new Square(rowIdentifier - 1, columnIdentifier - 1));
        } else {
            throw GameExceptions.ILLEGAL_MOVE;

        }
    }

    @Override
    public void showExtraInfo(Object infoObject, Board board) {
        ArrayList<Player> Winners = (ArrayList<Player>) infoObject;
        if (Winners == null) return;
        if (Winners.get(0).getScore() < 0) {
            System.out.println("NO ONE HAD WON ");
            return;
        }
        System.out.println("THE WINNER/s:");
        for (int i = 0; i < Winners.size(); i++) {
            System.out.println(i + 1 + "-" + Winners.get(i).getName() + " Score: " + Winners.get(i).getScore());
        }

        try {
            showBoard(board);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showPlayerLose(Player p) {
        System.out.println(p.getName() + " Has Lost , Score = " + p.getScore());
    }

    private String NumberToString(Integer i) {
        StringBuilder result = new StringBuilder();
        Integer mod = i % 26;
        result.insert(0, (char) ((int) 'A' + mod));
        i /= 26;
        while (i != 0) {
            mod = i % 27;
            result.insert(0, (char) ((int) 'A' - 1 + mod));
            i /= 27;
        }
        return result.toString();
    }

    @Override
    public void showBoard(Board board) throws Exception {
        ArrayList<String> Numbers = new ArrayList<>();
        Integer max = 0;
        for (int i = 0; i < board.squares.length; i++) {
            String C = NumberToString(i);
            if (C.length() > max) max = C.length();
            Numbers.add(NumberToString(i));
        }
        String columnsSpaces = "";
        String rowsSpaces = " ";
        for (int i = 0; i < String.valueOf(board.squares.length).length(); i++) rowsSpaces += " ";
        for (int i = 0; i < max; i++) columnsSpaces += " ";
        System.out.print(rowsSpaces);
        for (int n = 0; n < board.squares[0].length; n++) {
            System.out.print(n + 1);
            System.out.print(columnsSpaces);
        }
        System.out.println();
        for (int i = 0; i < board.squares.length; i++) {
            System.out.print(Numbers.get(i));
            System.out.print(rowsSpaces);
            for (int j = 0; j < board.squares[0].length; j++)
                System.out.print(new NormalShowBoardRules().getRepresentive(board.squares[i][j]) + columnsSpaces);
            System.out.println();
        }
    }

    @Override
    public void closeInterface() {

    }

    @Override
    public Integer getNumberOfPlayers() {
        System.out.println("Enter number of players : ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    @Override
    public Player getPlayerInfo() throws Exception {
        System.out.println("Enter type of player : ");
        System.out.println("1- for human player");
        System.out.println("2- for CPU Random");
        System.out.println("3- for CPU Easy");
        System.out.println("4- for CPU Hard");


        Player player;
        Scanner scanner = new Scanner(System.in);
        Integer choice = scanner.nextInt();
        switch (choice) {
            case 1:
                player = new HumanPlayer();
                break;
            case 2:
                player = new CPU_Random("shadi", "red");
                break;
            case 3:
                player = new CPU_Level1("shadi", "red");
                break;
            case 4:
                player = new CPU_NoLose("shadi", "red");
                break;
            default:
                System.out.println("Not known");
                throw GameExceptions.INVALID_DATA;
        }
        scanner = new Scanner(System.in);
        System.out.println("Enter player name : ");
        String s = scanner.nextLine();
        player.setName(s);
        System.out.println("Enter Player's color : ");
        s = scanner.nextLine();
        player.setColor(s);
        return player;
    }

    @Override
    public void setCurrentPlayerTurn(Player player) {
        System.out.print("Current Player : " + player.getName());
        if (player.getWarning())
            System.out.print(", Score : " + player.getScore());
        System.out.println();
    }

    @Override
    public void setGsetting(Setting gsetting) {

    }

    @Override
    public String editAccount(String Accountٍ) {
        return null;
    }

    @Override
    public void ShowWrongMessage(String string) {

    }

    @Override
    public void showScoreBoard() {

    }


    private Integer getPositiveInteger() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer number = scanner.nextInt();
            if (number <= 0) throw new Exception();
            return number;
        } catch (InputMismatchException ex) {
            System.out.println("Wrong input, enter numbers");
            return getPositiveInteger();
        } catch (Exception e) {
            System.out.println("Must be positive");
            return getPositiveInteger();
        }
    }

    private Integer getPositiveIntegerOrZero() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer number = scanner.nextInt();
            if (number < 0) throw new Exception();
            return number;
        } catch (InputMismatchException ex) {
            System.out.println("Wrong input, enter numbers");
            return getPositiveIntegerOrZero();
        } catch (Exception e) {
            System.out.println("Must be positive or zero ");
            return getPositiveIntegerOrZero();
        }
    }

    private Integer getInteger() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer number = scanner.nextInt();
            return number;
        } catch (InputMismatchException ex) {
            System.out.println("Wrong input, enter numbers");
            return getInteger();
        }
        /* i put return getInteger instead of this-->
        finally {
            System.out.println("Enter again : ");
            return getPositiveInteger();
        }*/
    }


    @Override
    public Setting getSettings() {
        Setting settings = new Setting();
        getBoardRoulseSetting(settings);
        getBoardSetting(settings);
        return settings;
    }

    public void getBoardSetting(Setting settings) {
        Integer choice;
        System.out.println("1- for fixed board ");
        System.out.println("2- for random default board");
        System.out.println("3- for random custom board");
        while (true) {
            choice = getPositiveInteger();
            if (choice == 1 || choice == 2 || choice == 3)
                break;
        }
        switch (choice) {
            case 1:
                settings.setBoardType(Setting.BoardType.Fixed);
                settings.setRow(9);
                settings.setCol(9);
                settings.setMine(9);
                break;
            case 2:
                settings.setBoardType(Setting.BoardType.Random);
                settings.setRow(10);
                settings.setCol(10);
                settings.setMine(20);
                break;
            case 3:
                //BOARD TYPE
                settings.setBoardType(Setting.BoardType.Random);
                System.out.println("Enter number of rows : ");
                Integer rows = getPositiveInteger();
                settings.setRow(rows);


                System.out.println("Enter number of columns : ");
                Integer columns = getPositiveInteger();
                settings.setCol(columns);


                System.out.println("Enter number of mines : ");
                Integer mines = getPositiveInteger();
                while (mines >= rows * columns) {
                    System.out.println("Must be less than grid size, Enter again : ");
                    mines = getPositiveInteger();
                }
                settings.setMine(mines);

                break;
        }

    }

    public void getBoardRoulseSetting(Setting settings) {
        Integer choice;
        System.out.println("1- for default rules");
        System.out.println("2- for custom rules");
        while (true) {
            choice = getPositiveInteger();
            if (choice == 1 || choice == 2)
                break;
        }
        switch (choice) {
            case 1: {
                settings.setEmptyClick(10);
                settings.setRighttFlag(5);
                settings.setWrongFlag(1);//make sure if positive or nigative
                settings.setMineClick(250);
                settings.setWarning(10);
                settings.setWhenToLose(0);
                settings.setWinner(100);
                break;
            }
            case 2: {
                System.out.println("Score to lose when a flag is misplaced ");
                Integer wrongFlag = getPositiveIntegerOrZero();
                settings.setWrongFlag(wrongFlag);


                System.out.println("Score to lose when a mine is opened ");
                Integer mineScore = getPositiveIntegerOrZero();
                settings.setMineClick(mineScore);


                System.out.println("Score to get when a flag is placed correctly : ");
                Integer flagRight = getPositiveIntegerOrZero();
                settings.setRighttFlag(flagRight);


                System.out.println("Score to get when an empty square is opened : ");
                Integer emptyClickScore = getPositiveIntegerOrZero();
                settings.setEmptyClick(emptyClickScore);


                System.out.println("max score to lose : ");
                Integer scoreToLose = getInteger();
                settings.setWhenToLose(scoreToLose);


                System.out.println("max score to warn : ");
                Integer scoreToWarn = getInteger();
                settings.setWarning(scoreToWarn);


                System.out.println("last move score : ");
                Integer lastMoveScore = getInteger();
                settings.setWinner(lastMoveScore);
                break;
            }
        }
    }
}
//remember to try "&" thing
