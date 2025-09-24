
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
		// reset all things
		Player winner = null;
		this.numMoves = 0;

		// Deep copy initial board to avoid modifying configuration's initialBoard and aliasing issues
		int size = configuration.getSize();
		this.board = new Piece[size][size];
		Piece[][] initial = configuration.getInitialBoard();
		for (int x = 0; x < size; x++) {
			System.arraycopy(initial[x], 0, this.board[x], 0, size);
		}

		this.currentPlayer = null;
		this.refreshOutput();

		Player[] players = configuration.getPlayers();
		int playerCount = players.length;
		int currentIndex = 0;

		while (true) {
			this.currentPlayer = players[currentIndex];
			Move[] availableMoves = getAvailableMoves(this.currentPlayer);

			// If no available moves for current player, check if all players have no moves (deadlock)
			if (availableMoves.length == 0) {
				boolean allNoMoves = true;
				for (Player p : players) {
					if (getAvailableMoves(p).length > 0) {
						allNoMoves = false;
						break;
					}
				}
				if (allNoMoves) {
					// Tie game: no winner
					return null;
				}
				// Skip this player's turn
				currentIndex = (currentIndex + 1) % playerCount;
				continue;
			}

			Move chosenMove = this.currentPlayer.nextMove(this, availableMoves);

			// Defensive: if chosenMove is null or not in availableMoves, skip turn to avoid index errors
			if (chosenMove == null || !Arrays.asList(availableMoves).contains(chosenMove)) {
				currentIndex = (currentIndex + 1) % playerCount;
				continue;
			}

			// Make the move
			movePiece(chosenMove);

			// Update score
			Piece movedPiece = this.getPiece(chosenMove.getDestination());
			updateScore(this.currentPlayer, movedPiece, chosenMove);

			this.numMoves++;

			// Check winner
			winner = getWinner(this.currentPlayer, movedPiece, chosenMove);

			this.refreshOutput();

			if (winner != null) {
				System.out.println();
				System.out.println("Congratulations! ");
				System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
				return winner;
			}

			// Next player's turn
			currentIndex = (currentIndex + 1) % playerCount;
		}
	}

	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		// No winner if protection moves not passed
		if (this.numMoves < this.configuration.getNumMovesProtection()) {
			return null;
		}

		Player[] players = this.configuration.getPlayers();
		Player opponent = null;
		for (Player p : players) {
			if (!p.equals(lastPlayer)) {
				opponent = p;
				break;
			}
		}

		// 1. Check if lastPlayer occupies the central place
		Piece pieceAtCenter = this.getPiece(this.configuration.getCentralPlace());
		if (pieceAtCenter != null && pieceAtCenter.getPlayer().equals(lastPlayer)) {
			return lastPlayer;
		}

		// 2. Check if opponent has no pieces left (captured all)
		boolean opponentHasPieces = false;
		int size = configuration.getSize();
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				Piece p = this.board[x][y];
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

		// 3. Check if opponent has no available moves (deadlock)
		if (getAvailableMoves(opponent).length == 0) {
			return lastPlayer;
		}

		// 4. No winner otherwise
		return null;
	}

	@Override
	public void updateScore(Player player, Piece piece, Move move) {
		Place src = move.getSource();
		Place dst = move.getDestination();
		int manhattanDistance = Math.abs(src.x() - dst.x()) + Math.abs(src.y() - dst.y());
		player.setScore(player.getScore() + manhattanDistance);
	}

	@Override
	public void movePiece(Move move) {
		Place src = move.getSource();
		Place dst = move.getDestination();
		Piece piece = this.getPiece(src);
		// Move piece to destination (capture if any)
		this.board[dst.x()][dst.y()] = piece;
		// Remove piece from source
		this.board[src.x()][src.y()] = null;
	}

	@Override
	public Move[] getAvailableMoves(Player player) {
		ArrayList<Move> moves = new ArrayList<>();
		int size = this.configuration.getSize();

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				Piece piece = this.board[x][y];
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