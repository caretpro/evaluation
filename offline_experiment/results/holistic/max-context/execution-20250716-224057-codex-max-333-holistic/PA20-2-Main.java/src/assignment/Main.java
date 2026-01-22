
package assignment.protocol;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.exception.InvalidConfigurationError;

/**
 * Entry point and helper to initialize a Jeson‑Mor game according to
 * the standard starting rows (K-A-A-K).
 */
public class Main {
    /**
     * Create and initialize a game.
     *
     * Constructs a {@link Configuration} with two players,
     * places each player’s pieces in the classical Jeson‑Mor starting rows
     * (Knight–Archer–Archer–Archer–Knight), and returns the new game.
     * The console (user) player moves first.
     *
     * @param size               size of gameboard (odd ≥3)
     * @param numMovesProtection number of moves with capture protection
     * @return the initialized JesonMor game
     * @throws InvalidConfigurationError if invalid size or setup
     */
    public static Game createGame(int size, int numMovesProtection) {
        // Delegate most checks to Configuration ctor
        if (size < 3 || size % 2 == 0) {
            throw new InvalidConfigurationError("size must be odd and ≥3");
        }

        // Player 1 (console) moves first, Player 2 (computer) moves second
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        Configuration configuration =
            new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);

        int lastRow = size - 1;
        int midCol  = size / 2;

        // Build starting row pattern K-A-A-A-K for both players
        // Bottom row y=0 for userPlayer
        configuration.addInitialPiece(new Knight(userPlayer), midCol - 2, 0);
        configuration.addInitialPiece(new Archer(userPlayer), midCol - 1, 0);
        configuration.addInitialPiece(new Archer(userPlayer), midCol,     0);
        configuration.addInitialPiece(new Archer(userPlayer), midCol + 1, 0);
        configuration.addInitialPiece(new Knight(userPlayer), midCol + 2, 0);

        // Top row y=lastRow for computerPlayer
        configuration.addInitialPiece(new Knight(computerPlayer), midCol - 2, lastRow);
        configuration.addInitialPiece(new Archer(computerPlayer), midCol - 1, lastRow);
        configuration.addInitialPiece(new Archer(computerPlayer), midCol,     lastRow);
        configuration.addInitialPiece(new Archer(computerPlayer), midCol + 1, lastRow);
        configuration.addInitialPiece(new Knight(computerPlayer), midCol + 2, lastRow);

        return new JesonMor(configuration);
    }

    /**
     * Main entry: args[0]=board size, args[1]=numMovesProtection
     */
    public static void main(String[] args) {
        var usage = "Usage: java assignment.protocol.Main <odd-size≥3> <numMovesProtection>";
        if (args.length < 2) {
            throw new IllegalArgumentException(usage);
        }
        int size, prot;
        try {
            size = Integer.parseInt(args[0]);
            prot = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Both arguments must be integers", e);
        }
        createGame(size, prot).start();
    }
}