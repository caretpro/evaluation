
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

        // Initialize the game board with pieces according to the assignment requirements.
        // Example for size 8:
        if (size == 8) {
            // User's pieces (Knights)
            configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
            configuration.addInitialPiece(new Knight(userPlayer), 0, 7);

            // Computer's pieces (Archers)
            configuration.addInitialPiece(new Archer(computerPlayer), 7, 0);
            configuration.addInitialPiece(new Archer(computerPlayer), 7, 7);
        } else if (size == 6) {
            // User's pieces (Knights)
            configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
            configuration.addInitialPiece(new Knight(userPlayer), 0, 5);

            // Computer's pieces (Archers)
            configuration.addInitialPiece(new Archer(computerPlayer), 5, 0);
            configuration.addInitialPiece(new Archer(computerPlayer), 5, 5);
        }
        // You can add more initializations for different board sizes if needed.
        else {
            configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
            configuration.addInitialPiece(new Archer(computerPlayer), size - 1, size - 1);
        }

        // Ensure that all positions on the board are initialized, even if they are empty.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (configuration.getPieceAt(i, j) == null) {
                    configuration.setPieceAt(null, i, j); // Explicitly set empty positions to null
                }
            }
        }

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