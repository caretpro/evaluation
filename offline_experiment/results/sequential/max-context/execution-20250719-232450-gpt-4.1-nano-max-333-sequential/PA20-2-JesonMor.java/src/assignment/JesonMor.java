
package assignment.mock;

import assignment.protocol.*;

public class MockPlayer extends Player {
    private Move[] movesToMake;
    private int moveIndex = 0;

    public MockPlayer(String name, Color color, Move[] moves) {
        super(name, color);
        this.movesToMake = moves != null ? moves : new Move[0];
    }

    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves.length == 0) {
            // No moves available, return a dummy move or handle accordingly
            // Here, returning the first available move if exists, else null (which should be handled by caller)
            return new Move(new Place(0,0), new Place(0,0));
        }
        // Return the next move in the predefined list if available
        if (moveIndex < movesToMake.length) {
            return movesToMake[moveIndex++];
        }
        // If no predefined moves left, fallback to first available move
        return availableMoves[0];
    }
}