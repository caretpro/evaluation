
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
     * <p>
     * The initialized gameboard should comply to the requirements of the assignment.
     * The user player should be put first in the player array in the configuration
     * and the user player moves first.
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        // 1) Create the two players, user first so they move first
        ConsolePlayer userPlayer     = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer  = new RandomPlayer("ComputerPlayer");

        // 2) Build the configuration
        Configuration cfg =
            new Configuration(size, new Player[]{ userPlayer, computerPlayer }, numMovesProtection);

        // 3) Compute indices
        int mid      = size / 2;
        int lastRow  = size - 1;

        // 4) Place each player’s archers in the 'half' columns and knights at the edges
        //    - Archers: columns mid-1 and mid
        //    - Knights: columns 0 and size-1
        //    UserPlayer on row 0; ComputerPlayer on row size-1

        // User home row (row 0)
        cfg.addInitialPiece(new Archer(userPlayer), mid - 1, 0);
        cfg.addInitialPiece(new Archer(userPlayer), mid,     0);
        cfg.addInitialPiece(new Knight(userPlayer),  0,       0);
        cfg.addInitialPiece(new Knight(userPlayer),  size - 1,0);

        // Computer home row (row size-1)
        cfg.addInitialPiece(new Archer(computerPlayer), mid - 1, lastRow);
        cfg.addInitialPiece(new Archer(computerPlayer), mid,     lastRow);
        cfg.addInitialPiece(new Knight(computerPlayer), 0,       lastRow);
        cfg.addInitialPiece(new Knight(computerPlayer), size - 1,lastRow);

        // 5) Return the initialized JesonMor game
        return new JesonMor(cfg);
    }

    public static void main(String[] args) {
        var helper = "two integer arguments are required specifying size of gameboard "
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