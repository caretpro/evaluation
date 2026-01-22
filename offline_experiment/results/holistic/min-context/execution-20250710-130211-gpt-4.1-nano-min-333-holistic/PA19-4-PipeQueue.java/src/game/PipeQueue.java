
package game.pipes;

/**
 * Class representing a Pipe in the game.
 */
public class Pipe {
    private final char type;

    /**
     * Private constructor to initialize the pipe with a specific type.
     *
     * @param type Character representing the pipe type ('L', 'T', 'I', etc.)
     */
    private Pipe(char type) {
        this.type = type;
    }

    /**
     * Creates a Pipe of type 'L'.
     *
     * @return A new Pipe instance of type 'L'.
     */
    public static Pipe createL() {
        return new Pipe('L');
    }

    /**
     * Creates a Pipe of type 'T'.
     *
     * @return A new Pipe instance of type 'T'.
     */
    public static Pipe createT() {
        return new Pipe('T');
    }

    /**
     * Creates a Pipe of type 'I'.
     *
     * @return A new Pipe instance of type 'I'.
     */
    public static Pipe createI() {
        return new Pipe('I');
    }

    /**
     * Returns a single-character representation of the pipe.
     *
     * @return Character representing the pipe type.
     */
    public char toSingleChar() {
        return type;
    }
}