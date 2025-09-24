
package assignment.protocol;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Game;
import assignment.protocol.Configuration;
import assignment.protocol.Player;

/**
 * Entry point for JesonMor: sets up the standard initial layout.
 */
public class Main {

    /**
     * Create and initialize a JesonMor game.
     *
     * @param size               the (odd) board size ≥3 ≤26
     * @param numMovesProtection number of moves with capture protection ≥0
     * @return the initialized JesonMor game
     */
    public static Game createGame(int size, int numMovesProtection) {
        var user = new ConsolePlayer("UserPlayer");
        var computer = new RandomPlayer("ComputerPlayer");
        var config = new Configuration(size, new Player[]{user, computer}, numMovesProtection);

        int mid = size / 2;  // central index

        // User’s side: Archer at (mid-2, mid), Knight at (mid-1, mid)
        config.addInitialPiece(new Archer(user), mid - 2, mid);
        config.addInitialPiece(new Knight(user), mid - 1, mid);

        // Computer’s side: Knight at (mid+1, mid), Archer at (mid+2, mid)
        config.addInitialPiece(new Knight(computer), mid + 1, mid);
        config.addInitialPiece(new Archer(computer), mid + 2, mid);

        return new JesonMor(config);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("two integer arguments required: <boardSize> <numMovesProtection>");
        }
        int size;
        int protection;
        try {
            size = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("the first argument is not a number");
        }
        try {
            protection = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("the second argument is not a number");
        }
        createGame(size, protection).start();
    }
}