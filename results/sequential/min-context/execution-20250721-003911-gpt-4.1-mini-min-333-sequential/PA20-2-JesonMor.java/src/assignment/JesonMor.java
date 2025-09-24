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
			this.currentPlayer = players[turnIndex];
			Move[] availableMoves = getAvailableMoves(this.currentPlayer);
			if (availableMoves.length == 0) {
				turnIndex = (turnIndex + 1) % playerCount;
				continue;
			}
			Move chosenMove = this.currentPlayer.nextMove(this, availableMoves);
			movePiece(chosenMove);
			Place dest = chosenMove.getDestination();
			Piece movedPiece = board[dest.x()][dest.y()];
			updateScore(this.currentPlayer, movedPiece, chosenMove);
			this.numMoves++;
			this.refreshOutput();
			winner = getWinner(this.currentPlayer, movedPiece, chosenMove);
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
		Player[] players = configuration.getPlayers();
		for (Player player : players) {
			if (!player.equals(lastPlayer)) {
				boolean opponentHasPieces = false;
				for (int x = 0; x < board.length; x++) {
					for (int y = 0; y < board[x].length; y++) {
						Piece piece = board[x][y];
						if (piece != null && piece.getOwner().equals(player)) {
							opponentHasPieces = true;
							break;
						}
					}
					if (opponentHasPieces)
						break;
				}
				if (!opponentHasPieces) {
					return lastPlayer;
				}
			}
		}
		return null;
	}

	public void updateScore(Player player, Piece piece, Move move) {
		Place source = move.getSource();
		Place destination = move.getDestination();
		int distance = Math.abs(source.x() - destination.x()) + Math.abs(source.y() - destination.y());
		int newScore = player.getScore() + distance;
		player.setScore(newScore);
	}

	public void movePiece(Move move) {
		Place source = move.getSource();
		Place destination = move.getDestination();
		Piece piece = board[source.x()][source.y()];
		board[destination.x()][destination.y()] = piece;
		board[source.x()][source.y()] = null;
	}

	public Move[] getAvailableMoves(Player player) {
		ArrayList<Move> moves = new ArrayList<>();
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				Piece piece = board[x][y];
				if (piece != null && piece.getPlayer().equals(player)) {
					Place place = new Place(x, y);
					Move[] pieceMoves = piece.getAvailableMoves(this, place);
					if (pieceMoves != null) {
						moves.addAll(Arrays.asList(pieceMoves));
					}
				}
			}
		}
		return moves.toArray(new Move[0]);
	}
}
