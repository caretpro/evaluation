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
		int boardSize = game.getConfiguration().getBoardSize();
		int row = source.row();
		int col = source.col();
		for (int i = row - 1; i >= 0; i--) {
			Place destination = new Place(i, col);
			Move move = new Move(source, destination);
			if (validateMove(game, move)) {
				moves.add(move);
			}
		}
		for (int i = row + 1; i < boardSize; i++) {
			Place destination = new Place(i, col);
			Move move = new Move(source, destination);
			if (validateMove(game, move)) {
				moves.add(move);
			}
		}
		for (int j = col - 1; j >= 0; j--) {
			Place destination = new Place(row, j);
			Move move = new Move(source, destination);
			if (validateMove(game, move)) {
				moves.add(move);
			}
		}
		for (int j = col + 1; j < boardSize; j++) {
			Place destination = new Place(row, j);
			Move move = new Move(source, destination);
			if (validateMove(game, move)) {
				moves.add(move);
			}
		}
		return moves.toArray(new Move[0]);
	}
}
