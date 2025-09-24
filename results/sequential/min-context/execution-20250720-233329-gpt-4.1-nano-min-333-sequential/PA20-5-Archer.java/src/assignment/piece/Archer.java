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
		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		int maxRow = game.getBoard().getNumRows();
		int maxCol = game.getBoard().getNumCols();
		int startRow = source.getRow();
		int startCol = source.getCol();
		for (int[] dir : directions) {
			int row = startRow;
			int col = startCol;
			boolean screenFound = false;
			while (true) {
				row += dir[0];
				col += dir[1];
				if (row < 0 || row >= maxRow || col < 0 || col >= maxCol) {
					break;
				}
				Place targetPlace = new Place(row, col);
				Piece targetPiece = game.getBoard().getPieceAt(targetPlace);
				if (targetPiece == null) {
					Move move = new Move(source, targetPlace);
					if (validateMove(game, move)) {
						moves.add(move);
					}
				} else {
					if (!screenFound) {
						screenFound = true;
					} else {
						if (targetPiece.getPlayer() != this.getPlayer()) {
							Move move = new Move(source, targetPlace);
							if (validateMove(game, move)) {
								moves.add(move);
							}
						}
						break;
					}
				}
			}
		}
		return moves.toArray(new Move[0]);
	}
}
