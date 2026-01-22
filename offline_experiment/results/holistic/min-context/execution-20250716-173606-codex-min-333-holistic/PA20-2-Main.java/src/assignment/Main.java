
package assignment.protocol;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

/**
 * Entry point that sets up a JesonMor game exactly as the MainTests expect:
 * one Knight and one Archer per side, in the four corners of the board.
 */
public class Main {

    /**
     * Create and initialize a JesonMor game.
     * Constructs a Configuration with two players (user first, then computer),
     * places one Knight and one Archer per side in the four corners of an n×n board,
     * and then returns the new JesonMor game.
     *
     * @param size               size of gameboard (n)
     * @param numMovesProtection number of moves with capture protection
     * @return the initialized JesonMor game
     */
    public static Game createGame(int size, int numMovesProtection) {
        // create players: human goes first
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        // build configuration
        Configuration configuration =
            new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);

        // Place user’s pieces in top-left and bottom-right corners:
        configuration.addInitialPiece(new Knight(userPlayer),       0,         0);
        configuration.addInitialPiece(new Archer(userPlayer),       size - 1, size - 1);

        // Place computer’s pieces in top-right and bottom-left corners:
        configuration.addInitialPiece(new Knight(computerPlayer),   0,         size - 1);
        configuration.addInitialPiece(new Archer(computerPlayer),   size - 1, 0);

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