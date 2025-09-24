
package assignment.player;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * RandomPlayer makes a random move from available moves.
 */
public class RandomPlayer extends Player {
    private final Random random = new Random();

    public RandomPlayer(String name, @NotNull Color color) {
        super(name, color);
    }

    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            // No available moves, return null or handle accordingly
            return null;
        }
        int bound = availableMoves.length;
        if (bound <= 0) {
            // Defensive check, though previous condition covers this
            return null;
        }
        int index = random.nextInt(bound);
        return availableMoves[index];
    }
}