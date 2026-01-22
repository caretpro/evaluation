
package assignment.player;

import assignment.protocol.Game;
import assignment.protocol.Color;
import assignment.protocol.Move;
import assignment.protocol.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * A computer player that makes a move randomly.
 */
public class RandomPlayer extends Player {
    private final Random random;
    
    public RandomPlayer(String name, Color color) {
        super(name, color);
        this.random = new Random(System.currentTimeMillis());
    }

    public RandomPlayer(String name) {
        this(name, Color.BLUE);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        if (availableMoves == null || availableMoves.length == 0) {
            throw new IllegalArgumentException("No available moves");
        }
        return availableMoves[random.nextInt(availableMoves.length)];
    }
}