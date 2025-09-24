
package assignment.protocol;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;

/**
 * Entry‐point helper for creating a JesonMor game with the standard initial setup.
 */
public class Main {

    /**
     * Create and initialize a JesonMor game.
     *
     * <p>You must construct a Configuration with two players (user first), place
     * the initial pieces for each side on the board, then use that Configuration
     * to build the JesonMor game object.</p>
     *
     * @param size               size of the square gameboard (must be even)
     * @param numMovesProtection number of moves with capture protection
     * @return a fully initialized JesonMor game ready to start()
     */
    public static Game createGame(int size, int numMovesProtection) {
        // 1. build the two players (user first)
        ConsolePlayer userPlayer    = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        // 2. create the configuration: userPlayer moves first
        Configuration configuration = new Configuration(
            size,
            new Player[] { userPlayer, computerPlayer },
            numMovesProtection
        );

        // 3. place initial pieces:
        //    - bottom row (row 0): user side
        //    - top row (row size-1): computer side
        //    Each row is split half Archers (left half) and half Knights (right half).
        int half = size / 2;
        for (int col = 0; col < size; col++) {
            if (col < half) {
                configuration.addInitialPiece(new Archer(userPlayer), col, 0);
                configuration.addInitialPiece(new Archer(computerPlayer), col, size - 1);
            } else {
                configuration.addInitialPiece(new Knight(userPlayer), col, 0);
                configuration.addInitialPiece(new Knight(computerPlayer), col, size - 1);
            }
        }

        // 4. create and return the JesonMor game
        return new JesonMor(configuration);
    }

    public static void main(String[] args) {
        String helper = "two integer arguments are required specifying size of gameboard "
                      + "and number of moves with capturing protection";
        if (args.length < 2) {
            throw new IllegalArgumentException(helper);
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