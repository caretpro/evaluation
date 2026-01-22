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
		int[] dr = { 1, -1, 0, 0 };
		int[] dc = { 0, 0, 1, -1 };
		int rows = game.getConfiguration().getRowsCount();
		int cols = game.getConfiguration().getColsCount();
		for (int d = 0; d < dr.length; d++) {
			int r = source.row();
			int c = source.col();
			while (true) {
				r += dr[d];
				c += dc[d];
				if (r < 0 || r >= rows || c < 0 || c >= cols) {
					break;
				}
				var dest = new Place(r, c);
				var m = new Move(source, dest);
				if (!validateMove(game, m)) {
					break;
				}
				moves.add(m);
			}
			int sr = source.row(), sc = source.col();
			boolean sawScreen = false;
			while (true) {
				sr += dr[d];
				sc += dc[d];
				if (sr < 0 || sr >= rows || sc < 0 || sc >= cols) {
					break;
				}
				var intermediate = new Place(sr, sc);
				if (!sawScreen) {
					if (game.pieceAt(intermediate) != null) {
						sawScreen = true;
					}
				} else {
					if (game.pieceAt(intermediate) != null) {
						var captureMove = new Move(source, intermediate);
						if (validateMove(game, captureMove)) {
							moves.add(captureMove);
						}
						break;
					}
				}
			}
		}
		return moves.toArray(Move[]::new);
	}
}
