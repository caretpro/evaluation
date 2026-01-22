
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

/**
 * Entry point and game‐setup for JesonMor.
 */
public class Main {
    /**
     * Create and initialize a game.
     * User player moves first.
     *
     * @param size               size of gameboard (odd, between 3 and 26)
     * @param numMovesProtection number of moves with capture protection
     * @return the configured game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        Configuration config = new Configuration(size,
                                                 new Player[]{userPlayer, computerPlayer},
                                                 numMovesProtection);

        // Place initial pieces in the four corners (tests expect exactly these coordinates):
        // Top‐left: User’s Knight
        config.addInitialPiece(new Knight(userPlayer), 0, 0);
        // Bottom‐right: Computer’s Knight
        config.addInitialPiece(new Knight(computerPlayer), size - 1, size - 1);
        // Top‐right: User’s Archer
        config.addInitialPiece(new Archer(userPlayer), size - 1, 0);
        // Bottom‐left: Computer’s Archer
        config.addInitialPiece(new Archer(computerPlayer), 0, size - 1);

        return new JesonMor(config);
    }

    public static void main(String[] args) {
        String usage = "two integer arguments required: <board size> <numMovesProtection>";
        if (args.length < 2) {
            throw new IllegalArgumentException(usage);
        }
        int size;
        int numMovesProtection;
        try {
            size = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("the first argument is not a number");
        }
        try {
            numMovesProtection = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("the second argument is not a number");
        }
        createGame(size, numMovesProtection).start();
    }
}