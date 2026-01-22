
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

/**
 * Entry point and factory for creating a JesonMor game.
 */
public class Main {
    /**
     * Create and initialize a game.
     *
     * <ol>
     *   <li>Creates two players (user moves first).</li>
     *   <li>Builds a Configuration for an N×N board.</li>
     *   <li>Places both Knights and both Archers on each player's back row
     *       in the classic Jeson‑Mor pattern.</li>
     *   <li>Returns a JesonMor game built from that configuration.</li>
     * </ol>
     *
     * @param size               size of gameboard (must be odd and ≥ 3)
     * @param numMovesProtection number of moves with capture protection
     * @return the initialized JesonMor game
     */
    public static Game createGame(int size, int numMovesProtection) {
        // 1) create players (user first)
        ConsolePlayer userPlayer    = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        // 2) build the configuration
        Configuration config = new Configuration(
            size,
            new Player[]{ userPlayer, computerPlayer },
            numMovesProtection
        );

        // 3) home rows
        int yUser = 0;
        int yComp = size - 1;

        // 4) classic placement:
        //    knights on files b and (size−1−b); archers on files c and (size−1−c)
        int fileB       = 1;
        int fileC       = 2;
        int mirrorB     = size - 1 - fileB;
        int mirrorC     = size - 1 - fileC;

        // user back row
        config.addInitialPiece(new Knight(userPlayer), fileB,    yUser);
        config.addInitialPiece(new Archer(userPlayer), fileC,    yUser);
        config.addInitialPiece(new Archer(userPlayer), mirrorC,  yUser);
        config.addInitialPiece(new Knight(userPlayer), mirrorB,  yUser);

        // computer back row
        config.addInitialPiece(new Knight(computerPlayer), fileB,   yComp);
        config.addInitialPiece(new Archer(computerPlayer), fileC,   yComp);
        config.addInitialPiece(new Archer(computerPlayer), mirrorC, yComp);
        config.addInitialPiece(new Knight(computerPlayer), mirrorB, yComp);

        // 5) return the configured game
        return new JesonMor(config);
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