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
		int idx = 0;
		while (winner == null) {
			this.currentPlayer = players[idx];
			Move[] moves = getAvailableMoves(currentPlayer);
			Move chosen = currentPlayer.nextMove(this, moves);
			movePiece(chosen);
			this.numMoves++;
			updateScore(currentPlayer, board[chosen.to().x()][chosen.to().y()], chosen);
			this.refreshOutput();
			winner = getWinner(currentPlayer, board[chosen.to().x()][chosen.to().y()], chosen);
			idx = (idx + 1) % players.length;
		}
		System.out.println();
		System.out.println("Congratulations! ");
		System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
		return winner;
	}

	/**
	 * Get the winner of the game. If there is no winner yet, return null; This method will be called every time after a player makes a move and after {@link JesonMor#updateScore(Player,Piece,Move)}  is called, in order to check whether any  {@link Player}  wins. If this method returns a player (the winner), then the game will exit with the winner. If this method returns null, next player will be asked to make a move.
	 * @param lastPlayer  the last player who makes a move
	 * @param lastMove    the last move made by lastPlayer
	 * @param lastPiece   the last piece that is moved by the player
	 * @return  the winner if it exists, otherwise return null
	 */
	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		int destY = lastMove.to().y();
		int height = board[0].length;
		int opponentBackRank = (lastPlayer.getId() == 0 ? height - 1 : 0);
		if (destY == opponentBackRank && lastPiece instanceof Knight) {
			return lastPlayer;
		}
		Player[] players = configuration.getPlayers();
		Player nextPlayer = players[(lastPlayer.getId() + 1) % players.length];
		Move[] nextMoves = getAvailableMoves(nextPlayer);
		if (nextMoves.length == 0) {
			return lastPlayer;
		}
		return null;
	}

	/**
	 * Update the score of a player according to the  {@link Piece}  and corresponding move made by him just now. This method will be called every time after a player makes a move, in order to update the corresponding score of this player. <p> The score of a player is the cumulative score of each move he makes. The score of each move is calculated with the Manhattan distance between the source and destination  {@link Place} . <p> Student can use  {@link Player#getScore()}  to get the current score of a player before updating. {@link Player#setScore(int)}  can be used to update the score of a player. <p> <strong>Attention: do not need to validate move in this method.</strong>
	 * @param player  the player who just makes a move
	 * @param piece   the piece that is just moved
	 * @param move    the move that is just made
	 */
	public void updateScore(Player player, Piece piece, Move move) {
		Place src = move.from();
		Place dst = move.to();
		int dx = Math.abs(src.x() - dst.x());
		int dy = Math.abs(src.y() - dst.y());
		int manhattan = dx + dy;
		player.setScore(player.getScore() + manhattan);
	}

	/**
	 * Make a move. This method performs moving a  {@link Piece}  from source to destination  {@link Place}  according  {@link Move}  object. Note that after the move, there will be no  {@link Piece}  in source  {@link Place} . <p> Positions of all  {@link Piece} s on the gameboard are stored in  {@link JesonMor#board}  field as a 2-dimension array of {@link Piece}  objects. The x and y coordinate of a  {@link Place}  on the gameboard are used as index in  {@link JesonMor#board} . E.g.  {@code  board[place.x()][place.y()]} . If one  {@link Place}  does not have a piece on it, it will be null in  {@code  board[place.x()][place.y()]} . Student may modify elements in  {@link JesonMor#board}  to implement moving a  {@link Piece} . The  {@link Move}  object can be considered valid on present gameboard.
	 * @param move  the move to make
	 */
	@Override
	public void movePiece(Move move) {
		Place src = move.from();
		Place dst = move.to();
		Piece moving = board[src.x()][src.y()];
		board[dst.x()][dst.y()] = moving;
		board[src.x()][src.y()] = null;
	}

	@Override
	public Move[] getAvailableMoves(Player player) {
		java.util.List<Move> result = new java.util.ArrayList<>();
		int width = board.length;
		int height = board[0].length;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Piece p = board[x][y];
				if (p != null && p.getOwner().equals(player)) {
					Place from = new Place(x, y);
					Move[] raw = p.getAvailableMoves(this, from);
					if (raw != null) {
						for (Move m : raw) {
							if (m != null) {
								Place to = m.to();
								int tx = to.x(), ty = to.y();
								if (0 <= tx && tx < width && 0 <= ty && ty < height) {
									result.add(m);
								}
							}
						}
					}
				}
			}
		}
		return result.toArray(new Move[0]);
	}
}
