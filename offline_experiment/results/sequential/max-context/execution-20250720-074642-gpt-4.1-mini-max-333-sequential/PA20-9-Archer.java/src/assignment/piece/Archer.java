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
		int size = game.getConfiguration().getSize();
		ArrayList<Move> moves = new ArrayList<>();
		int startX = source.x();
		int startY = source.y();
		int[][] directions = { { 0, 1 }, { 0, -1 }, { -1, 0 }, { 1, 0 } };
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			boolean screenFound = false;
			int x = startX + dx;
			int y = startY + dy;
			while (x >= 0 && x < size && y >= 0 && y < size) {
				Place dest = new Place(x, y);
				Piece pieceAtDest = game.getPiece(dest);
				if (!screenFound) {
					if (pieceAtDest == null) {
						Move move = new Move(source, dest);
						if (validateMove(game, move)) {
							moves.add(move);
						}
						x += dx;
						y += dy;
					} else {
						screenFound = true;
						x += dx;
						y += dy;
					}
				} else {
					if (pieceAtDest != null) {
						if (!pieceAtDest.getPlayer().equals(this.getPlayer())) {
							Move move = new Move(source, dest);
							if (validateMove(game, move)) {
								moves.add(move);
							}
						}
						break;
					} else {
						x += dx;
						y += dy;
					}
				}
			}
		}
		return moves.toArray(new Move[0]);
	}
}
