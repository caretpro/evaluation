
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
     * After that, use this {@link Configuration} object to construct a {@link JesonMor} game object.
     * This method should return the created {@link JesonMor} object.
     *
     * The initialized gameboard should comply to the requirements of the assignment.
     * The user player should be put first in the player array in the configuration
     * and the user player should move first.
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        // 1) Create players: user first so they move first
        ConsolePlayer user = new ConsolePlayer("User");
        RandomPlayer computer = new RandomPlayer("Computer");

        // 2) Build the configuration
        Configuration cfg =
            new Configuration(size,
                              new Player[] {user, computer},
                              numMovesProtection);

        // 3) Place Knights in the corners for both players
        cfg.addInitialPiece(new Knight(user), 0, 0);
        cfg.addInitialPiece(new Knight(user), size - 1, 0);
        cfg.addInitialPiece(new Knight(computer), 0, size - 1);
        cfg.addInitialPiece(new Knight(computer), size - 1, size - 1);

        // 4) Compute how many Archers to place on interior columns:
        int interiorCols = size - 2;
        int numArchersPerSide = interiorCols / 2;  // exactly half of the interior

        // 5) Evenly space that many Archers across the interior columns
        //    by computing the step between placed Archers.
        if (numArchersPerSide > 0) {
            double step = (double) interiorCols / numArchersPerSide;
            for (int i = 0; i < numArchersPerSide; i++) {
                int col = 1 + (int) Math.round(i * step);
                cfg.addInitialPiece(new Archer(user), col, 0);
                cfg.addInitialPiece(new Archer(computer), col, size - 1);
            }
        }

        // 6) Return the ready-to-start JesonMor game
        return new JesonMor(cfg);
    }

    public static void main(String[] args) {
        var helper =
            "two integer arguments are required specifying size of gameboard "
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