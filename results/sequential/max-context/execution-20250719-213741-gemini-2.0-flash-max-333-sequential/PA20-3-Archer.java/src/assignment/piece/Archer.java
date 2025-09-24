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
		List<Move> availableMoves = new ArrayList<>();
		int boardSize = game.getConfiguration().getSize();
		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		for (int[] direction : directions) {
			int x = source.x();
			int y = source.y();
			while (true) {
				x += direction[0];
				y += direction[1];
				if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
					break;
				}
				Place destination = new Place(x, y);
				Move move = new Move(source, destination);
				if (validateMove(game, move)) {
					availableMoves.add(move);
				} else {
					break;
				}
			}
		}
		return availableMoves.toArray(new Move[0]);
	}
}
