
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

/**
 * Entry point for Jeson Mor.
 */
public class Main {
    /**
     * Create and initialize a game.
     * Constructs a {@link Configuration} with two players,
     * places initial pieces on the board, then wraps it in a {@link JesonMor} game instance.
     *
     * The initialized gameboard must comply with the assignment requirements.
     * The user player goes first.
     *
     * @param size               size of gameboard (odd number ≥ 3, ≤ 26)
     * @param numMovesProtection number of moves with capture protection (≥ 0)
     * @return the initialized JesonMor game
     */
    public static Game createGame(int size, int numMovesProtection) {
        // 1. Create the two players (user first so they move first).
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
        Player[] players = new Player[] { userPlayer, computerPlayer };

        // 2. Build the configuration: board size, player order, and protection.
        Configuration configuration = new Configuration(size, players, numMovesProtection);

        // 3. Place initial pieces:
        //    - One Knight per side
        Knight userKnight     = new Knight(userPlayer);
        Knight computerKnight = new Knight(computerPlayer);
        configuration.addInitialPiece(userKnight, 0, 0);
        configuration.addInitialPiece(computerKnight, size - 1, size - 1);

        //    - One Archer per side
        Archer userArcher     = new Archer(userPlayer);
        Archer computerArcher = new Archer(computerPlayer);
        configuration.addInitialPiece(userArcher, 1, 0);
        configuration.addInitialPiece(computerArcher, size - 2, size - 1);

        // 4. Return the JesonMor game.
        return new JesonMor(configuration);
    }

    public static void main(String[] args) {
        String helper = "two integer arguments are required: <boardSize> <numMovesProtection>";
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