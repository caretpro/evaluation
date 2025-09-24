
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

public class Main {
    public static Game createGame(int size, int numMovesProtection) {
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
        
        Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer },
                numMovesProtection);
        
        // Add user player's pieces - only 1 Archer as per test requirements
        configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
        configuration.addInitialPiece(new Archer(userPlayer), 0, 1);
        
        // Add computer player's pieces - only 1 Archer as per test requirements
        configuration.addInitialPiece(new Knight(computerPlayer), size - 1, size - 1);
        configuration.addInitialPiece(new Archer(computerPlayer), size - 1, size - 2);
        
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