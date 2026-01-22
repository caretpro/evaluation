
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;
import assignment.protocol.Piece;

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

		// Create configuration with size, players, and protection moves
		Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

		// Central place to avoid placing pieces there
		var central = configuration.getCentralPlace();

		// User player's pieces: 2 knights, 1 archer
		Knight userKnight1 = new Knight(userPlayer);
		Knight userKnight2 = new Knight(userPlayer);
		Archer userArcher = new Archer(userPlayer);

		// Computer player's pieces: 2 knights, 1 archer
		Knight compKnight1 = new Knight(computerPlayer);
		Knight compKnight2 = new Knight(computerPlayer);
		Archer compArcher = new Archer(computerPlayer);

		// Place user pieces:
		// Knights at bottom-left and bottom-right corners
		addPieceSafe(configuration, userKnight1, 0, size - 1, central);
		addPieceSafe(configuration, userKnight2, size - 1, size - 1, central);
		// Archer at middle bottom row (center x, one row above bottom)
		addPieceSafe(configuration, userArcher, size / 2, size - 2, central);

		// Place computer pieces:
		// Knights at top-left and top-right corners
		addPieceSafe(configuration, compKnight1, 0, 0, central);
		addPieceSafe(configuration, compKnight2, size - 1, 0, central);
		// Archer at middle top row (center x, one row below top)
		addPieceSafe(configuration, compArcher, size / 2, 1, central);

		return new JesonMor(configuration);
	}

	private static void addPieceSafe(Configuration configuration, Piece piece, int x, int y, assignment.protocol.Place central) {
		if (!(x == central.x() && y == central.y())) {
			configuration.addInitialPiece(piece, x, y);
		}
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