package ivi.dyndns.org.util;

import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:game_scores.db";  // SQLite adatbázis neve
    private static Connection connection = null;

    // Kapcsolódás az adatbázishoz
    public static void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
                System.out.println("Kapcsolódva az adatbázishoz.");
            }
        } catch (SQLException e) {
            System.out.println("Hiba az adatbázishoz való kapcsolódáskor: " + e.getMessage());
        }
    }

    // Táblák létrehozása (ha nem léteznek)
    public static void createTables() {
        String playersTableSQL = """
                CREATE TABLE IF NOT EXISTS players (
                    id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    name TEXT NOT NULL UNIQUE, 
                    wins INTEGER DEFAULT 0
                );
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(playersTableSQL);
            System.out.println("Táblák létrehozva vagy már léteznek.");
        } catch (SQLException e) {
            System.out.println("Hiba a táblák létrehozásakor: " + e.getMessage());
        }
    }

    // Játékos hozzáadása (ha még nem létezik)
    public static void addPlayerIfNotExists(String playerName) {
        String sql = "INSERT OR IGNORE INTO players (name, wins) VALUES (?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName.toUpperCase()); // Nagybetűs név tárolása
            pstmt.executeUpdate();
            System.out.println("Játékos hozzáadva (ha még nem létezett): " + playerName);
        } catch (SQLException e) {
            System.out.println("Hiba a játékos hozzáadásakor: " + e.getMessage());
        }
    }

    // Játékos nyereményének frissítése
    public static void updateWins(String playerName) {
        String sql = "UPDATE players SET wins = wins + 1 WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName.toUpperCase());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Játékos nyereménye frissítve: " + playerName);
            } else {
                System.out.println("A játékos nem található: " + playerName);
            }
        } catch (SQLException e) {
            System.out.println("Hiba a nyeremény frissítésekor: " + e.getMessage());
        }
    }

    // TOP 10 ranglista lekérdezése
    public static void displayTopScores() {
        String sql = "SELECT name, wins FROM players ORDER BY wins DESC LIMIT 10";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nTOP 10 Ranglista:");
            int rank = 1;
            while (rs.next()) {
                System.out.printf("%d. %s - %d győzelem%n", rank++, rs.getString("name"), rs.getInt("wins"));
            }

        } catch (SQLException e) {
            System.out.println("Hiba a ranglista lekérdezésekor: " + e.getMessage());
        }
    }

    // Játékos keresése
    public static boolean playerExists(String playerName) {
        String sql = "SELECT COUNT(*) FROM players WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Hiba a játékos keresésekor: " + e.getMessage());
        }
        return false;
    }

    // Adatbázis kapcsolat lezárása
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Adatbázis kapcsolat lezárva.");
            }
        } catch (SQLException e) {
            System.out.println("Hiba az adatbázis kapcsolat lezárásakor: " + e.getMessage());
        }
    }
}
