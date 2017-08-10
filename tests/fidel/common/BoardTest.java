package fidel.common;

import org.testng.annotations.Test;

import static fidel.common.TileType.*;
import static org.testng.Assert.*;

@Test
public class BoardTest {
    @Test
    void test() {
        Board board = new Board(
                new TileType[][]{
                        {ENTRANCE, SPIDER},
                        {MEDIKIT, EXIT}
                }
        );
        assertEquals(board.get(0, 0), ENTRANCE);
        assertEquals(board.get(0, 1), SPIDER);
        assertEquals(board.get(1, 0), MEDIKIT);
        assertEquals(board.get(1, 1), EXIT);
    }

    @Test
    void test2() {
        Board board = new Board(
                new TileType[][]{
                        {ENTRANCE, RED_SPIDER, SMALL_SPIDER, SMALL_SPIDER, SMALL_SPIDER, EXIT},
                }
        );
        for (int j = 0; j < board.width; j++) {
            System.out.println(board.get(0, j));
        }
    }
}