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
		var boardSize = game.getConfiguration().getBoardSize();
		int boardWidth = boardSize.width;
		int boardHeight = boardSize.height;
		int[][] directions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
		int startCol = source.getColumn();
		int startRow = source.getRow();
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			boolean screenFound = false;
			int x = startCol;
			int y = startRow;
			while (true) {
				x += dx;
				y += dy;
				if (x < 0 || x >= boardWidth || y < 0 || y >= boardHeight) {
					break;
				}
				Place target = Place.of(x, y);
				Piece targetPiece = game.getPiece(target);
				if (!screenFound) {
					if (targetPiece != null) {
						screenFound = true;
					}
					continue;
				} else {
					if (targetPiece == null) {
						Move move = new Move(source, target);
						if (validateMove(game, move)) {
							moves.add(move);
						}
					} else {
						if (!targetPiece.getPlayer().equals(this.getPlayer())) {
							Move move = new Move(source, target);
							if (validateMove(game, move)) {
								moves.add(move);
							}
						}
						break;
					}
				}
			}
		}
		return moves.toArray(Move[]::new);
	}
}
