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

	/**
	 * Update the score of a player according to the {@link Piece} and corresponding move made by him just now.
	 * This method will be called every time after a player makes a move, in order to update the corresponding score
	 * of this player.
	 * <p>
	 * The score of a player is the cumulative score of each move he makes.
	 * The score of each move is calculated with the Manhattan distance between the source and destination {@link Place}.
	 * <p>
	 * Student can use {@link Player#getScore()} to get the current score of a player before updating.
	 * {@link Player#setScore(int)} can be used to update the score of a player.
	 * <p>
	 * <strong>Attention: do not need to validate move in this method.</strong>
	 *
	 * @param player the player who just makes a move
	 * @param piece  the piece that is just moved
	 * @param move   the move that is just made
	 */
	public void updateScore(Player player, Piece piece, Move move) {
		// TODO student implementation
	}

	@Override
	public Player start() {
		Player winner = null;
		this.numMoves = 0;
		this.board = configuration.getInitialBoard();
		this.currentPlayer = null;
		this.refreshOutput();
		while (winner == null) {
			for (Player player : configuration.getPlayers()) {
				this.currentPlayer = player;
				Move[] moves = getAvailableMoves(player);
				Move chosen = player.nextMove(this, moves);
				movePiece(chosen);
				numMoves++;
				updateScore(player, board[chosen.getSource().x()][chosen.getSource().y()], chosen);
				refreshOutput();
				Piece lastPiece = board[chosen.getDestination().x()][chosen.getDestination().y()];
				winner = getWinner(player, lastPiece, chosen);
				if (winner != null) {
					break;
				}
			}
		}
		System.out.println();
		System.out.println("Congratulations! ");
		System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
		return winner;
	}

	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		Place[] exits = configuration.getExit();
		Player[] players = configuration.getPlayers();
		int idx = Arrays.asList(players).indexOf(lastPlayer);
		Place opponentExit = exits[1 - idx];
		if (lastMove.getDestination().equals(opponentExit)) {
			return lastPlayer;
		}
		return null;
	}

	/**
	 * Make a move. This method performs moving a  {@link Piece}  from source to destination  {@link Place}  according  {@link Move}  object. Note that after the move, there will be no  {@link Piece}  in source  {@link Place} . <p> Positions of all  {@link Piece} s on the gameboard are stored in  {@link JesonMor#board}  field as a 2-dimension array of {@link Piece}  objects. The x and y coordinate of a  {@link Place}  on the gameboard are used as index in  {@link JesonMor#board} . E.g.  {@code  board[place.x()][place.y()]} . If one  {@link Place}  does not have a piece on it, it will be null in  {@code  board[place.x()][place.y()]} . Student may modify elements in  {@link JesonMor#board}  to implement moving a  {@link Piece} . The  {@link Move}  object can be considered valid on present gameboard.
	 * @param move  the move to make
	 */
	@Override
	public void movePiece(Move move) {
		var from = move.getSource();
		var to = move.getDestination();
		var p = board[from.x()][from.y()];
		board[from.x()][from.y()] = null;
		board[to.x()][to.y()] = p;
	}

	@Override
	public Move[] getAvailableMoves(Player player) {
		var moves = new ArrayList<Move>();
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				Piece p = board[x][y];
				if (p != null && p.getPlayer().equals(player)) {
					Place src = Place.of(x, y);
					for (Move m : p.getAvailableMoves(this, src)) {
						if (Game.isValidMove(this, m)) {
							moves.add(m);
						}
					}
				}
			}
		}
		return moves.toArray(Move[]::new);
	}
}
