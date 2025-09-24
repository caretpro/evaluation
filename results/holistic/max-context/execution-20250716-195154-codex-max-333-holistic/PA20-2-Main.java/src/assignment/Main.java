
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

/**
 * Entry point for Jeson‐Mor.
 * Constructs the two players, sets up the board with Archers and Knights,
 * and starts the game.
 */
public class Main {
    /**
     * Create and initialize a game.
     * First constructs a Configuration with two players (user first, then computer),
     * populates the initial board per assignment requirements, then returns a JesonMor game.
     *
     * @param size               size of gameboard (odd ≥3 and ≤26)
     * @param numMovesProtection number of moves with capture protection
     * @return the initialized JesonMor game
     */
    public static Game createGame(int size, int numMovesProtection) {
        // Create players: user moves first (GREEN), computer second (BLUE)
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        // Build configuration: board size, players, and capture‐protection moves
        Configuration config = new Configuration(size,
                new Player[] { userPlayer, computerPlayer },
                numMovesProtection);

        // Determine center coordinate
        int c = config.getCentralPlace().x();

        // Place initial pieces in a symmetric layout around the central square:
        // On each side (bottom for userPlayer, top for computerPlayer),
        // positions at (c±2, c±2): Knights, and (c±1, c±2): Archers.
        // Bottom side (userPlayer)
        config.addInitialPiece(new Knight(userPlayer), c - 2, c + 2);
        config.addInitialPiece(new Archer(userPlayer), c - 1, c + 2);
        config.addInitialPiece(new Archer(userPlayer), c + 1, c + 2);
        config.addInitialPiece(new Knight(userPlayer), c + 2, c + 2);
        // Top side (computerPlayer)
        config.addInitialPiece(new Knight(computerPlayer), c - 2, c - 2);
        config.addInitialPiece(new Archer(computerPlayer), c - 1, c - 2);
        config.addInitialPiece(new Archer(computerPlayer), c + 1, c - 2);
        config.addInitialPiece(new Knight(computerPlayer), c + 2, c - 2);

        return new JesonMor(config);
    }

    public static void main(String[] args) {
        var helper = "two integer arguments are required specifying size of gameboard and number of moves with capturing protection";
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