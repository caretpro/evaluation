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
		int x = source.x();
		int y = source.y();
		int[][] directions = { { 0, 1 }, { 0, -1 }, { -1, 0 }, { 1, 0 } };
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			boolean screenFound = false;
			int cx = x + dx;
			int cy = y + dy;
			while (cx >= 0 && cx < size && cy >= 0 && cy < size) {
				Place currentPlace = new Place(cx, cy);
				Piece pieceAtCurrent = game.getPiece(currentPlace);
				if (!screenFound) {
					if (pieceAtCurrent == null) {
						Move move = new Move(source, currentPlace);
						if (validateMove(game, move)) {
							moves.add(move);
						}
					} else {
						screenFound = true;
					}
				} else {
					if (pieceAtCurrent == null) {
						cx += dx;
						cy += dy;
						continue;
					} else {
						if (!pieceAtCurrent.getPlayer().equals(this.getPlayer())) {
							Move move = new Move(source, currentPlace);
							if (validateMove(game, move)) {
								moves.add(move);
							}
						}
						break;
					}
				}
				cx += dx;
				cy += dy;
			}
		}
		return moves.toArray(new Move[0]);
	}
}
