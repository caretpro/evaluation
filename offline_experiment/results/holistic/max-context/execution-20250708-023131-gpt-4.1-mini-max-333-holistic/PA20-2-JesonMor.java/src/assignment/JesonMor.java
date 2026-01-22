
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

		// Deep copy initial board to avoid shared references
		int size = configuration.getSize();
		this.board = new Piece[size][size];
		Piece[][] initial = configuration.getInitialBoard();
		for (int i = 0; i < size; i++) {
			System.arraycopy(initial[i], 0, this.board[i], 0, size);
		}

		// Use players from configuration directly to keep identity consistent
		Player[] players = configuration.getPlayers();
		this.currentPlayer = null;
		this.refreshOutput();

		int playerCount = players.length;
		int currentIndex = 0;

		// To detect deadlock (no moves for all players)
		int consecutiveNoMoveTurns = 0;

		while (true) {
			this.currentPlayer = players[currentIndex];
			Move[] availableMoves = getAvailableMoves(this.currentPlayer);

			if (availableMoves.length == 0) {
				consecutiveNoMoveTurns++;
				if (consecutiveNoMoveTurns >= playerCount) {
					// Deadlock: no moves for all players, game ends with no winner
					return null;
				}
				// Skip turn
				currentIndex = (currentIndex + 1) % playerCount;
				continue;
			} else {
				consecutiveNoMoveTurns = 0;
			}

			Move chosenMove = this.currentPlayer.nextMove(this, availableMoves);
			movePiece(chosenMove);
			this.numMoves++;

			Piece movedPiece = this.getPiece(chosenMove.getDestination());
			updateScore(this.currentPlayer, movedPiece, chosenMove);

			winner = getWinner(this.currentPlayer, movedPiece, chosenMove);

			this.refreshOutput();

			if (winner != null) {
				System.out.println();
				System.out.println("Congratulations! ");
				System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
				return winner;
			}

			currentIndex = (currentIndex + 1) % playerCount;
		}
	}

	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		if (lastPlayer == null || lastPiece == null || lastMove == null) {
			return null;
		}

		// Protection moves: no winner before protection moves passed
		if (this.numMoves < this.configuration.getNumMovesProtection()) {
			return null;
		}

		Place central = this.configuration.getCentralPlace();

		// 1. Win by moving onto the central place
		if (lastMove.getDestination().equals(central)) {
			return lastPlayer;
		}

		// 2. Win by capturing all opponent pieces (opponent has no pieces left)
		Player[] players = this.configuration.getPlayers();
		Player opponent = null;
		for (Player p : players) {
			if (!p.equals(lastPlayer)) {
				opponent = p;
				break;
			}
		}
		if (opponent != null) {
			boolean opponentHasPieces = false;
			int size = this.configuration.getSize();
			for (int x = 0; x < size && !opponentHasPieces; x++) {
				for (int y = 0; y < size && !opponentHasPieces; y++) {
					Piece piece = this.board[x][y];
					if (piece != null && piece.getPlayer().equals(opponent)) {
						opponentHasPieces = true;
					}
				}
			}
			if (!opponentHasPieces) {
				return lastPlayer;
			}
		}

		// 3. Win by opponent leaving the central place (last move source was central place)
		// Check if central place is empty or occupied by lastPlayer
		Piece centralPiece = this.board[central.x()][central.y()];
		if (centralPiece == null || !centralPiece.getPlayer().equals(opponent)) {
			// Opponent left central place, lastPlayer wins
			return lastPlayer;
		}

		return null;
	}

	@Override
	public void updateScore(Player player, Piece piece, Move move) {
		if (player == null || piece == null || move == null) {
			return;
		}
		Place src = move.getSource();
		Place dst = move.getDestination();
		int dist = Math.abs(src.x() - dst.x()) + Math.abs(src.y() - dst.y());
		player.setScore(player.getScore() + dist);
	}

	@Override
	public void movePiece(Move move) {
		Place src = move.getSource();
		Place dst = move.getDestination();
		Piece piece = this.board[src.x()][src.y()];
		this.board[dst.x()][dst.y()] = piece;
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