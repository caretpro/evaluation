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
		int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			for (int step = 1; step < size; step++) {
				int newX = source.x() + dx * step;
				int newY = source.y() + dy * step;
				if (newX < 0 || newX >= size || newY < 0 || newY >= size) {
					break;
				}
				Place dest = new Place(newX, newY);
				Move move = new Move(source, dest);
				if (game.getPiece(dest) != null) {
					break;
				}
				if (validateMove(game, move)) {
					validMoves.add(move);
				}
			}
			for (int step = 1; step < size; step++) {
				int newX = source.x() + dx * step;
				int newY = source.y() + dy * step;
				if (newX < 0 || newX >= size || newY < 0 || newY >= size) {
					break;
				}
				Place dest = new Place(newX, newY);
				Move move = new Move(source, dest);
				int pieceCount = 0;
				for (int i = 1; i < step; i++) {
					int betweenX = source.x() + dx * i;
					int betweenY = source.y() + dy * i;
					if (game.getPiece(betweenX, betweenY) != null) {
						pieceCount++;
					}
				}
				if (pieceCount == 1 && game.getPiece(dest) != null
						&& !game.getPiece(dest).getPlayer().equals(this.getPlayer())) {
					if (validateMove(game, move)) {
						validMoves.add(move);
					}
				}
			}
		}
		return validMoves.toArray(new Move[0]);
	}
}
