
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
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        // Create players with colors: user green, computer blue
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        // Create configuration with players in order: user first
        Configuration configuration = new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);

        var central = configuration.getCentralPlace();

        int lastRow = size - 1;

        // User player pieces (bottom side)
        // Knights at bottom-left corner and bottom-right corner
        int[][] userKnightPositions = {
                {0, lastRow},
                {lastRow, lastRow}
        };
        for (var pos : userKnightPositions) {
            if (!(pos[0] == central.x() && pos[1] == central.y())) {
                configuration.addInitialPiece(new Knight(userPlayer), pos[0], pos[1]);
            }
        }

        // One archer for user player at bottom center (middle column, second last row)
        int userArcherX = size / 2;
        int userArcherY = lastRow - 1;
        if (!(userArcherX == central.x() && userArcherY == central.y())) {
            configuration.addInitialPiece(new Archer(userPlayer), userArcherX, userArcherY);
        }

        // Computer player pieces (top side)
        // Knights at top-left corner and top-right corner
        int[][] computerKnightPositions = {
                {0, 0},
                {lastRow, 0}
        };
        for (var pos : computerKnightPositions) {
            if (!(pos[0] == central.x() && pos[1] == central.y())) {
                configuration.addInitialPiece(new Knight(computerPlayer), pos[0], pos[1]);
            }
        }

        // One archer for computer player at top center (middle column, row 1)
        int computerArcherX = size / 2;
        int computerArcherY = 1;
        if (!(computerArcherX == central.x() && computerArcherY == central.y())) {
            configuration.addInitialPiece(new Archer(computerPlayer), computerArcherX, computerArcherY);
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