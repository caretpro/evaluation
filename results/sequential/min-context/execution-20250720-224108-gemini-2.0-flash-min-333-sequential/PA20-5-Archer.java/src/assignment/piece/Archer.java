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

	/**
	 * Returns an array of moves that are valid given the current place of the piece. Given the  {@link Game}  object and the  {@link Place}  that current knight piece locates, this method should return ALL VALID  {@link Move} s according to the current  {@link Place}  of this knight piece. All the returned  {@link Move}  should have source equal to the source parameter. <p> Hint: you should consider corner cases when the  {@link Move}  is not valid on the gameboard. Several tests are provided and your implementation should pass them. <p> <strong>Attention: Student should make sure all  {@link Move} s returned are valid.</strong>
	 * @param game    the game object
	 * @param source  the current place of the piece
	 * @return  an array of available moves
	 */
	@Override
	public Move[] getAvailableMoves(Game game, Place source) {
		ArrayList<Move> moves = new ArrayList<>();
		int boardSize = game.getConfiguration().getDimension();
		int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
		for (int[] dir : directions) {
			int x = source.getX() + dir[0];
			int y = source.getY() + dir[1];
			boolean jumped = false;
			while (x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
				Place destination = new Place(x, y);
				Move move = new Move(source, destination);
				if (!jumped) {
					if (game.getPiece(destination) == null) {
						moves.add(move);
					} else {
						jumped = true;
					}
				} else {
					if (game.getPiece(destination) != null) {
						moves.add(move);
						break;
					}
				}
				x += dir[0];
				y += dir[1];
			}
		}
		return moves.toArray(new Move[0]);
	}
}
