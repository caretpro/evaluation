package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class JesonMor extends Game {
	public JesonMor(Configuration configuration) {
		super(configuration);
	}

	@Override
	public Player start() {
		Player winner = null;
		this.numMoves = 0;
		this.board = configuration.getInitialBoard();
		this.currentPlayer = null;
		this.refreshOutput();
		Player[] players = configuration.getPlayers();
		int turn = 0;
		while (true) {
			currentPlayer = players[turn % players.length];
			Move[] moves = getAvailableMoves(currentPlayer);
			Move chosen = currentPlayer.nextMove(this, moves);
			movePiece(chosen);
			updateScore(currentPlayer, board[chosen.to().x()][chosen.to().y()], chosen);
			numMoves++;
			refreshOutput();
			winner = getWinner(currentPlayer, board[chosen.to().x()][chosen.to().y()], chosen);
			if (winner != null) {
				System.out.println();
				System.out.println("Congratulations! ");
				System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
				return winner;
			}
			turn++;
		}
	}

	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		if (!(lastPiece instanceof Knight)) {
			return null;
		}
		Player[] players = configuration.getPlayers();
		Player opponent = players[0].equals(lastPlayer) ? players[1] : players[0];
		Piece[][] initial = configuration.getInitialBoard();
		Place target = null;
		for (int x = 0; x < initial.length; x++) {
			for (int y = 0; y < initial[x].length; y++) {
				Piece p = initial[x][y];
				if (p instanceof Knight && p.getOwner().equals(opponent)) {
					target = new Place(x, y);
					break;
				}
			}
			if (target != null) {
				break;
			}
		}
		if (target == null) {
			return null;
		}
		if (lastMove.to().equals(target)) {
			return lastPlayer;
		}
		return null;
	}

	@Override
	public void updateScore(Player player, Piece piece, Move move) {
		Place src = move.from();
		Place dst = move.to();
		int manhattan = Math.abs(src.x() - dst.x()) + Math.abs(src.y() - dst.y());
		player.setScore(player.getScore() + manhattan);
	}

	/**
	 * Make a move. This method performs moving a  {@link Piece}  from source to destination  {@link Place}  according  {@link Move}  object. Note that after the move, there will be no  {@link Piece}  in source  {@link Place} . <p> Positions of all  {@link Piece} s on the gameboard are stored in  {@link JesonMor#board}  field as a 2-dimension array of {@link Piece}  objects. The x and y coordinate of a  {@link Place}  on the gameboard are used as index in  {@link JesonMor#board} . E.g.  {@code  board[place.x()][place.y()]} . If one  {@link Place}  does not have a piece on it, it will be null in  {@code  board[place.x()][place.y()]} . Student may modify elements in  {@link JesonMor#board}  to implement moving a  {@link Piece} . The  {@link Move}  object can be considered valid on present gameboard.
	 * @param move  the move to make
	 */
	public void movePiece(Move move) {
		Place src = move.from();
		Place dst = move.to();
		Piece moving = board[src.x()][src.y()];
		board[src.x()][src.y()] = null;
		board[dst.x()][dst.y()] = moving;
	}

	@Override
	public Move[] getAvailableMoves(Player player) {
		ArrayList<Move> moves = new ArrayList<>();
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				Piece p = board[x][y];
				if (p != null && p.owner().equals(player)) {
					Place from = new Place(x, y);
					for (Move m : p.getAvailableMoves(this, from)) {
						if (Rules.isValid(this, m)) {
							moves.add(m);
						}
					}
				}
			}
		}
		return moves.toArray(new Move[0]);
	}
}
