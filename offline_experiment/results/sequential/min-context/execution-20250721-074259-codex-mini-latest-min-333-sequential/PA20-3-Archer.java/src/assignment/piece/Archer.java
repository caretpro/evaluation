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
		int srcRow = source.row();
		int srcCol = source.col();
		int[][] dirs = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
		for (var d : dirs) {
			int dr = d[0], dc = d[1];
			int r = srcRow + dr, c = srcCol + dc;
			while (true) {
				var p = new Place(r, c);
				if (!Place.isValid(p) || game.getPieceAt(p) != null) {
					break;
				}
				var m = new Move(source, p);
				if (validateMove(game, m)) {
					moves.add(m);
				}
				r += dr;
				c += dc;
			}
			var screenPos = new Place(r, c);
			if (Place.isValid(screenPos) && game.getPieceAt(screenPos) != null) {
				r += dr;
				c += dc;
				var targetPos = new Place(r, c);
				if (Place.isValid(targetPos)) {
					var occupant = game.getPieceAt(targetPos);
					if (occupant != null && occupant.getPlayer() != getPlayer()) {
						var m = new Move(source, targetPos);
						if (validateMove(game, m)) {
							moves.add(m);
						}
					}
				}
			}
		}
		return moves.toArray(Move[]::new);
	}
}
