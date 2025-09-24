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
		int size = game.getConfiguration().getSize();
		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			boolean canMove = true;
			for (int step = 1; step < size; step++) {
				int newX = source.x() + dx * step;
				int newY = source.y() + dy * step;
				if (newX < 0 || newX >= size || newY < 0 || newY >= size) {
					break;
				}
				Place dest = new Place(newX, newY);
				Move move = new Move(source, dest);
				if (game.getPiece(dest) == null) {
					if (canMove && validateMove(game, move)) {
						validMoves.add(move);
					}
				} else {
					canMove = false;
					for (int jump = step + 1; jump < size; jump++) {
						int capX = source.x() + dx * jump;
						int capY = source.y() + dy * jump;
						if (capX < 0 || capX >= size || capY < 0 || capY >= size) {
							break;
						}
						Place capDest = new Place(capX, capY);
						Move capMove = new Move(source, capDest);
						if (game.getPiece(capDest) != null && game.getPiece(capDest).getPlayer() != this.getPlayer()
								&& validateMove(game, capMove)) {
							validMoves.add(capMove);
							break;
						} else if (game.getPiece(capDest) != null) {
							break;
						}
					}
					break;
				}
			}
		}
		return validMoves.toArray(new Move[0]);
	}
}
