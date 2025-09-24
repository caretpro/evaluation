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
    public RandomPlayer(String name, Color color) {
        super(name, color);
    }

    public RandomPlayer(String name) {
        this(name, Color.BLUE);
    }

    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        int index = new Random().nextInt(availableMoves.length);
        return availableMoves[index];
    }
}
