
package assignment.actions;

import assignment.utils.ShouldNotReachException;

/**
 * Represents a move action by a player, with a delta x/y.
 */
public abstract class Move implements Action {
    private final int dx;
    private final int dy;

    protected Move(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * @return the horizontal delta for the move.
     */
    public int getDx() {
        return dx;
    }

    /**
     * @return the vertical delta for the move.
     */
    public int getDy() {
        return dy;
    }

    /**
     * Factory method to create a Move from a command character.
     *
     * @param cmd the command character (e.g. W/A/S/D or H/J/K/L)
     * @return the corresponding Move.
     * @throws ShouldNotReachException if the command is not a valid movement key.
     */
    public static Move fromCommand(char cmd) {
        switch (cmd) {
            case 'W': case 'w': return new Move(-1,  0) {};
            case 'S': case 's': return new Move( 1,  0) {};
            case 'A': case 'a': return new Move( 0, -1) {};
            case 'D': case 'd': return new Move( 0,  1) {};
            case 'H': case 'h': return new Move( 0, -1) {};
            case 'L': case 'l': return new Move( 0,  1) {};
            case 'J': case 'j': return new Move( 1,  0) {};
            case 'K': case 'k': return new Move(-1,  0) {};
            default:
                // Uses the no-arg constructor of ShouldNotReachException
                // which has a default error message.
                throw new ShouldNotReachException();
        }
    }
}