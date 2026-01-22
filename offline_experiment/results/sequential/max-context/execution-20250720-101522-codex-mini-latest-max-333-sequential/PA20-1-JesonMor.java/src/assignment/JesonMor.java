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
		int playerCount = players.length;
		int turnIndex = 0;
		while (true) {
			currentPlayer = players[turnIndex];
			Move[] availableMoves = getAvailableMoves(currentPlayer);
			Move chosenMove = currentPlayer.nextMove(this, availableMoves);
			Piece movedPiece = getPiece(chosenMove.getSource());
			movePiece(chosenMove);
			numMoves++;
			updateScore(currentPlayer, movedPiece, chosenMove);
			refreshOutput();
			winner = getWinner(currentPlayer, movedPiece, chosenMove);
			if (winner != null) {
				System.out.println();
				System.out.println("Congratulations! ");
				System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
				return winner;
			}
			turnIndex = (turnIndex + 1) % playerCount;
		}
	}

	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		if (lastMove.getDestination().equals(getCentralPlace()) && numMoves >= configuration.getNumMovesProtection()) {
			return lastPlayer;
		}
		Player opponent = configuration.getPlayers()[0].equals(lastPlayer) ? configuration.getPlayers()[1]
				: configuration.getPlayers()[0];
		Move[] oppMoves = getAvailableMoves(opponent);
		if (oppMoves.length == 0) {
			return lastPlayer;
		}
		return null;
	}

	@Override
	public void updateScore(Player player, Piece piece, Move move) {
		Place src = move.getSource();
		Place dst = move.getDestination();
		int manhattanDistance = Math.abs(src.x() - dst.x()) + Math.abs(src.y() - dst.y());
		player.setScore(player.getScore() + manhattanDistance);
	}

	/**
	 * Make a move. This method performs moving a  {@link Piece}  from source to destination  {@link Place}  according  {@link Move}  object. Note that after the move, there will be no  {@link Piece}  in source  {@link Place} . <p> Positions of all  {@link Piece} s on the gameboard are stored in  {@link JesonMor#board}  field as a 2-dimension array of {@link Piece}  objects. The x and y coordinate of a  {@link Place}  on the gameboard are used as index in  {@link JesonMor#board} . E.g.  {@code  board[place.x()][place.y()]} . If one  {@link Place}  does not have a piece on it, it will be null in  {@code  board[place.x()][place.y()]} . Student may modify elements in  {@link JesonMor#board}  to implement moving a  {@link Piece} . The  {@link Move}  object can be considered valid on present gameboard.
	 * @param move  the move to make
	 */
	@Override
	public void movePiece(Move move) {
		Place src = move.getSource();
		Place dst = move.getDestination();
		Piece piece = board[src.x()][src.y()];
		board[dst.x()][dst.y()] = piece;
		board[src.x()][src.y()] = null;
	}

	/**
	 * Decide whether a raw candidate move is actually legal under current board state.
	 */
	private boolean isMoveValid(Move m, boolean protectionActive) {
		Place dst = m.getDestination();
		int size = configuration.getSize();
		if (dst.x() < 0 || dst.x() >= size || dst.y() < 0 || dst.y() >= size) {
			return false;
		}
		Piece occupant = board[dst.x()][dst.y()];
		if (occupant != null && occupant.getPlayer().equals(currentPlayer)) {
			return false;
		}
		if (protectionActive && occupant != null) {
			return false;
		}
		return true;
	}
}
