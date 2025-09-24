
package game.pipes;

import java.util.Random;

/**
 * Represents a pipe with varying heights.
 */
public class Pipe {

    // Predefined pipe heights (example).
    private static final int[] POSSIBLE_HEIGHTS = {50, 100, 150, 200, 250};

    private final int height;

    public Pipe(int height) {
        this.height = height;
    }

    public int toSingleChar() {
        // ...
    }

    // Factory to create a random Pipe based on POSSIBLE_HEIGHTS.
    public static Pipe randomPipe(Random rnd) {
        int idx = rnd.nextInt(POSSIBLE_HEIGHTS.length);
        return new Pipe(POSSIBLE_HEIGHTS[idx]);
    }
}