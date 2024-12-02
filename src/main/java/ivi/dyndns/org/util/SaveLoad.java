package ivi.dyndns.org.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveLoad {
    private static final String SAVE_FILE = "gamestate.txt";

    // Ellenőrzi, hogy létezik-e mentett állapot
    public static boolean doesSaveFileExist() {
        File file = new File(SAVE_FILE);
        return file.exists();
    }

    // Játékállapot mentése
    public static void saveGameState(int rows, int cols, List<String> players, String moves) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            writer.write(rows + "\n"); // Sorok száma
            writer.write(cols + "\n"); // Oszlopok száma
            writer.write(players.get(0) + "\n"); // Első játékos neve
            writer.write(players.get(1) + "\n"); // Második játékos neve
            writer.write(moves + "\n"); // Játékosok lépései
        } catch (IOException e) {
            System.err.println("Nem sikerült menteni a játékállapotot: " + e.getMessage());
        }
    }

    // Mentett játék betöltése
    public static List<String> readSaveFile() {
        List<String> saveData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                saveData.add(line);
            }
        } catch (IOException e) {
            System.err.println("Nem sikerült betölteni a mentést: " + e.getMessage());
        }
        return saveData;
    }

    // Mentés törlése (játék vége esetén)
    public static void clearSaveFile() {
        try {
            File file = new File(SAVE_FILE);
            if (file.exists()) {
                if (!file.delete()) {
                    System.err.println("Nem sikerült törölni a mentett állományt.");
                }
            }
        } catch (Exception e) {
            System.err.println("Hiba történt a mentés törlésekor: " + e.getMessage());
        }
    }
}
