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
		ArrayList<Move> validMoves = new ArrayList<>();
		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			int x = source.x() + dx;
			int y = source.y() + dy;
			int boardSize = game.getConfiguration().getSize();
			while (x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
				Place dest = new Place(x, y);
				Move move = new Move(source, dest);
				if (game.getPiece(dest) == null) {
					if (validateMove(game, move)) {
						validMoves.add(move);
					}
					x += dx;
					y += dy;
				} else {
					x += dx;
					y += dy;
					if (x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
						dest = new Place(x, y);
						Piece target = game.getPiece(dest);
						if (target != null && target.getPlayer() != this.getPlayer()) {
							move = new Move(source, dest);
							if (validateMove(game, move)) {
								validMoves.add(move);
							}
						}
					}
					break;
				}
			}
		}
		return validMoves.toArray(new Move[0]);
	}
}
