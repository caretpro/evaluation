package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.player.ConsolePlayer;
import assignment.player.RandomPlayer;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Player;

public class Main {
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

	/**
	 * Create and initialize a game. Student should first construct a  {@link Configuration}  object with two players. Then add initial pieces to gameboard in the  {@link Configuration}  object. After that, use this  {@link Configuration}  object to construct a  {@link JesonMor}  game object. This method should return the created  {@link JesonMor}  object. The initialized gameboard should comply to the requirements of the assignment. The user player should be put first in the player array in the configuration and user player should moves first.
	 * @param size                size of gameboard
	 * @param numMovesProtection  number of moves with capture protection
	 * @return  the game object
	 */
	public static Game createGame(int size, int numMovesProtection) {
		ConsolePlayer user = new ConsolePlayer("UserPlayer", Color.GREEN);
		RandomPlayer comp = new RandomPlayer("ComputerPlayer", Color.BLUE);
		Configuration cfg = new Configuration(size, new Player[] { user, comp }, numMovesProtection);
		int mid = size / 2;
		cfg.addInitialPiece(new Knight(user), new Place(mid, 0));
		cfg.addInitialPiece(new Archer(user), new Place(mid - 1, 0));
		cfg.addInitialPiece(new Archer(user), new Place(mid + 1, 0));
		cfg.addInitialPiece(new Knight(comp), new Place(mid, size - 1));
		cfg.addInitialPiece(new Archer(comp), new Place(mid - 1, size - 1));
		cfg.addInitialPiece(new Archer(comp), new Place(mid + 1, size - 1));
		return new JesonMor(cfg);
	}
}
