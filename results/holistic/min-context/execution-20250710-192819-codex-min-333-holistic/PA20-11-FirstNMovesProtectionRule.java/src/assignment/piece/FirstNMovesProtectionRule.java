
package assignment.protocol;

/**
 * Represents the state of a game and its history.
 */
public interface Game {
    /**
     * @return the total number of moves made so far in this game.
     */
    int getMoveCount();
}