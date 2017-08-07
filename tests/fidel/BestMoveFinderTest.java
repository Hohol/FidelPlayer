package fidel;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static fidel.Command.*;
import static fidel.TileType.*;
import static org.testng.Assert.*;

@Test
public class BestMoveFinderTest {

    BestMoveFinder bestMoveFinder = new BestMoveFinder();

    @Test
    void test() {
        GameState gameState = new GameState(
                new TileType[][]{{ENTRANCE, EMPTY, EMPTY, EXIT}}
        );
        List<Command> bestMoves = bestMoveFinder.findBestMoves(gameState);
        assertEquals(bestMoves, Arrays.asList(RIGHT, RIGHT, RIGHT, ENTER));
    }
}