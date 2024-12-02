package ivi.dyndns.org.model;

import java.util.List;

public class GameState {
    private List<String> players;  // A játékosok nevei
    private String moves;  // A játékosok lépéseinek sorozata (például "ABBCA...")
    private String currentPlayer;  // A következő játékos
    private int rows;
    private int cols;

    public GameState(List<String> players, String moves, String currentPlayer, int rows, int cols) {
        this.players = players;
        this.moves = moves;
        this.currentPlayer = currentPlayer;
        this.rows = rows;
        this.cols = cols;
    }

    public List<String> getPlayers() {
        return players;
    }

    public String getMoves() {
        return moves;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
