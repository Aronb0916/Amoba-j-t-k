package example;

public class Player {
    private final String name;
    private final char symbol; // 'X' vagy 'O'

    public Player(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }
}
