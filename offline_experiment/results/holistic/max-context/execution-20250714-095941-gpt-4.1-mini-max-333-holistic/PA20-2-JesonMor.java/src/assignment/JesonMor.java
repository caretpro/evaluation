
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

		// Deep copy initial board to avoid modifying configuration's initial board
		int size = configuration.getSize();
		this.board = new Piece[size][size];
		Piece[][] initial = configuration.getInitialBoard();
		for (int x = 0; x < size; x++) {
			System.arraycopy(initial[x], 0, this.board[x], 0, size);
		}

		Player[] players = configuration.getPlayers();
		int playerCount = players.length;
		int currentIndex = 0;
		this.currentPlayer = players[currentIndex];

		this.refreshOutput();

		while (true) {
			this.currentPlayer = players[currentIndex];
			Move[] availableMoves = getAvailableMoves(this.currentPlayer);

			// If no moves available for current player
			if (availableMoves.length == 0) {
				// Check if opponent also has no moves -> tie or decide winner by score
				int opponentIndex = (currentIndex + 1) % playerCount;
				Player opponent = players[opponentIndex];
				Move[] opponentMoves = getAvailableMoves(opponent);

				if (opponentMoves.length == 0) {
					// Both players have no moves -> tie or highest score wins
					int scoreCurrent = this.currentPlayer.getScore();
					int scoreOpponent = opponent.getScore();
					if (scoreCurrent > scoreOpponent) {
						winner = this.currentPlayer;
					} else if (scoreOpponent > scoreCurrent) {
						winner = opponent;
					} else {
						// tie, no winner
						winner = null;
					}
					break;
				} else {
					// Opponent has moves, skip current player's turn
					currentIndex = opponentIndex;
					continue;
				}
			}

			Move chosenMove = this.currentPlayer.nextMove(this, availableMoves);
			movePiece(chosenMove);
			this.numMoves++;
			Piece movedPiece = this.getPiece(chosenMove.getDestination());
			updateScore(this.currentPlayer, movedPiece, chosenMove);
			this.refreshOutput();

			winner = getWinner(this.currentPlayer, movedPiece, chosenMove);
			if (winner != null) {
				break;
			}
			currentIndex = (currentIndex + 1) % playerCount;
		}

		if (winner != null) {
			System.out.println();
			System.out.println("Congratulations! ");
			System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
		} else {
			System.out.println();
			System.out.println("Game ended in a tie.");
		}
		return winner;
	}

	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		if (lastPlayer == null || lastPiece == null || lastMove == null) {
			return null;
		}

		// No winner during protection moves
		if (this.numMoves <= this.configuration.getNumMovesProtection()) {
			return null;
		}

		Player[] players = configuration.getPlayers();
		Player opponent = null;
		for (Player p : players) {
			if (!p.equals(lastPlayer)) {
				opponent = p;
				break;
			}
		}
		if (opponent == null) {
			return null; // should not happen
		}

		// Win by moving piece to central place
		if (lastMove.getDestination().equals(this.configuration.getCentralPlace())) {
			return lastPlayer;
		}

		// Win by opponent having no pieces left (capture all)
		boolean opponentHasPieces = false;
		int size = configuration.getSize();
		for (int x = 0; x < size && !opponentHasPieces; x++) {
			for (int y = 0; y < size && !opponentHasPieces; y++) {
				Piece p = board[x][y];
				if (p != null && p.getPlayer().equals(opponent)) {
					opponentHasPieces = true;
				}
			}
		}
		if (!opponentHasPieces) {
			return lastPlayer;
		}

		// Win by opponent having no available moves (deadlock)
		Move[] opponentMoves = getAvailableMoves(opponent);
		if (opponentMoves.length == 0) {
			return lastPlayer;
		}

		// Win by leaving central place (if last move source was central place and now empty)
		Place central = configuration.getCentralPlace();
		Piece pieceAtCentral = board[central.x()][central.y()];
		if (pieceAtCentral == null) {
			// Check if last move source was central place and last player moved piece away
			if (lastMove.getSource().equals(central)) {
				return lastPlayer;
			}
		}

		return null;
	}

	@Override
	public void updateScore(Player player, Piece piece, Move move) {
		if (player == null || move == null) {
			return;
		}
		Place src = move.getSource();
		Place dst = move.getDestination();
		int manhattanDistance = Math.abs(src.x() - dst.x()) + Math.abs(src.y() - dst.y());
		int newScore = player.getScore() + manhattanDistance;
		player.setScore(newScore);
	}

	@Override
	public void movePiece(Move move) {
		if (move == null) {
			return;
		}
		Place src = move.getSource();
		Place dst = move.getDestination();
		Piece piece = this.board[src.x()][src.y()];
		// Move piece to destination (capture if any)
		this.board[dst.x()][dst.y()] = piece;
		// Remove piece from source
		this.board[src.x()][src.y()] = null;
	}

	@Override
	public Move[] getAvailableMoves(Player player) {
		if (player == null) {
			return new Move[0];
		}
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