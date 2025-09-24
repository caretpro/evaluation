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
		int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		int srcRow = source.row();
		int srcCol = source.column();
		for (int[] dir : dirs) {
			boolean screenFound = false;
			int r = srcRow + dir[0];
			int c = srcCol + dir[1];
			while (game.getConfiguration().isValidPlace(new Place(r, c))) {
				Place p = new Place(r, c);
				if (!screenFound) {
					if (game.getPiece(p) != null) {
						screenFound = true;
					}
				} else {
					var occ = game.getPiece(p);
					if (occ != null && occ.getPlayer() != getPlayer()) {
						Move m = new Move(source, p);
						if (validateMove(game, m)) {
							moves.add(m);
						}
						break;
					}
				}
				r += dir[0];
				c += dir[1];
			}
		}
		return moves.toArray(Move[]::new);
	}
}
