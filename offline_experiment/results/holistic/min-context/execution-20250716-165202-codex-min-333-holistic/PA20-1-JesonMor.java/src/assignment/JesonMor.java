
package assignment;

import assignment.piece.Knight;
import assignment.protocol.Color;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Player;
import assignment.protocol.Place;
import assignment.protocol.Piece;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * JesonMor game implementation.
 */
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
        this.refreshOutput();

        // order of play
        Player[] players = configuration.getPlayers();
        int turn = 0;

        while (true) {
            // pick next player
            currentPlayer = players[turn];

            // collect moves and ask player for one
            Move[] moves = getAvailableMoves(currentPlayer);
            Move chosen = currentPlayer.nextMove(this, moves);

            // perform move
            movePiece(chosen);
            // lookup the moved piece at its destination
            Place dst = chosen.getDst();
            Piece moved = board[dst.getX()][dst.getY()];

            // update score & output, increment move count
            updateScore(currentPlayer, moved, chosen);
            numMoves++;
            refreshOutput();

            // check winner
            winner = getWinner(currentPlayer, moved, chosen);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n",
                                  winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }

            // next turn (round‑robin)
            turn = (turn + 1) % players.length;
        }
    }

    /**
     * Get the winner of the game. If there is no winner yet, return null;
     * This method will be called every time after a player makes a move and after
     * {@link JesonMor#updateScore(Player, Piece, Move)} is called, in order to
     * check whether any {@link Player} wins.
     * If this method returns a player (the winner), then the game will exit with the winner.
     * If this method returns null, next player will be asked to make a move.
     *
     * @param lastPlayer the last player who made a move
     * @param lastPiece  the last piece that was moved
     * @param lastMove   the last move made
     * @return the winner if it exists, otherwise null
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // A Knight reaching the opponent’s home rank wins immediately.
        if (lastPiece instanceof Knight) {
            int toY = lastMove.getDst().getY();
            int height = board[0].length;
            // white wins if reaching bottom row, black if reaching top row
            if (lastPlayer.isWhite() && toY == height - 1) {
                return lastPlayer;
            } else if (!lastPlayer.isWhite() && toY == 0) {
                return lastPlayer;
            }
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
     * <strong>Attention: do not need to validate move in this method.</strong>
     *
     * @param player the player who just made a move
     * @param piece  the piece that was just moved
     * @param move   the move that was just made
     */
    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.getSrc().getX() - move.getDst().getX());
        int dy = Math.abs(move.getSrc().getY() - move.getDst().getY());
        player.setScore(player.getScore() + dx + dy);
    }

    /**
     * Make a move.
     * This method performs moving a {@link Piece} from source to destination {@link Place} according to {@link Move}.
     * After the move, there will be no {@link Piece} at the source {@link Place}.
     *
     * Positions of all {@link Piece}s on the gameboard are stored in {@link JesonMor#board} as a 2D array of
     * {@link Piece} objects.
     *
     * @param move the move to make
     */
    @Override
    public void movePiece(Move move) {
        Place src = move.getSrc();
        Place dst = move.getDst();
        Piece p = board[src.getX()][src.getY()];
        board[src.getX()][src.getY()] = null;
        board[dst.getX()][dst.getY()] = p;
    }

    /**
     * Get all available moves of one player.
     * This method is called when it is the {@link Player}'s turn to make a move.
     * It will iterate all {@link Piece}s belonging to the {@link Player} on board and obtain available moves of
     * each piece through {@link Piece#getAvailableMoves(Game, Place)}.
     * <p>
     * <strong>Attention: Student should make sure all {@link Move}s returned are valid.</strong>
     *
     * @param player the player whose available moves to get
     * @return an array of available moves
     */
    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> all = new ArrayList<>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                Piece p = board[x][y];
                if (p != null && p.getPlayer().equals(player)) {
                    Place loc = new Place(x, y);
                    all.addAll(Arrays.asList(p.getAvailableMoves(this, loc)));
                }
            }
        }
        return all.toArray(Move[]::new);
    }
}