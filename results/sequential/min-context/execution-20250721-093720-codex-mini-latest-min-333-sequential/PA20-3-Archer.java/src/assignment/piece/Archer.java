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
		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		for (int[] dir : directions) {
			int dx = dir[0], dy = dir[1];
			int x = source.x() + dx, y = source.y() + dy;
			while (Place.inBounds(x, y) && game.getPieceAt(Place.of(x, y)) == null) {
				var mv = new Move(source, Place.of(x, y));
				if (validateMove(game, mv)) {
					moves.add(mv);
				}
				x += dx;
				y += dy;
			}
			if (Place.inBounds(x, y)) {
				x += dx;
				y += dy;
				while (Place.inBounds(x, y)) {
					var dest = Place.of(x, y);
					var occupant = game.getPieceAt(dest);
					if (occupant != null) {
						var mv = new Move(source, dest);
						if (validateMove(game, mv)) {
							moves.add(mv);
						}
						break;
					}
					x += dx;
					y += dy;
				}
			}
		}
		return moves.toArray(Move[]::new);
	}
}
