
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

public class Main {
    /**
     * Create and initialize a game.
     * Student should first construct a {@link Configuration} object with two players.
     * Then add initial pieces to gameboard in the {@link Configuration} object.
     * After that, use this {@link Configuration} object to construct a {@link JesonMor} game object
     * This method should return the created {@link JesonMor} object.
     * <p>
     * The initialized gameboard should comply to the requirements of the assignment.
     * The user player should be put
     * first in the player array in the configuration and user player should moves first.
     *
     * <strong>Attention: The code in this method is only an example of using {@link Configuration} to initialize
     * gameboard, Students should remove them and implement on their own.</strong>
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
        Configuration configuration = new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);

        // Initialize the board with archers in the corners for both players
        Archer userArcher1 = new Archer(userPlayer);
        Archer userArcher2 = new Archer(userPlayer);
        Archer computerArcher1 = new Archer(computerPlayer);
        Archer computerArcher2 = new Archer(computerPlayer);

        configuration.addInitialPiece(userArcher1, 0, 0);
        configuration.addInitialPiece(userArcher2, size - 1, size - 1);
        configuration.addInitialPiece(computerArcher1, size - 1, 0); // Corrected position
        configuration.addInitialPiece(computerArcher2, 0, size - 1); // Corrected position

        // Add knights for both players
        Knight userKnight = new Knight(userPlayer);
        Knight computerKnight = new Knight(computerPlayer);

        configuration.addInitialPiece(userKnight, 1, 1);
        configuration.addInitialPiece(computerKnight, size - 2, size - 2);

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