package ivi.dyndns.org.model;

public class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid;
    private int lastRow = -1;
    private int lastCol = -1;

    // ANSI színek a kiíráshoz
    public static final String RESET = "\u001B[0m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";

    // Konstruktor új tábla létrehozásához
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = ' '; // Üres mezők
            }
        }
    }

    // Lépések alapján a tábla visszaállítása
    public void applyMoves(String moves) {
        char currentDisc = 'O'; // Első játékos (sárga) kezd
        for (char column : moves.toCharArray()) {
            int col = column - 'A';
            if (col >= 0 && col < cols) {
                dropDisc(col, currentDisc);
                currentDisc = (currentDisc == 'O') ? 'X' : 'O'; // Váltás a másik játékosra
            }
        }
    }

    // Korong elhelyezése a táblán
    public boolean dropDisc(int col, char disc) {
        if (col < 0 || col >= cols) return false;
        for (int i = rows - 1; i >= 0; i--) {
            if (grid[i][col] == ' ') {
                grid[i][col] = disc;
                lastRow = i;
                lastCol = col;
                return true;
            }
        }
        return false; // Ha az oszlop tele van
    }

    // Nyertes állapot ellenőrzése
    public boolean checkWin() {
        if (lastRow == -1 || lastCol == -1) return false; // Nincs lépés, nincs nyertes
        char disc = grid[lastRow][lastCol];
        return checkDirection(disc, 1, 0) // Vízszintes
                || checkDirection(disc, 0, 1) // Függőleges
                || checkDirection(disc, 1, 1) // Főátló
                || checkDirection(disc, 1, -1); // Mellékátló
    }

    // Egy adott irány ellenőrzése
    private boolean checkDirection(char disc, int dRow, int dCol) {
        int count = 1; // Az aktuális mező már számít
        count += countInDirection(disc, dRow, dCol); // Előre
        count += countInDirection(disc, -dRow, -dCol); // Hátra
        return count >= 4;
    }

    // Azonos korongok számlálása egy irányban
    private int countInDirection(char disc, int dRow, int dCol) {
        int count = 0;
        int r = lastRow + dRow;
        int c = lastCol + dCol;
        while (r >= 0 && r < rows && c >= 0 && c < cols && grid[r][c] == disc) {
            count++;
            r += dRow;
            c += dCol;
        }
        return count;
    }

    // Tábla megtelt-e
    public boolean isFull() {
        for (int i = 0; i < cols; i++) {
            if (grid[0][i] == ' ') return false; // Még van üres hely az oszlopok tetején
        }
        return true;
    }

    // Tábla megjelenítése
    // A tábla kiíratása
    public void display() {
        int rowNumberWidth = String.valueOf(rows).length(); // Sorszámok szélessége a legnagyobb sorszám alapján

        // Fejléc kiíratása (oszlopok betűjele)
        System.out.print(" ".repeat(rowNumberWidth + 2)); // Igazítás a sorszámok szélessége szerint (+3 a kerethez)
        for (int i = 0; i < cols; i++) {
            System.out.print(" " + (char) ('A' + i) + "  ");
        }
        System.out.println();

        // Felső keret
        System.out.print(" ".repeat(rowNumberWidth + 1) + "╔");
        for (int i = 0; i < cols - 1; i++) {
            System.out.print("═══╦");
        }
        System.out.println("═══╗");

        // Sorok kiíratása
        for (int i = 0; i < rows; i++) {
            // Sorszám kiíratása
            System.out.printf("%" + rowNumberWidth + "d ║", i + 1);
            for (int j = 0; j < cols; j++) {
                System.out.print(" " + getColoredDisc(grid[i][j]) + " ║");
            }
            System.out.println();

            // Alsó keret vagy középső elválasztók
            if (i < rows - 1) {
                System.out.print(" ".repeat(rowNumberWidth + 1) + "╠");
                for (int j = 0; j < cols - 1; j++) {
                    System.out.print("═══╬");
                }
                System.out.println("═══╣");
            }
        }

        // Alsó keret
        System.out.print(" ".repeat(rowNumberWidth + 1) + "╚");
        for (int i = 0; i < cols - 1; i++) {
            System.out.print("═══╩");
        }
        System.out.println("═══╝");
    }

    // Színezett korongok kiírása
    private String getColoredDisc(char disc) {
        return switch (disc) {
            case 'O' -> YELLOW + "O" + RESET;
            case 'X' -> RED + "O" + RESET;
            default -> " ";
        };
    }

    // Getterek
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public char[][] getGrid() {
        return grid;
    }
}
