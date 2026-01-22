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
		var board = game.getBoard();
		var directions = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		var moves = new ArrayList<Move>();
		int sr = source.getRow();
		int sc = source.getCol();
		for (var dir : directions) {
			int dr = dir[0], dc = dir[1];
			int r = sr + dr, c = sc + dc;
			while (board.isValid(r, c) && board.getPiece(r, c) == null) {
				r += dr;
				c += dc;
			}
			if (board.isValid(r, c) && board.getPiece(r, c) != null) {
				r += dr;
				c += dc;
				while (board.isValid(r, c)) {
					if (board.getPiece(r, c) != null) {
						Place dest = new Place(r, c);
						Move m = new Move(source, dest);
						if (validateMove(game, m)) {
							moves.add(m);
						}
						break;
					}
					r += dr;
					c += dc;
				}
			}
		}
		return moves.toArray(new Move[0]);
	}
}
