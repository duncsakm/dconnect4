package ivi.dyndns.org.util;

import ivi.dyndns.org.model.GameState;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlSaveLoad {

    private static final String XML_FILE = "game_state.xml";

    // Mentés XML-be
    public static void saveGameStateToXML(int rows, int cols, List<String> players, String moves, String currentPlayer) {
        try {
            GameState gameState = new GameState(players, moves, currentPlayer, rows, cols);

            // Ellenőrizzük, hogy az adatok helyesek-e
            System.out.println("Menteni kívánt GameState objektum:");
            System.out.println("Sorok száma: " + rows);
            System.out.println("Oszlopok száma: " + cols);
            System.out.println("Játékosok: " + players);
            System.out.println("Lépéssorozat: " + moves);
            System.out.println("Aktuális játékos: " + currentPlayer);

            JAXBContext context = JAXBContext.newInstance(GameState.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(gameState, new File(XML_FILE));

            System.out.println("Játék sikeresen mentve XML formátumban.");
        } catch (JAXBException e) {
            System.err.println("Hiba történt az XML mentés során: " + e.getMessage());
            e.printStackTrace(); // A teljes hibaüzenetet kiírjuk
        } catch (Exception e) {
            System.err.println("Ismeretlen hiba történt az XML mentés során: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Betöltés XML-ből
    public static List<String> readXMLSave() {
        try {
            JAXBContext context = JAXBContext.newInstance(GameState.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            GameState gameState = (GameState) unmarshaller.unmarshal(new File(XML_FILE));

            List<String> saveData = new ArrayList<>();
            saveData.add(String.valueOf(gameState.getRows())); // Sorok száma
            saveData.add(String.valueOf(gameState.getCols())); // Oszlopok száma
            saveData.add(gameState.getPlayers().get(0)); // Első játékos neve
            saveData.add(gameState.getPlayers().get(1)); // Második játékos neve
            saveData.add(gameState.getMoves()); // Lépéssorozat

            return saveData;
        } catch (JAXBException e) {
            System.err.println("Hiba történt az XML betöltés során: " + e.getMessage());
            return null;
        }
    }

    // Ellenőrzi, hogy létezik-e az XML mentés
    public static boolean doesXMLSaveExist() {
        File file = new File(XML_FILE);
        return file.exists() && file.isFile();
    }
}
