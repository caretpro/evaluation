
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.piece.Wizard;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.JesonMorGame;
import assignment.protocol.Player;

public class Main {
    /**
     * Create and initialize a game.
     * Constructs a {@link Configuration} with two players (human first),
     * places the initial set of pieces in their standard positions, then
     * returns a new {@link JesonMorGame} game built from that configuration.
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the initialized JesonMorGame object
     */
    public static Game createGame(int size, int numMovesProtection) {
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
        Player[] players = new Player[] { userPlayer, computerPlayer };

        Configuration configuration =
                new Configuration(size, players, numMovesProtection);

        int bottom = size - 1;
        configuration.addInitialPiece(new Knight(userPlayer),    bottom, 0);
        configuration.addInitialPiece(new Archer(userPlayer),    bottom, 1);
        configuration.addInitialPiece(new Wizard(userPlayer),    bottom, 2);

        int top = 0;
        configuration.addInitialPiece(new Knight(computerPlayer),    top, size - 1);
        configuration.addInitialPiece(new Archer(computerPlayer),    top, size - 2);
        configuration.addInitialPiece(new Wizard(computerPlayer),    top, size - 3);

        return new JesonMorGame(configuration);
    }

    public static void main(String[] args) {
        String helper = "two integer arguments are required specifying "
                      + "size of gameboard and number of moves with capture protection";
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