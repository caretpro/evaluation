
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JesonMor extends Game {
	public JesonMor(Configuration configuration) {
		super(configuration);
	}

	/**
	 * Start the game
	 * Players will take turns according to the order in {@link Configuration#getPlayers()} to make a move until
	 * a player wins.
	 * <p>
	 * In the implementation, student should implement the loop letting two players take turns to move pieces.
	 * The order of the players should be consistent to the order in {@link Configuration#getPlayers()}.
	 * {@link Player#nextMove(Game, Move[])} should be used to retrieve the player's choice of his next move.
	 * After each move, {@link Game#refreshOutput()} should be called to refresh the gameboard printed in the console.
	 * <p>
	 * When a winner appears, set the local variable {@code winner} so that this method can return the winner.
	 *
	 * @return the winner
	 */
	@Override
	public Player start() {
		// reset all things
		Player winner = null;
		this.numMoves = 0;
		this.board = configuration.getInitialBoard();
		this.currentPlayer = null;
		Player[] players = configuration.getPlayers();
		int currentPlayerIndex = 0;
		this.refreshOutput();
		while (true) {
			currentPlayer = players[currentPlayerIndex];
			Move[] availableMoves = getAvailableMoves(currentPlayer);

			if (availableMoves.length == 0) {
				// No moves available, check if the player is in checkmate
				boolean hasValidMoves = false;
				for (Move move : getAvailableMoves(currentPlayer)) {
					JesonMor tempGame = new JesonMor(configuration);
					tempGame.board = Arrays.stream(board).map(Piece[]::clone).toArray(Piece[][]::new);
					tempGame.movePiece(move);
					if (tempGame.getWinner(currentPlayer, tempGame.board[move.destination().x()][move.destination().y()], move) == null) {
						hasValidMoves = true;
						break;
					}
				}
				if (!hasValidMoves) {
					// The current player has no valid moves, so the other player wins
					winner = players[(currentPlayerIndex + 1) % players.length];
					break;
				}
			}

			Move nextMove = currentPlayer.nextMove(this, availableMoves);
			if (nextMove == null) {
				winner = players[(currentPlayerIndex + 1) % players.length];
				break;
			}

			Piece piece = board[nextMove.source().x()][nextMove.source().y()];
			movePiece(nextMove);
			updateScore(currentPlayer, piece, nextMove);
			refreshOutput();
			numMoves++;

			winner = getWinner(currentPlayer, piece, nextMove);
			if (winner != null) {
				break;
			}

			currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
			if (numMoves > 1000) {
				System.out.println("Reached maximum number of moves. Game ends in a draw.");
				return null;
			}
		}
		if (winner != null) {
			System.out.println();
			System.out.println("Congratulations! ");
			System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
			return winner;
		}
		return null;
	}

	/**
	 * Get the winner of the game. If there is no winner yet, return null;
	 * This method will be called every time after a player makes a move and after
	 * {@link JesonMor#updateScore(Player, Piece)} is called, in order to
	 * check whether any {@link Player} wins.
	 * If this method returns null, next player will be asked to make a move.
	 *
	 * @param lastPlayer the last player who makes a move
	 * @param lastMove   the last move made by lastPlayer
	 * @param lastPiece  the last piece that is moved by the player
	 * @return the winner if it exists, otherwise return null
	 */
	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		boolean opponentHasPieces = false;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] != null && !board[i][j].getOwner().equals(lastPlayer)) {
					opponentHasPieces = true;
					break;
				}
			}
			if (opponentHasPieces) {
				break;
			}
		}

		if (!opponentHasPieces) {
			return lastPlayer;
		}

		return null;
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
		int manhattanDistance = Math.abs(move.source().x() - move.destination().x()) + Math.abs(move.source().y() - move.destination().y());
		player.setScore(player.getScore() + manhattanDistance);
	}

	/**
	 * Make a move.
	 * This method performs moving a {@link Piece} from source to destination {@link Place} according {@link Move} object.
	 * Note that after the move, there will be no {@link Piece} in source {@link Place}.
	 * <p>
	 * Positions of all {@link Piece}s on the gameboard are stored in {@link JesonMor#board} field as a 2-dimension array of
	 * {@link Piece} objects.
	 * The x and y coordinate of a {@link Place} on the gameboard are used as index in {@link JesonMor#board}.
	 * E.g. {@code board[place.x()][place.y()]}.
	 * If one {@link Place} does not have a piece on it, it will be null in {@code board[place.x()][place.y()]}.
	 * Student may modify elements in {@link JesonMor#board} to implement moving a {@link Piece}.
	 * The {@link Move} object can be considered valid on present gameboard.
	 *
	 * @param move the move to make
	 */
	public void movePiece(Move move) {
		board[move.destination().x()][move.destination().y()] = board[move.source().x()][move.source().y()];
		board[move.source().x()][move.source().y()] = null;
	}

	/**
	 * Get all available moves of one player.
	 * This method is called when it is the {@link Player}'s turn to make a move.
	 * It will iterate all {@link Piece}s belonging to the {@link Player} on board and obtain available moves of
	 * each of the {@link Piece}s through method {@link Piece#getAvailableMoves(Game, Place)} of each {@link Piece}.
	 * <p>
	 * <strong>Attention: Student should make sure all {@link Move}s returned are valid.</strong>
	 *
	 * @param player the player whose available moves to get
	 * @return an array of available moves
	 */
	public Move[] getAvailableMoves(Player player) {
		List<Move> moves = new ArrayList<>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				Piece piece = board[i][j];
				if (piece != null && piece.getOwner().equals(player)) {
					Place place = new Place(i, j);
					Move[] pieceMoves = piece.getAvailableMoves(this, place);
					moves.addAll(Arrays.asList(pieceMoves));
				}
			}
		}
		return moves.toArray(new Move[0]);
	}
}