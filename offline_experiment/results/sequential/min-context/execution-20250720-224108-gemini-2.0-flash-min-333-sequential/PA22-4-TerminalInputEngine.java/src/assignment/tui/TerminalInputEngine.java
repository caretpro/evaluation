
package assignment.actions;

/**
 * A concrete implementation of the Move action.
 */
public class ConcreteMove extends Move {

    private final char moveCharacter;

    /**
     * Constructs a ConcreteMove with the specified move character and a default ID of 0.
     *
     * @param moveCharacter The character representing the move (W, A, S, D, H, J, K, L).
     */
    public ConcreteMove(char moveCharacter) {
        super(0); // Assuming '0' is a default or valid ID for the Move action
        this.moveCharacter = moveCharacter;
    }

    /**
     * Gets the move character.
     *
     * @return The move character.
     */
    public char getMoveCharacter() {
        return moveCharacter;
    }

    @Override
    public char getDirection() {
        return moveCharacter;
    }
}