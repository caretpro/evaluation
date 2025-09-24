
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

/**
 * Entry point for the JesonMor game.
 */
public class Main {

    /**
     * Create and initialize a game.
     *
     * Students must:
     * 1. Construct a Configuration with two players (user first).
     * 2. Add initial pieces to the gameboard according to the assignment spec.
     * 3. Construct and return a JesonMor game from that Configuration.
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the initialized JesonMor game
     */
    public static Game createGame(int size, int numMovesProtection) {
        ConsolePlayer userPlayer = new ConsolePlayer("User");
        RandomPlayer computerPlayer = new RandomPlayer("Computer");
        Configuration cfg =
            new Configuration(size,
                              new Player[] { userPlayer, computerPlayer },
                              numMovesProtection);

        // Place only corner Knights and a single center Archer in each back‑row:
        int last = size - 1;
        int mid = size / 2;

        // User back‑row (row 0):
        cfg.addInitialPiece(new Knight(userPlayer), 0, 0);
        cfg.addInitialPiece(new Archer(userPlayer), mid, 0);
        cfg.addInitialPiece(new Knight(userPlayer), last, 0);

        // Computer back‑row (row last):
        cfg.addInitialPiece(new Knight(computerPlayer), 0, last);
        cfg.addInitialPiece(new Archer(computerPlayer), mid, last);
        cfg.addInitialPiece(new Knight(computerPlayer), last, last);

        return new JesonMor(cfg);
    }

    public static void main(String[] args) {
        var helper = "two integer arguments are required: size of gameboard and number of moves with capture protection";
        if (args.length < 2) {
            throw new IllegalArgumentException(helper);
        }
        int size, numMovesProtection;
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