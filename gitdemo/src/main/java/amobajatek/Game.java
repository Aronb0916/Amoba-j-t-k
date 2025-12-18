package amobajatek;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Game {
    final Board board;
    private final Player playerX;
    private final Player playerO;
    Player currentPlayer;
    private boolean aiFirstMove = true;

    public Game(int rows, int cols, String playerName) {
        this.board = new Board(rows, cols);
        this.playerX = new Player(playerName, 'X');
        this.playerO = new Player("Computer", 'O');
        this.currentPlayer = playerX;
    }

    public void start() {
        System.out.println("Játék indul!");
        board.printBoard();
    }

    public boolean lepes(int row, int col) throws IOException {
        if (!isValid(row, col)) {
            System.out.println("Érvénytelen koordináta!");
            return false;
        }
        if (!board.isEmpty(row, col)) {
            System.out.println("A mező foglalt!");
            return false;
        }

        board.placeSymbol(row, col, currentPlayer.getSymbol());
        board.printBoard();

        if (checkWin(row, col)) {
            System.out.println(currentPlayer.getName() + " nyert!");
            updateHighscore(currentPlayer.getName(), "highscores.json");
            return true;
        }

        switchPlayer();

        if (currentPlayer == playerO) {
            ellenfellepes(row, col);
        }
        return false;
    }

    private void ellenfellepes(int lastHumanRow, int lastHumanCol) throws IOException {
        Random rnd = new Random();

        int[] winPos = findWinningMove(playerO.getSymbol());
        if (winPos != null) {
            board.placeSymbol(winPos[0], winPos[1], playerO.getSymbol());
            System.out.println("Computer nyerő lépést tett!");
            board.printBoard();
            if (checkWin(winPos[0], winPos[1])) {
                System.out.println("Computer nyert!");
                updateHighscore(playerO.getName(), "highscores.json");
                return;
            }
            switchPlayer();
            return;
        }

        int[] blockPos = findWinningMove(playerX.getSymbol());
        if (blockPos != null) {
            board.placeSymbol(blockPos[0], blockPos[1], playerO.getSymbol());
            System.out.println("Az ellenfél blokkolta a lépésed!");
            board.printBoard();
            switchPlayer();
            return;
        }

        int row, col;
        if (aiFirstMove) {
            do {
                row = rnd.nextInt(board.getSor());
                col = rnd.nextInt(board.getOszlop());
            } while (!board.isEmpty(row, col));
            board.placeSymbol(row, col, playerO.getSymbol());
            aiFirstMove = false;
        } else {
            List<int[]> neighbors = getNeighbors(lastHumanRow, lastHumanCol);
            Collections.shuffle(neighbors);
            boolean placed = false;
            for (int[] pos : neighbors) {
                if (isValid(pos[0], pos[1]) && board.isEmpty(pos[0], pos[1])) {
                    board.placeSymbol(pos[0], pos[1], playerO.getSymbol());
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                do {
                    row = rnd.nextInt(board.getSor());
                    col = rnd.nextInt(board.getOszlop());
                } while (!board.isEmpty(row, col));
                board.placeSymbol(row, col, playerO.getSymbol());
            }
        }

        board.printBoard();

        if (checkWin(lastHumanRow, lastHumanCol)) {
            System.out.println("Ellenfél nyert!");
            updateHighscore(playerO.getName(), "highscores.json");
            return;
        }

        switchPlayer();
    }

    int[] findWinningMove(char symbol) {
        for (int r = 0; r < board.getSor(); r++) {
            for (int c = 0; c < board.getOszlop(); c++) {
                if (board.isEmpty(r, c)) {
                    board.getGrid()[r][c] = symbol;
                    boolean win = checkWin(r, c);
                    board.getGrid()[r][c] = '.';
                    if (win) {
                        return new int[]{r, c};
                    }
                }
            }
        }
        return null;
    }

    private List<int[]> getNeighbors(int row, int col) {
        List<int[]> list = new ArrayList<>();
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                list.add(new int[]{row + dr, col + dc});
            }
        }
        return list;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
    }

    boolean isValid(int row, int col) {
        return row >= 0 && row < board.getSor() && col >= 0 && col < board.getOszlop();
    }

    private boolean checkWin(int row, int col) {
        char symbol = currentPlayer.getSymbol();
        return checkDirection(row, col, symbol, 0, 1)
                || checkDirection(row, col, symbol, 1, 0)
                || checkDirection(row, col, symbol, 1, 1)
                || checkDirection(row, col, symbol, 1, -1);
    }

    boolean checkDirection(int row, int col, char symbol, int dr, int dc) {
        int count = 1;

        int r = row + dr;
        int c = col + dc;
        while (isValid(r, c) && board.getGrid()[r][c] == symbol) {
            count++;
            r += dr;
            c += dc;
        }
        r = row - dr;
        c = col - dc;
        while (isValid(r, c) && board.getGrid()[r][c] == symbol) {
            count++;
            r -= dr;
            c -= dc;
        }
        return count >= 5;
    }

    public void saveGameAsJson(String filename) {
        try {
            JSONObject gameState = new JSONObject();
            JSONArray boardArray = new JSONArray();

            char[][] grid = board.getGrid();
            for (int r = 0; r < board.getSor(); r++) {
                JSONArray rowArray = new JSONArray();
                for (int c = 0; c < board.getOszlop(); c++) {
                    rowArray.put(String.valueOf(grid[r][c]));
                }
                boardArray.put(rowArray);
            }

            gameState.put("currentPlayer", currentPlayer.getName());
            gameState.put("board", boardArray);

            try (FileWriter file = new FileWriter(filename)) {
                file.write(gameState.toString(4));
            }

            System.out.println("Jatekallas JSON formátumban elmentve: " + filename);
        } catch (IOException e) {
            System.out.println("Hiba a JSON mentés közben: " + e.getMessage());
        }
    }

    public void updateHighscore(String winnerName, String filename) throws IOException {
        highscoresql dao = new highscoresql();
        dao.ensurePlayerExists(playerX.getName());
        dao.ensurePlayerExists(playerO.getName());
        dao.updateScore(winnerName);

        // SQL-ből visszaolvasás
        JSONObject highscores = new JSONObject();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:amoba.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, score FROM highscores")) {

            while (rs.next()) {
                highscores.put(rs.getString("name"), rs.getInt("score"));
            }
        } catch (SQLException e) {
            System.out.println("Hiba az SQL olvasás közben: " + e.getMessage());
        }

        // JSON fájlba írás
        try (FileWriter file = new FileWriter(filename)) {
            file.write(highscores.toString(4));
        }

        System.out.println("Jelenlegi állás (JSON):");
        for (String key : highscores.keySet()) {
            System.out.println(key + ": " + highscores.getInt(key));
        }

        dao.printScores();
    }




    public void loadGameFromJson(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject gameState = new JSONObject(content);

            // jatekos visszaállítása
            String currentPlayerName = gameState.getString("currentPlayer");
            if (currentPlayerName.equals(playerX.getName())) {
                currentPlayer = playerX;
            } else {
                currentPlayer = playerO;
            }

            // tábla visszatöltése
            JSONArray boardArray = gameState.getJSONArray("board");
            char[][] grid = board.getGrid();

            for (int r = 0; r < board.getSor(); r++) {
                JSONArray rowArray = boardArray.getJSONArray(r);
                for (int c = 0; c < board.getOszlop(); c++) {
                    String symbol = rowArray.getString(c);
                    grid[r][c] = symbol.charAt(0);
                }
            }

            System.out.println("Játékállás betöltve a JSON-ból: " + filename);
            board.printBoard();
        } catch (IOException e) {
            System.out.println("Hiba a JSON betöltés közben: " + e.getMessage());
        }

    }

}

