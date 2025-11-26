package example;

public class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid;

    public Board(int rows, int cols) {
        if (rows < 4 || cols < 4 || cols > rows || rows > 25) {
            throw new IllegalArgumentException("Érvénytelen méret: 4 <= M <= N <= 25 és M <= N");
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = '.';
            }
        }
    }

    public void printBoard() {
        System.out.print("   ");
        for (int c = 0; c < cols; c++) {
            System.out.print((char) ('a' + c));
            System.out.print(' ');
        }
        System.out.println();
        for (int r = 0; r < rows; r++) {
            System.out.printf("%2d ", r + 1);
            for (int c = 0; c < cols; c++) {
                System.out.print(grid[r][c]);
                System.out.print(' ');
            }
            System.out.println();
        }
    }

    public boolean isEmpty(int row, int col) {
        return grid[row][col] == '.';
    }

    public void placeSymbol(int row, int col, char symbol) {
        if (!isEmpty(row, col)) {
            throw new IllegalArgumentException("A mező foglalt!");
        }
        grid[row][col] = symbol;
    }

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
