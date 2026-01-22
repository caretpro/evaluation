
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
     * @param size               size of gameboard (odd, ≥3)
     * @param numMovesProtection number of moves with capture protection (≥0)
     * @return the game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        // 1) Create players: user first so they move first
        ConsolePlayer user = new ConsolePlayer("UserPlayer");
        RandomPlayer computer = new RandomPlayer("ComputerPlayer");

        // 2) Build configuration
        Configuration cfg =
            new Configuration(size, new Player[]{user, computer}, numMovesProtection);

        // 3) Compute center coordinate
        int mid = size / 2;

        // 4) For each diagonal direction (dx, dy),
        //    place one Knight at distance 1 and one Archer at distance 2.
        int[][] dirs = { {+1,+1}, {+1,-1}, {-1,+1}, {-1,-1} };
        for (int[] d : dirs) {
            int dx = d[0], dy = d[1];
            // user on the “south” diagonals (dy > 0), computer on the “north” (dy < 0)
            // Knights at distance 1
            cfg.addInitialPiece(new Knight(dy > 0 ? user : computer),
                                mid + dx * 1,
                                mid + dy * 1);
            // Archers at distance 2
            cfg.addInitialPiece(new Archer(dy > 0 ? user : computer),
                                mid + dx * 2,
                                mid + dy * 2);
        }

        // 5) Build and return the JesonMor game
        return new JesonMor(cfg);
    }

    public static void main(String[] args) {
        var msg = "two integer arguments required: <size> <numMovesProtection>";
        if (args.length < 2) {
            throw new IllegalArgumentException(msg);
        }
        int size, numProt;
        try {
            size   = Integer.parseInt(args[0]);
            numProt = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("arguments must be integers", e);
        }
        createGame(size, numProt).start();
    }
}