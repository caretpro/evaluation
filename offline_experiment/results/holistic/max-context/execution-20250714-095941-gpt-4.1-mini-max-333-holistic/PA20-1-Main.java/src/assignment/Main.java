
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
		// Create players: user player first, then computer player
		ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
		RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

		// Create configuration with size, players array and protection moves
		Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

		// Place initial pieces for userPlayer (Green)
		// Knights at bottom-left corner (0, size-1) and (1, size-1)
		configuration.addInitialPiece(new Knight(userPlayer), 0, size - 1);
		configuration.addInitialPiece(new Knight(userPlayer), 1, size - 1);
		// Archers at (0, size-2) and (1, size-2)
		configuration.addInitialPiece(new Archer(userPlayer), 0, size - 2);
		configuration.addInitialPiece(new Archer(userPlayer), 1, size - 2);

		// Place initial pieces for computerPlayer (Blue)
		// Knights at top-right corner (size-1, 0) and (size-2, 0)
		configuration.addInitialPiece(new Knight(computerPlayer), size - 1, 0);
		configuration.addInitialPiece(new Knight(computerPlayer), size - 2, 0);
		// Archers at (size-1, 1) and (size-2, 1)
		configuration.addInitialPiece(new Archer(computerPlayer), size - 1, 1);
		configuration.addInitialPiece(new Archer(computerPlayer), size - 2, 1);

		// Return new JesonMor game with this configuration
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