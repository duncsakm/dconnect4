package ivi.dyndns.org.service;

import ivi.dyndns.org.model.Board;
import ivi.dyndns.org.util.DatabaseManager;
import ivi.dyndns.org.util.SaveLoad;
import ivi.dyndns.org.util.XmlSaveLoad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Board board;
    private List<String> players;
    private String currentPlayer;
    private String moves = "";
    private final Scanner scanner = new Scanner(System.in);

    public void play() {
        DatabaseManager.connect(); // Kapcsolódás az adatbázishoz
        DatabaseManager.createTables();
        boolean exitGame = false;

        while (!exitGame) {
            System.out.println("Menü:");
            System.out.println("N - Új játék");
            if (SaveLoad.doesSaveFileExist()) { // Félbemaradt játék ellenőrzése
                System.out.println("R - Félbemaradt játék betöltése");
            }
            System.out.println("L - Mentett játék betöltése");
            System.out.println("T - Top 10 játékos");
            System.out.println("X - Kilépés");
            System.out.print("Válassz egy lehetőséget: ");

            String command = scanner.nextLine().toUpperCase();

            switch (command) {
                case "N":
                    startNewGame();
                    break;
                case "L":
                    loadGameFromXML();
                    break;
                case "R":
                    if (SaveLoad.doesSaveFileExist()) {
                        loadGameFromTXT();
                    } else {
                        System.out.println("Nincs félbemaradt játék.");
                    }
                    break;
                case "T":
                    displayTopScores();
                    break;
                case "X":
                    System.out.println("Kilépés a játékból...");
                    exitGame = true;
                    break;
                default:
                    System.out.println("Érvénytelen parancs. Próbáld újra!");
            }
        }
    }


    private void loadGameFromTXT() {
        if (SaveLoad.doesSaveFileExist()) {
            System.out.println("Mentett játék betöltése TXT-ből...");
            List<String> saveData = SaveLoad.readSaveFile();
            initializeGameFromSave(saveData);
        } else {
            System.out.println("Nincs mentett játék TXT formátumban.");
        }
    }

    private void loadGameFromXML() {
        File xmlFile = new File("game_state.xml");
        if (XmlSaveLoad.doesXMLSaveExist()) {
            // Ellenőrizzük, hogy az XML fájl nem üres-e
            if (xmlFile.length() == 0) {
                System.out.println("A mentett játék XML fájl üres.");
            } else {
                System.out.println("Mentett játék betöltése XML-ből...");
                List<String> saveData = XmlSaveLoad.readXMLSave();
                initializeGameFromSave(saveData);
            }
        } else {
            System.out.println("Nincs mentett játék XML formátumban.");
        }
    }



    private void saveGameToXML() {
        if (board == null) {
            System.out.println("Nincs aktív játék, amit menthetnénk.");
            return;
        }

        // Kiíratjuk, hogy mit mentene az XML-be
        /*System.out.println("Menteni kívánt játékállapot:");
        System.out.println("Sorok száma: " + board.getRows());
        System.out.println("Oszlopok száma: " + board.getCols());
        System.out.println("Játékosok: " + players);
        System.out.println("Lépéssorozat: " + moves);
        System.out.println("Aktuális játékos: " + currentPlayer);*/

        System.out.println("Játék mentése XML formátumban...");
        XmlSaveLoad.saveGameStateToXML(board.getRows(), board.getCols(), players, moves, currentPlayer);
    }

    private void displayTopScores() {
        System.out.println("Legjobb 10 játékos:");
        DatabaseManager.displayTopScores();
    }

    private void startNewGame() {
        System.out.println("Új játék indítása...");
        SaveLoad.clearSaveFile();
        initializeNewGame();
        gameLoop();
    }

    private void initializeGameFromSave(List<String> saveData) {
        int rows = Integer.parseInt(saveData.get(0));
        int cols = Integer.parseInt(saveData.get(1));
        players = new ArrayList<>();
        players.add(saveData.get(2));
        players.add(saveData.get(3));
        currentPlayer = players.get(saveData.get(4).length() % 2);
        moves = saveData.size() > 4 ? saveData.get(4) : "";

        board = new Board(rows, cols);
        replayMoves();
        gameLoop();
    }

    private void initializeNewGame() {
        System.out.println("Adja meg a pálya méretét (4 <= oszlopok száma <= sorok száma <= 12):");
        int rows = 0, cols = 0;

        while (rows < 4 || rows > 12 || cols < 4 || cols > rows) {
            System.out.print("Sorok száma: ");
            rows = scanner.nextInt();
            System.out.print("Oszlopok száma: ");
            cols = scanner.nextInt();
            scanner.nextLine(); // Sorvége karakter kezelése
        }

        board = new Board(rows, cols);
        players = new ArrayList<>();

        String player1, player2;
        while (true) {
            System.out.print("Adja meg az első játékos nevét (sárga korong - O): ");
            player1 = scanner.nextLine().trim().toUpperCase();
            if (player1.isEmpty()) {
                System.out.println("A játékos neve nem lehet üres. Próbáld újra!");
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("Adja meg a második játékos nevét (piros korong - X): ");
            player2 = scanner.nextLine().trim().toUpperCase();
            if (player2.isEmpty()) {
                System.out.println("A játékos neve nem lehet üres. Próbáld újra!");
                continue;
            }
            if (player1.equals(player2)) {
                System.out.println("A két játékos neve nem lehet azonos. Próbáld újra!");
                continue;
            }
            break;
        }

        players.add(player1);
        players.add(player2.equals("AI") ? "AI" : player2);

        currentPlayer = players.get(0);
        moves = "";
        SaveLoad.saveGameState(board.getRows(), board.getCols(), players, moves);
    }

    private void replayMoves() {
        char currentDisc = 'O';
        for (char move : moves.toCharArray()) {
            int col = move - 'A';
            board.dropDisc(col, currentDisc);
            currentDisc = (currentDisc == 'O') ? 'X' : 'O';
        }
    }

    private void gameLoop() {
        boolean gameOver = false;

        while (!gameOver) {
            board.display();
            System.out.println(currentPlayer + " következik.");

            if (isAIPlayer(currentPlayer)) {
                System.out.println("AI gondolkodik...");
                int col = getAIMove();
                System.out.println("AI választott oszlop: " + (char) ('A' + col));

                if (!board.dropDisc(col, getDiscForCurrentPlayer())) {
                    System.out.println("AI hibát követett el: próbál újra.");
                    continue; // Újrapróbálkozás
                }

                saveGameStep(col);

                if (board.checkWin()) {
                    board.display();
                    System.out.println(currentPlayer + " nyert!");
                    clearGameStateOnGameOver();
                    DatabaseManager.addPlayerIfNotExists(currentPlayer);
                    DatabaseManager.updateWins(currentPlayer);
                    gameOver = true;
                } else if (board.isFull()) {
                    board.display();
                    System.out.println("Döntetlen!");
                    clearGameStateOnGameOver();
                    gameOver = true;
                } else {
                    switchPlayer();
                }

            } else {
                System.out.println("Válasszon oszlopot (A-" + (char) ('A' + board.getCols() - 1) + "), vagy használja: S - Mentés, X - Menübe lépés");
                String input = scanner.nextLine().toUpperCase();

                if ("S".equals(input)) {
                    saveGameToXML();
                    System.out.println("Játékállapot elmentve XML-be.");
                    continue;
                } else if ("X".equals(input)) {
                    System.out.println("Visszalépés a főmenübe...");
                    return;
                } else if (input.length() == 1) {
                    int col = input.charAt(0) - 'A';
                    if (col >= 0 && col < board.getCols()) {
                        if (!board.dropDisc(col, getDiscForCurrentPlayer())) {
                            System.out.println("Ez az oszlop tele van. Próbáld másikat!");
                            continue;
                        }

                        saveGameStep(col);

                        if (board.checkWin()) {
                            board.display();
                            System.out.println(currentPlayer + " nyert!");
                            clearGameStateOnGameOver();
                            DatabaseManager.addPlayerIfNotExists(currentPlayer);
                            DatabaseManager.updateWins(currentPlayer);
                            gameOver = true;
                        } else if (board.isFull()) {
                            board.display();
                            System.out.println("Döntetlen!");
                            clearGameStateOnGameOver();
                            gameOver = true;
                        } else {
                            switchPlayer();
                        }
                    } else {
                        System.out.println("Érvénytelen oszlop. Próbáld újra!");
                    }
                } else {
                    System.out.println("Érvénytelen bemenet. Próbáld újra!");
                }
            }
        }
    }



    private int getPlayerMove() {
        int col = -1;
        while (col < 0 || col >= board.getCols()) {
            System.out.print("Válasszon egy oszlopot (A-" + (char) ('A' + board.getCols() - 1) + "): ");
            String input = scanner.nextLine().toUpperCase();

            if (input.length() == 1) {
                col = input.charAt(0) - 'A';
            }
        }
        return col;
    }

    private int getAIMove() {
        Random random = new Random();
        int col;

        do {
            col = random.nextInt(board.getCols());
        } while (!board.isColumnValid(col));

        return col;
    }

    private char getDiscForCurrentPlayer() {
        return currentPlayer.equals(players.get(0)) ? 'O' : 'X';
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals(players.get(0)) ? players.get(1) : players.get(0);
    }

    private void saveGameStep(int col) {
        moves += (char) ('A' + col);
        SaveLoad.saveGameState(board.getRows(), board.getCols(), players, moves);
    }

    private void clearGameStateOnGameOver() {
        System.out.println("A játék véget ért. Mentés törölve.");
        SaveLoad.clearSaveFile();
    }

    private boolean isAIPlayer(String player) {
        return player.equals("AI");
    }
}
