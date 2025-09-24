
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * JesonMor is a simple two‐player turn‐based game: players take turns moving Knights on the board.
 * The first player to move a Knight onto the opponent’s starting Knight position wins.
 */
public class JesonMor extends Game {

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    /**
     * Start the game.
     * Players will take turns according to the order in {@link Configuration#getPlayers()} to make a move until a player wins.
     */
    @Override
    public Player start() {
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.refreshOutput();

        Player[] players = configuration.getPlayers();
        int turn = 0;

        while (true) {
            Player current = players[turn % players.length];
            Move[] options = getAvailableMoves(current);
            Move next = current.nextMove(this, options);

            movePiece(next);
            numMoves++;
            // update score based on the moved piece ending at next.getDest()
            Piece moved = board[next.getDest().x()][next.getDest().y()];
            updateScore(current, moved, next);
            refreshOutput();

            Player winner = getWinner(current, moved, next);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s%n", winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }

            turn++;
        }
    }

    /**
     * Get the winner of the game. If there is no winner yet, return null.
     * The winning condition: a player wins when he moves his Knight into the opponent’s original Knight start position.
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (!(lastPiece instanceof Knight)) {
            return null;
        }
        // destination of the last move
        Place dest = lastMove.getDest();

        // find opponent and check if dest matches their starting Knight place
        for (Player p : configuration.getPlayers()) {
            if (!Objects.equals(p, lastPlayer)) {
                Map<Class<? extends Piece>, java.util.List<Place>> map = configuration.getInitialPlacesMap(p);
                Place oppStart = map.get(Knight.class).get(0);
                if (dest.equals(oppStart)) {
                    return lastPlayer;
                }
            }
        }
        return null;
    }

    /**
     * Update the score of a player according to the Manhattan distance moved.
     */
    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place src = move.getSrc();
        Place dst = move.getDest();
        int dist = Math.abs(src.x() - dst.x()) + Math.abs(src.y() - dst.y());
        player.setScore(player.getScore() + dist);
    }

    /**
     * Perform the move on the board (move piece from src to dest).
     */
    @Override
    public void movePiece(Move move) {
        Place src = move.getSrc();
        Place dst = move.getDest();
        Piece p = board[src.x()][src.y()];
        board[src.x()][src.y()] = null;
        board[dst.x()][dst.y()] = p;
    }

    /**
     * Collect all valid moves for the given player by querying each of their pieces.
     */
    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece p = board[x][y];
                if (p != null && p.getOwner().equals(player)) {
                    Place here = new Place(x, y);
                    for (Move m : p.getAvailableMoves(this, here)) {
                        moves.add(m);
                    }
                }
            }
        }
        return moves.toArray(Move[]::new);
    }
}