
package assignment.mock;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Player;

public class MockPlayer extends Player {
    public MockPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            // No moves available, handle accordingly
            throw new IllegalStateException("No available moves");
        }
        // Always select the first move to avoid index errors
        return availableMoves[0];
    }
}