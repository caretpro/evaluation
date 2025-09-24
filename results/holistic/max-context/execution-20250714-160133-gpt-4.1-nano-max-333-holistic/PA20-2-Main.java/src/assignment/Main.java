
package assignment;

import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

/**
 * Main class to initialize and start the JesonMor game.
 */
public class Main {
    /**
     * Create and initialize a game.
     * Student should first construct a {@link Configuration} object with two players.
     * Then add initial pieces to gameboard in the {@link Configuration} object.
     * After that, use this {@link Configuration} object to construct a {@link JesonMor} game object.
     * This method should return the created {@link JesonMor} object.
     * <p>
     * The initialized gameboard should comply to the requirements of the assignment.
     * The user player should be put
     * first in the player array in the configuration and user player should move first.
     *
     * <strong>Attention: The code in this method is only an example of using {@link Configuration} to initialize
     * gameboard, Students should remove them and implement on their own.</strong>
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        // Instantiate players: user and computer
        ConsolePlayer userPlayer = new ConsolePlayer("User");
        RandomPlayer computerPlayer = new RandomPlayer("Computer");
        // Create configuration with players and protection moves
        Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);
        // Add initial pieces for each player
        // Place user's knight at (0,0)
        Knight userKnight = new Knight(userPlayer);
        configuration.addInitialPiece(userKnight, 0, 0);
        // Place computer's knight at (size - 1, size - 1)
        Knight computerKnight = new Knight(computerPlayer);
        configuration.addInitialPiece(computerKnight, size - 1, size - 1);
        // Return a new JesonMor game with this configuration
        return new JesonMor(configuration);
    }

    public static void main(String[] args) {
        var helper = "two integer arguments are required specifying size of gameboard and number of moves with capturing protection ";
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