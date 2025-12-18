package amobajatek;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Game {
    final Board board;
    private final Player playerX;
    private final Player playerO;
    private Player currentPlayer;
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

    public boolean lepes(int row, int col) {
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
            return true;
        }

        switchPlayer();

        if (currentPlayer == playerO) {
            ellenfellepes(row, col);
        }
        return false;
    }

    private void ellenfellepes(int lastHumanRow, int lastHumanCol) {
        Random rnd = new Random();

        int[] winPos = findWinningMove(playerO.getSymbol());
        if (winPos != null) {
            board.placeSymbol(winPos[0], winPos[1], playerO.getSymbol());
            System.out.println("Computer nyerő lépést tett!");
            board.printBoard();
            if (checkWin(winPos[0], winPos[1])) {
                System.out.println("Computer nyert!");
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
                row = rnd.nextInt(board.getRows());
                col = rnd.nextInt(board.getCols());
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
                    row = rnd.nextInt(board.getRows());
                    col = rnd.nextInt(board.getCols());
                } while (!board.isEmpty(row, col));
                board.placeSymbol(row, col, playerO.getSymbol());
            }
        }

        board.printBoard();

        if (checkWin(lastHumanRow, lastHumanCol)) {
            System.out.println("Ellenfél nyert!");
            return;
        }

        switchPlayer();
    }

    int[] findWinningMove(char symbol) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
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

    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
    }

    private boolean isValid(int row, int col) {
        return row >= 0 && row < board.getRows() && col >= 0 && col < board.getCols();
    }

    private boolean checkWin(int row, int col) {
        char symbol = currentPlayer.getSymbol();
        return checkDirection(row, col, symbol, 0, 1)
                || checkDirection(row, col, symbol, 1, 0)
                || checkDirection(row, col, symbol, 1, 1)
                || checkDirection(row, col, symbol, 1, -1);
    }

    private boolean checkDirection(int row, int col, char symbol, int dr, int dc) {
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
            for (int r = 0; r < board.getRows(); r++) {
                JSONArray rowArray = new JSONArray();
                for (int c = 0; c < board.getCols(); c++) {
                    rowArray.put(String.valueOf(grid[r][c]));
                }
                boardArray.put(rowArray);
            }

            gameState.put("currentPlayer", currentPlayer.getName());
            gameState.put("board", boardArray);

            try (FileWriter file = new FileWriter(filename)) {
                file.write(gameState.toString(4));
            }

            System.out.println("Játékállás JSON formátumban elmentve: " + filename);
        } catch (IOException e) {
            System.out.println("Hiba a JSON mentés közben: " + e.getMessage());
        }
    }

    public void loadGameFromJson(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject gameState = new JSONObject(content);

            String currentPlayerName = gameState.getString("currentPlayer");
            currentPlayer = currentPlayerName.equals(playerX.getName()) ? playerX : playerO;

            JSONArray boardArray = gameState.getJSONArray("board");
            char[][] grid = board.getGrid();
            for (int r = 0; r < board.getRows(); r++) {
                JSONArray rowArray = boardArray.getJSONArray(r);
                for (int c = 0; c < board.getCols(); c++) {
                    grid[r][c] = rowArray.getString(c).charAt(0);
                }
            }

            System.out.println("Jatekallas JSON fájlból: " + filename);
            board.printBoard();
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Nem található a fájl vagy olvasási hiba: " + e.getMessage());

        } catch (IOException e) {
            System.out.println("Hiba a JSON betöltés közben: " + e.getMessage());
        }
    }
    public void updateHighscore(String winnerName, String filename) throws IOException {
        JSONObject highscores;

        if (Files.exists(Paths.get(filename))) {
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            highscores = new JSONObject(content);
        } else {
            highscores = new JSONObject();
        }

        int currentScore = highscores.optInt(winnerName, 0);
        highscores.put(winnerName, currentScore + 1);

        try (FileWriter file = new FileWriter(filename)) {
            file.write(highscores.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Jelenlegi állás " + winnerName + ": " + (currentScore + 1));
    } }
    


