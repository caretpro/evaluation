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
			int cx = x + dx;
			int cy = y + dy;
			while (cx >= 0 && cx < size && cy >= 0 && cy < size) {
				Place dest = new Place(cx, cy);
				Piece pieceAtDest = game.getPiece(dest);
				if (pieceAtDest == null) {
					Move move = new Move(source, dest);
					if (validateMove(game, move)) {
						moves.add(move);
					}
					cx += dx;
					cy += dy;
				} else {
					cx += dx;
					cy += dy;
					while (cx >= 0 && cx < size && cy >= 0 && cy < size) {
						Place captureDest = new Place(cx, cy);
						Piece pieceAtCapture = game.getPiece(captureDest);
						if (pieceAtCapture != null) {
							if (!pieceAtCapture.getPlayer().equals(this.getPlayer())) {
								Move captureMove = new Move(source, captureDest);
								if (validateMove(game, captureMove)) {
									moves.add(captureMove);
								}
							}
							break;
						}
						cx += dx;
						cy += dy;
					}
					break;
				}
			}
		}
		return moves.toArray(Move[]::new);
	}
}
