package ivi.dyndns.org.util;

import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:game_scores.db";  // Az SQLite adatbázis fájl neve
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
        String playersTableSQL = "CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "wins INTEGER DEFAULT 0);";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(playersTableSQL);
            System.out.println("Táblák létrehozva.");
        } catch (SQLException e) {
            System.out.println("Hiba a táblák létrehozásakor: " + e.getMessage());
        }
    }

    // Játékos mentése
    public static void savePlayer(String playerName) {
        String sql = "INSERT INTO players (name, wins) VALUES (?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.executeUpdate();
            System.out.println("Játékos mentve: " + playerName);
        } catch (SQLException e) {
            System.out.println("Hiba a játékos mentésekor: " + e.getMessage());
        }
    }

    // Játékos nyereményének frissítése
    public static void updateWins(String playerName) {
        String sql = "UPDATE players SET wins = wins + 1 WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.executeUpdate();
            System.out.println("Játékos nyereménye frissítve: " + playerName);
        } catch (SQLException e) {
            System.out.println("Hiba a nyeremény frissítésekor: " + e.getMessage());
        }
    }

    // Ranglista lekérdezése (összesített)
    public static void printLeaderboard() {
        String sql = "SELECT name, wins FROM players ORDER BY wins DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("Ranglista:");
            while (rs.next()) {
                System.out.println(rs.getString("name") + ": " + rs.getInt("wins"));
            }
        } catch (SQLException e) {
            System.out.println("Hiba a ranglista lekérdezésekor: " + e.getMessage());
        }
    }

    // Játékos keresése
    public static boolean playerExists(String playerName) {
        String sql = "SELECT COUNT(*) FROM players WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Hiba a játékos keresésekor: " + e.getMessage());
        }
        return false;
    }

    // Adatbázis kapcsolat bezárása
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Adatbázis kapcsolat lezárva.");
            }
        } catch (SQLException e) {
            System.out.println("Hiba az adatbázis kapcsolat bezárásakor: " + e.getMessage());
        }
    }
}
