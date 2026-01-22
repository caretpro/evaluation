package assignment.game;

/**
 * Denotes a position on a game map.
 * The original point is at top-left corner.
 * The coordinates start from 0;
 *
 * @param x The horizontal displacement.
 * @param y The vertical displacement.
 */
public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    /**
     * Creates a new position.
     *
     * @param x The horizontal displacement.
     * @param y The vertical displacement.
     * @return The newly created position.
     */
    public static Position of(int x, int y) {
        return new Position(x, y);
    }
}
