package ivi.dyndns.org.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement
public class GameState {

    private List<String> players;
    private String moves;
    private String currentPlayer;
    private int rows;
    private int cols;

    // Alapértelmezett konstruktor szükséges JAXB-hez
    public GameState() {}

    public GameState(List<String> players, String moves, String currentPlayer, int rows, int cols) {
        if (players == null || players.size() != 2 || moves == null || currentPlayer == null || rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Érvénytelen játékállapot adatok.");
        }

        this.players = players;
        this.moves = moves;
        this.currentPlayer = currentPlayer;
        this.rows = rows;
        this.cols = cols;
    }

    // Getterek és setterek
    @XmlElement
    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    @XmlElement
    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }

    @XmlElement
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @XmlElement
    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    @XmlElement
    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }
}
