
package assignment;

import assignment.piece.Archer;
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
        // Instantiate the user player and computer player
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
        // Create configuration with players and protection moves
        Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);
        // Add initial Knight pieces for each player at specified positions
        Knight knight1 = new Knight(userPlayer);
        Knight knight2 = new Knight(computerPlayer);
        configuration.addInitialPiece(knight1, 0, 0);
        configuration.addInitialPiece(knight2, 1, 0);
        // Add initial Archer pieces for each player at specified positions
        Archer archer1 = new Archer(userPlayer);
        configuration.addInitialPiece(archer1, 0, 1);
        Archer archer2 = new Archer(computerPlayer);
        configuration.addInitialPiece(archer2, 1, 1);
        // Return a new JesonMor game instance with this configuration
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