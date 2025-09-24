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
		ArrayList<Move> moves = new ArrayList<>();
		int srcRow = source.row;
		int srcCol = source.column;
		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		for (int[] dir : directions) {
			int dRow = dir[0];
			int dCol = dir[1];
			boolean screenFound = false;
			int r = srcRow + dRow;
			int c = srcCol + dCol;
			while (game.getConfiguration().isValid(new Place(r, c))) {
				Place target = new Place(r, c);
				var pieceAtTarget = game.getPiece(r, c);
				if (!screenFound) {
					if (pieceAtTarget == null) {
						Move move = new Move(source, target);
						if (validateMove(game, move)) {
							moves.add(move);
						}
						r += dRow;
						c += dCol;
					} else {
						screenFound = true;
						r += dRow;
						c += dCol;
					}
				} else {
					if (pieceAtTarget != null) {
						Move move = new Move(source, target);
						if (validateMove(game, move)) {
							moves.add(move);
						}
						break;
					} else {
						r += dRow;
						c += dCol;
					}
				}
			}
		}
		return moves.toArray(Move[]::new);
	}
}
