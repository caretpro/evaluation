
package assignment.player;

import assignment.protocol.Game;
import assignment.protocol.Color;
import assignment.protocol.Move;
import assignment.protocol.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

/**
 * A computer player that makes a move randomly.
 */
public class RandomPlayer extends Player {
    private final Random random = new Random();

    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    public RandomPlayer(String name) {
        this(name, Color.BLUE);
    }

    /**
     * Choose a move from available moves.
     * This method will be called by {@link Game} object to get the move that the player wants to make when it is the
     * player's turn.
     * <p>
     * {@link RandomPlayer} chooses a move from available ones randomly.
     * <p>
     * <strong>Attention: The {@link Move} returned must always be one of the provided {@code availableMoves}.</strong>
     *
     * @param game           the current game object
     * @param availableMoves available moves for this player to choose from.
     * @return the chosen move
     * @throws IllegalArgumentException if {@code availableMoves} is null or empty
     */
    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            throw new IllegalArgumentException("No available moves for player " + getName());
        }
        int idx = random.nextInt(availableMoves.length);
        Move m = availableMoves[idx];
        // guard against null entries just in case
        if (m == null) {
            throw new IllegalStateException("RandomPlayer encountered a null Move in availableMoves at index " + idx);
        }
        return m;
    }
}