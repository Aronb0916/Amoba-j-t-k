package amobajatek;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(5, 5, "TesztJatekos");
    }

    @Test
    void testValidMove() throws IOException {
        boolean result = game.lepes(0, 0);
        assertFalse(result);
    }

    @Test
    void testInvalidMove() throws IOException {
        boolean result = game.lepes(-1, -1);
        assertFalse(result);
    }

    @Test
    void testOccupiedCell() throws IOException {
        assertFalse(game.lepes(2, 2));
        assertFalse(game.lepes(2, 2));
    }

    @Test
    void testSaveAndLoadGame() throws IOException {
        game.lepes(0, 0);
        game.saveGameAsJson("tesztmentes.json");

        Game ujGame = new Game(5, 5, "Teszt");
        ujGame.loadGameFromJson("tesztmentes.json");

        // ⚠️ Itt hibás az assertEquals hívásod
        // assertEquals('X','.', ujGame.board.getGrid()[0][0]);  <-- rossz sorrend
        // helyesen:
        assertEquals('X', ujGame.board.getGrid()[0][0]);
    }

    @Test
    void testFindWinningMoveReturnsCorrectPosition() {
        game.board.placeSymbol(0, 0, 'X');
        game.board.placeSymbol(0, 1, 'X');
        game.board.placeSymbol(0, 2, 'X');
        game.board.placeSymbol(0, 3, 'X');
        int[] winPos = game.findWinningMove('X');
        assertNotNull(winPos);
        assertEquals(0, winPos[0]);
        assertEquals(4, winPos[1]);
    }

    @Test
    void testMainExitImmediately() throws IOException {
        String simulatedInput = "TesztJatekos\nnem\nexit\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Main.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Kilépés és mentés"));
    }

    @Test
    void testStartPrintsBoard() {
        game.start(); // most már nem lesz NullPointerException
    }
    @Test
    void testIsValidCoordinates() {
        Game game = new Game(5, 5, "Teszt");
        assertTrue(game.isValid(0, 0));   // érvényes
        assertFalse(game.isValid(-1, 0)); // negatív sor
        assertFalse(game.isValid(5, 5));  // túl nagy index
}
    @Test
    void testCheckDirectionDetectsFiveInARowHorizontally() {
        Game game = new Game(5, 5, "Teszt");
        char[][] grid = game.board.getGrid();
        for (int i = 0; i < 5; i++) {
            grid[0][i] = 'X';
        }
        assertTrue(game.checkDirection(0, 2, 'X', 0, 1)); // vízszintes irányban
    }
    @Test
    void testSwitchPlayerChangesCurrentPlayer() {
        Game game = new Game(5, 5, "Teszt");
        Player first = game.currentPlayer;

        // közvetlenül meghívjuk a váltást
        game.switchPlayer();

        assertNotEquals(first, game.currentPlayer);
    }
    @Test
    void testUpdateHighscoreCreatesFile() throws IOException {
        Game game = new Game(5, 5, "Teszt");
        String filename = "tesztscore.json";
        game.updateHighscore("Teszt", filename);
        assertTrue(Files.exists(Paths.get(filename)));
    }
    @Test
    void testFindWinningMoveReturnsBlockingPosition() {
        Game game = new Game(5, 5, "Teszt");
        game.board.placeSymbol(0, 0, 'X');
        game.board.placeSymbol(0, 1, 'X');
        game.board.placeSymbol(0, 2, 'X');
        game.board.placeSymbol(0, 3, 'X');

        int[] blockPos = game.findWinningMove('X');
        assertNotNull(blockPos);
        assertEquals(0, blockPos[0]);
        assertEquals(4, blockPos[1]); // ez a blokkolandó pozíció
    }





}
