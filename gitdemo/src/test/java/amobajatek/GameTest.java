package amobajatek;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void testValidMove() {
        Game game = new Game(10, 10, "Aron");
        boolean result = game.lepes(0, 0);
        assertFalse(result);
    }

    @Test
    void testInvalidMove() {
        Game game = new Game(10, 10, "Aron");
        boolean result = game.lepes(-1, -1);
        assertFalse(result);
    }
        @Test
        void testOccupiedCell() {
            Game game = new Game(10, 10, "Aron");
            assertFalse(game.lepes(2, 2));
            assertFalse(game.lepes(2, 2));
        }
    }

