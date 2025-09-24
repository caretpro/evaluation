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
		int boardRows = config.rows;
		int boardCols = config.columns;
		int srcRow = source.row;
		int srcCol = source.column;
		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		for (int[] dir : directions) {
			int dRow = dir[0];
			int dCol = dir[1];
			int row = srcRow + dRow;
			int col = srcCol + dCol;
			while (row >= 0 && row < boardRows && col >= 0 && col < boardCols) {
				Place dest = new Place(row, col);
				if (game.getPiece(dest) == null) {
					Move move = new Move(source, dest);
					if (validateMove(game, move)) {
						moves.add(move);
					}
					row += dRow;
					col += dCol;
				} else {
					break;
				}
			}
			row += dRow;
			col += dCol;
			while (row >= 0 && row < boardRows && col >= 0 && col < boardCols) {
				Place dest = new Place(row, col);
				var pieceAtDest = game.getPiece(dest);
				if (pieceAtDest == null) {
					row += dRow;
					col += dCol;
					continue;
				} else {
					Move move = new Move(source, dest);
					if (validateMove(game, move)) {
						moves.add(move);
					}
					break;
				}
			}
		}
		return moves.toArray(new Move[0]);
	}
}
