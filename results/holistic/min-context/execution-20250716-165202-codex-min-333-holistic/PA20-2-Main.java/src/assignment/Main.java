
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

/**
 * Entry point for the JesonMor assignment.  Builds and starts a game
 * based on command‑line arguments: board size and capture‑protection moves.
 */
public class Main {
    /**
     * Create and initialize a JesonMor game.
     * <p>
     * Constructs a Configuration with two players (user first),
     * places exactly one Archer in each of the four “half‐board” slots,
     * and Knights at the four corners, then returns a JesonMor Game ready to start.
     *
     * @param size               size of the square gameboard (N×N)
     * @param numMovesProtection number of moves during which captures are disabled
     * @return a fully configured JesonMor game
     */
    public static Game createGame(int size, int numMovesProtection) {
        // Create the two players: user goes first, computer second
        ConsolePlayer userPlayer    = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        // Build the configuration: board size, players array, capture protection
        Configuration config = new Configuration(
            size,
            new Player[]{userPlayer, computerPlayer},
            numMovesProtection
        );

        // Compute half‑board column for placing archers:
        int half = size / 2;

        // Place exactly one Archer in each of the four “half‑board” spots:
        config.addInitialPiece(new Archer(userPlayer),     0,        half);
        config.addInitialPiece(new Archer(computerPlayer), size - 1, half);
        config.addInitialPiece(new Archer(userPlayer),     half,     0);
        config.addInitialPiece(new Archer(computerPlayer), half,     size - 1);

        // Place the Knights in the four corners:
        config.addInitialPiece(new Knight(userPlayer),     0,        0);
        config.addInitialPiece(new Knight(userPlayer),     0,        size - 1);
        config.addInitialPiece(new Knight(computerPlayer), size - 1, 0);
        config.addInitialPiece(new Knight(computerPlayer), size - 1, size - 1);

        return new JesonMor(config);
    }

    public static void main(String[] args) {
        var usage = "two integer arguments required: <boardSize> <movesWithoutCapture>";
        if (args.length < 2) {
            throw new IllegalArgumentException(usage);
        }
        int size, protection;
        try {
            size       = Integer.parseInt(args[0]);
            protection = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("both arguments must be integers", ex);
        }
        createGame(size, protection).start();
    }
}