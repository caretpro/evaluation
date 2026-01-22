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
		int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			boolean screenEncountered = false;
			int x = source.x() + dx;
			int y = source.y() + dy;
			while (x >= 0 && x < size && y >= 0 && y < size) {
				Piece targetPiece = game.getPiece(x, y);
				if (targetPiece == null) {
					Move move = new Move(source, x, y);
					if (validateMove(game, move)) {
						moves.add(move);
					}
					x += dx;
					y += dy;
				} else {
					if (!screenEncountered) {
						screenEncountered = true;
						x += dx;
						y += dy;
					} else {
						Piece targetPiece2 = game.getPiece(x, y);
						if (targetPiece2 != null
								&& targetPiece2.getPlayer().getColor() != this.getPlayer().getColor()) {
							Move move = new Move(source, x, y);
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
