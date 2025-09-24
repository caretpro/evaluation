package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * The rule that requires capturing piece is not allowed within the first certain number of moves.
 */
public class FirstNMovesProtectionRule implements Rule {
	/**
	 * The number of moves within which capturing piece is not allowed.
	 */
	private final int numProtectedMoves;

	public FirstNMovesProtectionRule(int numProtectedMoves) {
		this.numProtectedMoves = numProtectedMoves;
	}

	@Override
	public String getDescription() {
		return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
	}

	/**
	 * Returns all moves made so far (in order). 
	 */
	List<Move> getMoves();
}
