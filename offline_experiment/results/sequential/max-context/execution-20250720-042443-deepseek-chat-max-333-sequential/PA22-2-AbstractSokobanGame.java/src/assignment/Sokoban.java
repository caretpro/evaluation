package assignment;

import assignment.game.SokobanGame;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * The holder of the entry point of the game.
 */
public class Sokoban {

    /**
     * The entry point of the program.
     *
     * @param args The command line args.
     */
    public static void main(@NotNull String[] args) {
        if (args.length < 1) {
            System.err.println("Map is not provided.");
            return;
        }
        final String mapFile = args[0];
        try {
            final SokobanGame game = SokobanGameFactory.createTUIGame(mapFile);
            game.run();
        } catch (IOException e) {
            System.err.println("Failed to load game map: " + e);
            return;
        }
    }
}
