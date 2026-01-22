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
		int boardWidth = game.getConfiguration().getBoardWidth();
		int boardHeight = game.getConfiguration().getBoardHeight();
		int srcCol = source.getColumn();
		int srcRow = source.getRow();
		int[][] directions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
		for (int[] dir : directions) {
			int dCol = dir[0];
			int dRow = dir[1];
			int col = srcCol + dCol;
			int row = srcRow + dRow;
			boolean screenFound = false;
			while (col >= 0 && col < boardWidth && row >= 0 && row < boardHeight) {
				Place target = new Place(col, row);
				Piece targetPiece = game.getPieceAt(target);
				if (!screenFound) {
					if (targetPiece != null) {
						screenFound = true;
					}
				} else {
					if (targetPiece != null) {
						Move move = new Move(source, target);
						if (validateMove(game, move)) {
							moves.add(move);
						}
						break;
					}
				}
				col += dCol;
				row += dRow;
			}
		}
		return moves.toArray(Move[]::new);
	}
}
