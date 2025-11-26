package amobajatek;

public class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = '.'; // üres mező
            }
        }
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

    public boolean isEmpty(int row, int col) {
        return grid[row][col] == '.';
    }

    public void placeSymbol(int row, int col, char symbol) {
        grid[row][col] = symbol;
    }

    public void printBoard() {
        System.out.println();
        System.out.print("   ");
        for (int c = 0; c < cols; c++) {
            char colLabel = (char) ('a' + c);
            System.out.print(colLabel + " ");
        }
        System.out.println();
        for (int r = 0; r < rows; r++) {
            String rowLabel = String.valueOf(r + 1);
            if (rowLabel.length() == 1) {
                rowLabel = " " + rowLabel;
            }
            System.out.print(rowLabel + " ");
            for (int c = 0; c < cols; c++) {
                System.out.print(grid[r][c] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
