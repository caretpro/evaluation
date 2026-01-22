
package assignment.protocol;

/**
 * Represents a rectangular game board.
 */
public interface Game {
    /**
     * @return the number of rows (height) of the board
     */
    int getHeight();

    /**
     * @return the number of columns (width) of the board
     */
    int getWidth();
}