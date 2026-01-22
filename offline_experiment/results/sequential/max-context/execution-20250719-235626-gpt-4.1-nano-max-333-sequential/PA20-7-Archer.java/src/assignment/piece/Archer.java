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
			boolean screenEncountered = false;
			int step = 1;
			while (true) {
				int newX = x + dx * step;
				int newY = y + dy * step;
				if (newX < 0 || newX >= size || newY < 0 || newY >= size) {
					break;
				}
				Place targetPlace = new Place(newX, newY);
				Piece targetPiece = game.getPiece(targetPlace);
				if (targetPiece == null) {
					if (!screenEncountered) {
						Move move = new Move(source, targetPlace);
						if (validateMove(game, move)) {
							moves.add(move);
						}
					} else {
					}
					step++;
				} else {
					if (!screenEncountered) {
						screenEncountered = true;
						step++;
					} else {
						if (targetPiece.getPlayer().getColor() != this.getPlayer().getColor()) {
							Move move = new Move(source, targetPlace);
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
