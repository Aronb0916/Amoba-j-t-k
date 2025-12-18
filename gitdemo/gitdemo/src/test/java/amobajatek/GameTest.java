package amobajatek;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

class GameTest {

    @Test
    void testValidMove() throws IOException {
        Game game = new Game(10, 10, "Aron");
        boolean result = game.lepes(0, 0);
        assertFalse(result);
    }

    @Test
    void testInvalidMove() throws IOException {
        Game game = new Game(10, 10, "Aron");
        boolean result = game.lepes(-1, -1);
        assertFalse(result);
    }
        @Test
        void testOccupiedCell() throws IOException {
            Game game = new Game(10, 10, "Aron");
            assertFalse(game.lepes(2, 2));
            assertFalse(game.lepes(2, 2));
        }
    @Test
    void testSaveAndLoadGame() throws IOException {
        Game game = new Game(5, 5, "Teszt");
        game.lepes(0, 0);
        game.saveGameAsJson("tesztmentes.json");

        Game ujGame = new Game(5, 5, "Teszt");
        ujGame.loadGameFromJson("tesztmentes.json");

        assertEquals('X', ujGame.board.getGrid()[0][0]);
    }
    @Test
    void testFindWinningMoveReturnsCorrectPosition() {
        Game game = new Game(5, 5, "Teszt");
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
        // szimulált bemenet (név, nem, exit)
        String simulatedInput = "TesztJatekos\nnem\nexit\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        //futtatjuk a main metódust
        Main.main(new String[]{});

        //ellenőrizzük, hogy a kimenet tartalmazza-e a kilépés üzenetet
        String output = outContent.toString();
        assertTrue(output.contains("Kilépés és mentés"));
    }

}

