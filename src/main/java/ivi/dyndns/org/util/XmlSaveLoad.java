package ivi.dyndns.org.util;

import ivi.dyndns.org.model.GameState;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;

public class XmlSaveLoad {

    private static final String XML_FILE = "game_state.xml";

    // Mentés XML-be
    public static void saveGameStateToXml(GameState gameState) {
        try {
            JAXBContext context = JAXBContext.newInstance(GameState.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(gameState, new File(XML_FILE));
        } catch (JAXBException e) {
            System.err.println("Hiba történt az XML mentés során: " + e.getMessage());
        }
    }

    // Visszatöltés XML-ből
    public static GameState loadGameStateFromXml() {
        try {
            JAXBContext context = JAXBContext.newInstance(GameState.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (GameState) unmarshaller.unmarshal(new File(XML_FILE));
        } catch (JAXBException e) {
            System.err.println("Hiba történt az XML betöltés során: " + e.getMessage());
            return null;
        }
    }
}
