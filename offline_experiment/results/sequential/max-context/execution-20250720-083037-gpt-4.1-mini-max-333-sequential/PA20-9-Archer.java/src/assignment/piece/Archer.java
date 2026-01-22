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
		int size = game.getConfiguration().getSize();
		int srcX = source.x();
		int srcY = source.y();
		int[][] directions = { { 0, 1 }, { 0, -1 }, { -1, 0 }, { 1, 0 } };
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			boolean screenFound = false;
			int x = srcX + dx;
			int y = srcY + dy;
			while (x >= 0 && x < size && y >= 0 && y < size) {
				Place dest = new Place(x, y);
				Piece destPiece = game.getPiece(dest);
				if (!screenFound) {
					if (destPiece == null) {
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
					if (destPiece == null) {
						x += dx;
						y += dy;
					} else {
						if (!destPiece.getPlayer().equals(this.getPlayer())) {
							Move move = new Move(source, dest);
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
