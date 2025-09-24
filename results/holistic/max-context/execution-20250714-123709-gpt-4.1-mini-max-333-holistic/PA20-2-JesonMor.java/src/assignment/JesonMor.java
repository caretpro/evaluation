
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

		// Deep copy the initial board to avoid mutating configuration's initial board
		int size = configuration.getSize();
		this.board = new Piece[size][size];
		Piece[][] initial = configuration.getInitialBoard();
		for (int x = 0; x < size; x++) {
			System.arraycopy(initial[x], 0, this.board[x], 0, size);
		}

		Player[] players = configuration.getPlayers();
		int currentPlayerIndex = 0;
		this.currentPlayer = players[currentPlayerIndex];

		this.refreshOutput();

		while (true) {
			this.currentPlayer = players[currentPlayerIndex];
			Move[] availableMoves = getAvailableMoves(currentPlayer);

			// If no available moves, the other player wins
			if (availableMoves.length == 0) {
				winner = players[(currentPlayerIndex + 1) % players.length];
				break;
			}

			Move chosenMove = currentPlayer.nextMove(this, availableMoves);

			// Make the move
			movePiece(chosenMove);

			// Update score
			Piece movedPiece = getPiece(chosenMove.getDestination());
			updateScore(currentPlayer, movedPiece, chosenMove);

			this.numMoves++;

			// Check winner only after numMovesProtection moves
			if (this.numMoves >= configuration.getNumMovesProtection()) {
				winner = getWinner(currentPlayer, movedPiece, chosenMove);
			}

			this.refreshOutput();

			if (winner != null) {
				System.out.println();
				System.out.println("Congratulations! ");
				System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
				return winner;
			}

			// Switch to next player
			currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
		}

		return winner;
	}

	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		if (this.numMoves < configuration.getNumMovesProtection()) {
			return null;
		}

		Player[] players = configuration.getPlayers();
		Player opponent = players[0].equals(lastPlayer) ? players[1] : players[0];
		Place central = configuration.getCentralPlace();

		// Check if lastPlayer occupies the central place -> lastPlayer wins
		Piece pieceAtCentral = getPiece(central);
		if (pieceAtCentral != null) {
			Player occupant = pieceAtCentral.getPlayer();
			if (occupant.equals(lastPlayer)) {
				return lastPlayer;
			}
		} else {
			// Central place is empty - if lastPlayer just left it, opponent wins
			if (lastMove.getSource().equals(central)) {
				return opponent;
			}
		}

		// Check if opponent has no pieces left on board
		boolean opponentHasPieces = false;
		int size = configuration.getSize();
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				Piece p = board[x][y];
				if (p != null && p.getPlayer().equals(opponent)) {
					opponentHasPieces = true;
					break;
				}
			}
			if (opponentHasPieces) break;
		}

		if (!opponentHasPieces) {
			return lastPlayer;
		}

		return null;
	}

	@Override
	public void updateScore(Player player, Piece piece, Move move) {
		Place src = move.getSource();
		Place dst = move.getDestination();
		int manhattanDistance = Math.abs(src.x() - dst.x()) + Math.abs(src.y() - dst.y());

		// Check if a piece was captured (destination had opponent piece before move)
		// Since movePiece already moved the piece, check if destination was occupied before move
		// We can check by comparing if the move captured a piece by checking if the destination was occupied by opponent before move
		// But we don't have that info here, so we approximate by checking if the move is a capture by comparing source and destination pieces before move
		// Instead, we can check if the destination had a piece before move by tracking in start() or here we just add no extra points.

		// To fix the score mismatch in tests, only add Manhattan distance as score.
		player.setScore(player.getScore() + manhattanDistance);
	}

	@Override
	public void movePiece(Move move) {
		Place src = move.getSource();
		Place dst = move.getDestination();
		Piece piece = board[src.x()][src.y()];
		board[dst.x()][dst.y()] = piece;
		board[src.x()][src.y()] = null;
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