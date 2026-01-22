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

	/**
	 * Start the game Players will take turns according to the order in  {@link Configuration#getPlayers()}  to make a move until a player wins. <p> In the implementation, student should implement the loop letting two players take turns to move pieces. The order of the players should be consistent to the order in  {@link Configuration#getPlayers()} . {@link Player#nextMove(Game,Move[])}  should be used to retrieve the player's choice of his next move. After each move,  {@link Game#refreshOutput()}  should be called to refresh the gameboard printed in the console. <p> When a winner appears, set the local variable  {@code  winner}  so that this method can return the winner.
	 * @return  the winner
	 */
	@Override
	public Player start() {
		Player winner = null;
		this.numMoves = 0;
		this.board = configuration.getInitialBoard();
		this.currentPlayer = null;
		this.refreshOutput();
		while (true) {
			for (Player player : configuration.getPlayers()) {
				this.currentPlayer = player;
				Move[] moves = getAvailableMoves(player);
				Move chosen = player.nextMove(this, moves);
				movePiece(chosen);
				updateScore(player, board[chosen.from().x()][chosen.from().y()], chosen);
				numMoves++;
				refreshOutput();
				winner = getWinner(player, board[chosen.to().x()][chosen.to().y()], chosen);
				if (winner != null) {
					System.out.println();
					System.out.println("Congratulations! ");
					System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
					return winner;
				}
			}
		}
	}

	/**
	 * Get the winner of the game. If there is no winner yet, return null; This method will be called every time after a player makes a move and after {@link JesonMor#updateScore(Player,Piece,Move)}  is called, in order to check whether any  {@link Player}  wins. If this method returns a player (the winner), then the game will exit with the winner. If this method returns null, next player will be asked to make a move.
	 * @param lastPlayer  the last player who makes a move
	 * @param lastPiece   the last piece that is moved by the player
	 * @param lastMove    the last move made by lastPlayer
	 * @return  the winner if it exists, otherwise return null
	 */
	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		Place landing = lastMove.to();
		for (Player p : configuration.getPlayers()) {
			if (p != lastPlayer) {
				Place opponentPos = p.getPieces().get(0).getPosition();
				if (opponentPos.equals(landing)) {
					return lastPlayer;
				}
			}
		}
		return null;
	}

	@Override
	public void updateScore(Player player, Piece piece, Move move) {
		Place from = move.from();
		Place to = move.to();
		int distance = Math.abs(to.x() - from.x()) + Math.abs(to.y() - from.y());
		player.setScore(player.getScore() + distance);
	}

	/**
	 * Make a move. This method performs moving a  {@link Piece}  from source to destination  {@link Place}  according  {@link Move}  object. Note that after the move, there will be no  {@link Piece}  in source  {@link Place} . <p> Positions of all  {@link Piece} s on the gameboard are stored in  {@link JesonMor#board}  field as a 2-dimension array of {@link Piece}  objects. The x and y coordinate of a  {@link Place}  on the gameboard are used as index in  {@link JesonMor#board} . E.g.  {@code  board[place.x()][place.y()]} . If one  {@link Place}  does not have a piece on it, it will be null in  {@code  board[place.x()][place.y()]} . Student may modify elements in  {@link JesonMor#board}  to implement moving a  {@link Piece} . The  {@link Move}  object can be considered valid on present gameboard.
	 * @param move  the move to make
	 */
	public void movePiece(Move move) {
		Place from = move.from();
		Place to = move.to();
		Piece moving = board[from.x()][from.y()];
		board[from.x()][from.y()] = null;
		board[to.x()][to.y()] = moving;
		moving.setPosition(to);
	}

	@Override
	public Move[] getAvailableMoves(Player player) {
		var moves = new ArrayList<Move>();
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				Piece piece = board[x][y];
				if (piece != null && piece.getOwner() == player) {
					Place from = new Place(x, y);
					Move[] candidates = piece.getAvailableMoves(this, from);
					for (Move m : candidates) {
						Place to = m.to();
						int tx = to.x(), ty = to.y();
						if (tx < 0 || tx >= board.length || ty < 0 || ty >= board[0].length) {
							continue;
						}
						Piece dest = board[tx][ty];
						if (dest != null && dest.getOwner() == player) {
							continue;
						}
						moves.add(m);
					}
				}
			}
		}
		return moves.toArray(new Move[0]);
	}
}
