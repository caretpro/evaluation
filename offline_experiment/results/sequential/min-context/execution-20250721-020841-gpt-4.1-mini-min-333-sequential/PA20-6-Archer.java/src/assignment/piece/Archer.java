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
		var moves = new ArrayList<Move>();
		var config = game.getConfiguration();
		int rows = config.rows();
		int cols = config.cols();
		int srcRow = source.row();
		int srcCol = source.col();
		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		for (int[] dir : directions) {
			int dRow = dir[0];
			int dCol = dir[1];
			int r = srcRow + dRow;
			int c = srcCol + dCol;
			boolean screenFound = false;
			while (r >= 0 && r < rows && c >= 0 && c < cols) {
				Place currentPlace = new Place(r, c);
				if (game.getPiece(currentPlace) != null) {
					screenFound = true;
					break;
				}
				r += dRow;
				c += dCol;
			}
			if (!screenFound) {
				continue;
			}
			r += dRow;
			c += dCol;
			while (r >= 0 && r < rows && c >= 0 && c < cols) {
				Place targetPlace = new Place(r, c);
				Move move = new Move(source, targetPlace);
				if (validateMove(game, move)) {
					moves.add(move);
				}
				if (game.getPiece(targetPlace) != null) {
					break;
				}
				r += dRow;
				c += dCol;
			}
		}
		return moves.toArray(Move[]::new);
	}
}
