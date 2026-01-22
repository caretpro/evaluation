
package assignment;

import assignment.protocol.*;

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
			this.currentPlayer = players[currentIndex];
			Move[] availableMoves = getAvailableMoves(currentPlayer);

			// If no available moves, the other player wins (deadlock)
			if (availableMoves.length == 0) {
				winner = players[(currentIndex + 1) % players.length];
				break;
			}

			Move chosenMove = currentPlayer.nextMove(this, availableMoves);

			// Save piece on central place before move
			Place centralPlace = configuration.getCentralPlace();
			Piece centralBefore = board[centralPlace.x()][centralPlace.y()];

			movePiece(chosenMove);

			Piece movedPiece = getPiece(chosenMove.getDestination());

			updateScore(currentPlayer, movedPiece, chosenMove);

			numMoves++;

			winner = getWinner(currentPlayer, movedPiece, chosenMove, centralBefore);

			refreshOutput();

			if (winner != null) {
				System.out.println();
				System.out.println("Congratulations! ");
				System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
				return winner;
			}

			currentIndex = (currentIndex + 1) % players.length;
		}

		System.out.println();
		System.out.println("Congratulations! ");
		System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
		return winner;
	}

	/**
	 * Overloaded getWinner to pass piece on central place before move for detecting leaving central place.
	 */
	private Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove, Piece centralBefore) {
		if (numMoves < configuration.getNumMovesProtection()) {
			return null;
		}

		Player[] players = configuration.getPlayers();
		Player opponent = players[0].equals(lastPlayer) ? players[1] : players[0];

		Place central = configuration.getCentralPlace();
		Piece centralAfter = board[central.x()][central.y()];

		// Detect leaving central place:
		if (centralBefore != null) {
			if (centralAfter == null) {
				// central place now empty, player who left loses, other wins
				if (centralBefore.getPlayer().equals(lastPlayer)) {
					return opponent;
				} else {
					return lastPlayer;
				}
			} else if (!centralAfter.getPlayer().equals(centralBefore.getPlayer())) {
				// central place occupied by different player, player who left loses
				if (centralBefore.getPlayer().equals(lastPlayer)) {
					return opponent;
				} else {
					return lastPlayer;
				}
			}
		}

		// If last move moved piece onto central place, lastPlayer wins immediately
		if (lastMove.getDestination().equals(central)) {
			return lastPlayer;
		}

		// Check if opponent has any pieces left
		boolean opponentHasPieces = false;
		int size = configuration.getSize();
		for (int x = 0; x < size && !opponentHasPieces; x++) {
			for (int y = 0; y < size; y++) {
				Piece p = board[x][y];
				if (p != null && p.getPlayer().equals(opponent)) {
					opponentHasPieces = true;
					break;
				}
			}
		}

		if (!opponentHasPieces) {
			return lastPlayer;
		}

		return null;
	}

	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		Place centralPlace = configuration.getCentralPlace();
		Piece centralBefore = board[centralPlace.x()][centralPlace.y()];
		return getWinner(lastPlayer, lastPiece, lastMove, centralBefore);
	}

	@Override
	public void updateScore(Player player, Piece piece, Move move) {
		Place source = move.getSource();
		Place dest = move.getDestination();
		int manhattanDistance = Math.abs(source.x() - dest.x()) + Math.abs(source.y() - dest.y());
		player.setScore(player.getScore() + manhattanDistance);
	}

	@Override
	public void movePiece(Move move) {
		Place source = move.getSource();
		Place dest = move.getDestination();

		Piece piece = board[source.x()][source.y()];
		board[dest.x()][dest.y()] = piece;
		board[source.x()][source.y()] = null;
	}

	@Override
	public Move[] getAvailableMoves(Player player) {
		ArrayList<Move> moves = new ArrayList<>();
		int size = configuration.getSize();

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				Piece piece = board[x][y];
				if (piece != null && piece.getPlayer().equals(player)) {
					Place source = new Place(x, y);
					Move[] pieceMoves = piece.getAvailableMoves(this, source);
					if (pieceMoves != null) {
						moves.addAll(Arrays.asList(pieceMoves));
					}
				}
			}
		}

		return moves.toArray(new Move[0]);
	}
}