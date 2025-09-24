
package assignment.protocol;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;

public class Main {
    /**
     * Create and initialize a game.
     * User goes first and is always at index 0.
     *
     * @param size               size of the square board
     * @param numMovesProtection number of moves with capture protection
     * @return a game ready to start
     */
    public static Game createGame(int size, int numMovesProtection) {
        // 1) players: user moves first
        ConsolePlayer user    = new ConsolePlayer("UserPlayer");
        RandomPlayer opponent = new RandomPlayer("ComputerPlayer");

        // 2) configuration
        Configuration config = new Configuration(size, new Player[]{user, opponent}, numMovesProtection);

        // 3) User side: one archer at each corner of row 0
        Archer uaLeft  = new Archer(user);
        Archer uaRight = new Archer(user);
        config.addInitialPiece(uaLeft,  0,       0);
        config.addInitialPiece(uaRight, size - 1, 0);

        // 4) Knights immediately in front (row 1, same columns)
        Knight ukLeft  = new Knight(user);
        Knight ukRight = new Knight(user);
        config.addInitialPiece(ukLeft,  0, 1);
        config.addInitialPiece(ukRight, size - 1, 1);

        // 5) Opponent side: Knights on its forward row (row size-2)
        Knight oaLeft  = new Knight(opponent);
        Knight oaRight = new Knight(opponent);
        config.addInitialPiece(oaLeft,  0,       size - 2);
        config.addInitialPiece(oaRight, size - 1, size - 2);

        // 6) Opponent’s archers on back row (row size-1), corners
        Archer oarchLeft  = new Archer(opponent);
        Archer oarchRight = new Archer(opponent);
        config.addInitialPiece(oarchLeft,  0,       size - 1);
        config.addInitialPiece(oarchRight, size - 1, size - 1);

        // 7) return the game
        return new JesonMor(config);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                "two integer arguments are required specifying size of gameboard and number of moves with capturing protection"
            );
        }
        int size;
        int protection;
        try {
            size = Integer.parseInt(args[0]);
            protection = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("both arguments must be integers");
        }
        createGame(size, protection).start();
    }
}