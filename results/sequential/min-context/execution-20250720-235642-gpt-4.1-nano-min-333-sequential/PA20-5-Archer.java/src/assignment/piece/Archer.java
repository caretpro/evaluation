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
		int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
		int maxRows = game.getConfiguration().getBoardRows();
		int maxCols = game.getConfiguration().getBoardCols();
		for (int[] dir : directions) {
			boolean screenFound = false;
			int row = source.getRow() + dir[0];
			int col = source.getCol() + dir[1];
			while (row >= 0 && row < maxRows && col >= 0 && col < maxCols) {
				Place targetPlace = new Place(row, col);
				Piece targetPiece = game.getPieceAt(targetPlace);
				if (!screenFound) {
					if (targetPiece == null) {
						Move move = new Move(source, targetPlace);
						if (validateMove(game, move)) {
							moves.add(move);
						}
					} else {
						screenFound = true;
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
				row += dir[0];
				col += dir[1];
			}
		}
		return moves.toArray(new Move[0]);
	}
}
