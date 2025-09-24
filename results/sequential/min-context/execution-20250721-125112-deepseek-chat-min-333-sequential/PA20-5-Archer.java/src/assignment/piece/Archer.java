package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;

/**
 * Archer piece that moves similar to cannon in chinese chess.
 * Rules of move of Archer can be found in wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class Archer extends Piece {
	public Archer(Player player) {
		super(player);
		//        throw new UnsupportedOperationException();
	}

	@Override
	public char getLabel() {
		return 'A';
	}

	private boolean validateMove(Game game, Move move) {
		var rules = new Rule[] { new OutOfBoundaryRule(), new OccupiedRule(), new VacantRule(), new NilMoveRule(),
				new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), new ArcherMoveRule(), };
		for (var rule : rules) {
			if (!rule.validate(game, move)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Move[] getAvailableMoves(Game game, Place source) {
		ArrayList<Move> validMoves = new ArrayList<>();
		int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			boolean foundPiece = false;
			for (int step = 1;; step++) {
				int newRow = source.row() + step * dx;
				int newCol = source.col() + step * dy;
				Place dest = new Place(newRow, newCol);
				if (!game.board().isValidPlace(dest)) {
					break;
				}
				Move move = new Move(source, dest);
				if (!foundPiece) {
					if (game.board().getPiece(dest) == null) {
						if (validateMove(game, move)) {
							validMoves.add(move);
						}
					} else {
						foundPiece = true;
					}
				} else {
					if (game.board().getPiece(dest) != null) {
						if (game.board().getPiece(dest).player() != this.player() && validateMove(game, move)) {
							validMoves.add(move);
						}
						break;
					}
				}
			}
		}
		return validMoves.toArray(new Move[0]);
	}
}
