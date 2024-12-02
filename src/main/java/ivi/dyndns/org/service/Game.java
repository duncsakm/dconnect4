package ivi.dyndns.org.service;

import ivi.dyndns.org.model.Board;
import ivi.dyndns.org.util.SaveLoad;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private Board board;
    private List<String> players;
    private String currentPlayer;
    private String moves = "";
    private final Scanner scanner = new Scanner(System.in);

    public void play() {
        initializeGame();
        boolean gameOver = false;

        while (!gameOver) {
            board.display();
            System.out.println(currentPlayer + " következik.");
            int col = getPlayerMove();

            if (!board.dropDisc(col, getDiscForCurrentPlayer())) {
                System.out.println("Ez az oszlop tele van. Próbálkozz máshol!");
                continue;
            }

            saveGameStep(col);

            if (board.checkWin()) {
                board.display();
                System.out.println(currentPlayer + " nyert!");
                clearGameStateOnGameOver();
                gameOver = true;
            } else if (board.isFull()) {
                board.display();
                System.out.println("Döntetlen!");
                clearGameStateOnGameOver();
                gameOver = true;
            } else {
                switchPlayer();
            }
        }
    }

    private void initializeGame() {
        System.out.println("Adja meg a pálya méretét (4 <= oszlopok száma <= sorok száma <= 12):");
        int rows = 0, cols = 0;

        while (rows < 4 || rows > 12 || cols < 4 || cols > rows) {
            System.out.print("Sorok száma: ");
            rows = scanner.nextInt();
            System.out.print("Oszlopok száma: ");
            cols = scanner.nextInt();
        }

        if (SaveLoad.doesSaveFileExist()) {
            List<String> saveData = SaveLoad.readSaveFile();
            int savedRows = Integer.parseInt(saveData.get(0));
            int savedCols = Integer.parseInt(saveData.get(1));

            if (savedRows == rows && savedCols == cols) {
                System.out.println("Mentett játék betöltése...");
                players = new ArrayList<>();
                players.add(saveData.get(2)); // Első játékos neve
                players.add(saveData.get(3)); // Második játékos neve
                currentPlayer = players.get(saveData.get(4).length() % 2); // Aktuális játékos a lépések alapján
                moves = saveData.size() > 4 ? saveData.get(4) : ""; // Lépéssorozat

                board = new Board(rows, cols);
                replayMoves(); // Végrehajtjuk a mentett lépéseket
                return;
            } else {
                System.out.println("A táblaméret eltér. Új játék indul.");
                SaveLoad.clearSaveFile();
            }
        }

        setupNewGame(rows, cols);
    }

    private void setupNewGame(int rows, int cols) {
        board = new Board(rows, cols);
        players = new ArrayList<>();

        System.out.print("Adja meg az első játékos nevét (sárga korong - O): ");
        players.add(scanner.next());
        System.out.print("Adja meg a második játékos nevét (piros korong - X): ");
        players.add(scanner.next());

        currentPlayer = players.get(0);

        SaveLoad.saveGameState(rows, cols, players, moves);
    }

    private void replayMoves() {
        char currentDisc = 'O';
        for (char move : moves.toCharArray()) {
            int col = move - 'A';
            board.dropDisc(col, currentDisc);
            currentDisc = (currentDisc == 'O') ? 'X' : 'O';
        }
    }

    private int getPlayerMove() {
        int col = -1;
        while (col < 0 || col >= board.getCols()) {
            System.out.print("Válasszon egy oszlopot (A-" + (char) ('A' + board.getCols() - 1) + "): ");
            String input = scanner.next().toUpperCase();

            if (input.length() == 1) {
                col = input.charAt(0) - 'A';
            }
        }
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
}
