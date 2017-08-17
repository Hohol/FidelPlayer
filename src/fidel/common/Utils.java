package fidel.common;

import static java.lang.Math.abs;

public class Utils {
    public static int dist(Cell a, Cell b) {
        return abs(a.row - b.row) + abs(a.col - b.col);
    }
}
