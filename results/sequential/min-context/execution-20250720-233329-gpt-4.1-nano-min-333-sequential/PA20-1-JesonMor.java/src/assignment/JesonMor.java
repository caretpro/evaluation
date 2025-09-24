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
		int currentIndex = 0;
		while (true) {
			Player currentPlayer = players[currentIndex];
			this.currentPlayer = currentPlayer;
			Move[] moves = getAvailableMoves(currentPlayer);
			if (moves.length == 0) {
			}
			Move move = currentPlayer.nextMove(this, moves);
			movePiece(move);
			Piece movedPiece = board[move.source().x()][move.source().y()];
			updateScore(currentPlayer, movedPiece, move);
			this.refreshOutput();
			Player potentialWinner = getWinner(currentPlayer, movedPiece, move);
			if (potentialWinner != null) {
				winner = potentialWinner;
				break;
			}
			currentIndex = (currentIndex + 1) % players.length;
			this.numMoves++;
		}
		System.out.println();
		System.out.println("Congratulations! ");
		System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
		return winner;
	}

	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		final int WIN_CONDITION = 3;
		int x = lastMove.destination().x();
		int y = lastMove.destination().y();
		int[][] directions = { { 1, 0 }, { 0, 1 }, { 1, 1 }, { 1, -1 } };
		for (int[] dir : directions) {
			int count = 1;
			int dx = dir[0], dy = dir[1];
			int nx = x + dx, ny = y + dy;
			while (isValidPosition(nx, ny) && isPlayerPieceAt(lastPlayer, nx, ny)) {
				count++;
				nx += dx;
				ny += dy;
			}
			dx = -dir[0];
			dy = -dir[1];
			nx = x + dx;
			ny = y + dy;
			while (isValidPosition(nx, ny) && isPlayerPieceAt(lastPlayer, nx, ny)) {
				count++;
				nx += dx;
				ny += dy;
			}
			if (count >= WIN_CONDITION) {
				return lastPlayer;
			}
		}
		return null;
	}

	public void updateScore(Player player, Piece piece, Move move) {
		Place source = move.source();
		Place destination = move.destination();
		int distance = Math.abs(source.x() - destination.x()) + Math.abs(source.y() - destination.y());
		int currentScore = player.getScore();
		player.setScore(currentScore + distance);
	}

	public void movePiece(Move move) {
		Place source = move.source();
		Place destination = move.destination();
		int srcX = source.x();
		int srcY = source.y();
		int destX = destination.x();
		int destY = destination.y();
		Piece movingPiece = board[srcX][srcY];
		board[destX][destY] = movingPiece;
		board[srcX][srcY] = null;
	}

	public Move[] getAvailableMoves(Player player) {
		ArrayList<Move> validMoves = new ArrayList<>();
		Piece[][] board = this.board;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				Piece piece = board[x][y];
				if (piece != null && piece.getOwner().equals(player)) {
					Place currentPlace = new Place(x, y);
					Move[] potentialMoves = piece.getAvailableMoves(this, currentPlace);
					for (Move move : potentialMoves) {
						if (isValidMove(move, player)) {
							validMoves.add(move);
						}
					}
				}
			}
		}
		return validMoves.toArray(new Move[0]);
	}
}
