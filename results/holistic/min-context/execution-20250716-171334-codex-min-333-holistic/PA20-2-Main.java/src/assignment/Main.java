
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

/**
 * Entry point for the JesonMor assignment.
 */
public class Main {
    /**
     * Create and initialize a JesonMor game.
     * <p>
     * Constructs a Configuration with the two players (user first, then computer),
     * places the user’s Knight in the top-left corner and user’s Archer in the top middle,
     * and places the computer’s Knight in the bottom-right corner and computer’s Archer
     * in the bottom middle of the board.
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the initialized JesonMor game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        // create user (console) and computer (random) players
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        // build configuration: user goes first
        Configuration configuration =
                new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);

        // compute middle column
        int midCol = size / 2;

        // place user's pieces: Knight at (0,0), Archer at (0, midCol)
        configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
        configuration.addInitialPiece(new Archer(userPlayer), 0, midCol);

        // place computer's pieces: Knight at (size-1, size-1), Archer at (size-1, midCol)
        configuration.addInitialPiece(new Knight(computerPlayer), size - 1, size - 1);
        configuration.addInitialPiece(new Archer(computerPlayer), size - 1, midCol);

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