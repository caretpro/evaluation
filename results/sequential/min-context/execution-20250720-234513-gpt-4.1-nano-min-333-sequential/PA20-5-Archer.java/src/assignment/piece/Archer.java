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
		int row = source.getRow();
		int col = source.getCol();
		int maxRow = game.getBoard().getNumRows();
		int maxCol = game.getBoard().getNumCols();
		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		for (int[] dir : directions) {
			int r = row + dir[0];
			int c = col + dir[1];
			boolean screenFound = false;
			while (r >= 0 && r < maxRow && c >= 0 && c < maxCol) {
				Place targetPlace = new Place(r, c);
				Piece targetPiece = game.getBoard().getPieceAt(targetPlace);
				if (!screenFound) {
					if (targetPiece != null) {
						screenFound = true;
					} else {
						Move move = new Move(source, targetPlace);
						if (validateMove(game, move)) {
							moves.add(move);
						}
					}
				} else {
					if (targetPiece != null) {
						if (targetPiece.getPlayer() != this.getPlayer()) {
							Move move = new Move(source, targetPlace);
							if (validateMove(game, move)) {
								moves.add(move);
							}
						}
						break;
					}
				}
				r += dir[0];
				c += dir[1];
			}
		}
		return moves.toArray(new Move[0]);
	}
}
