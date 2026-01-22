
package assignment;

import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * JesonMor game implementation.
 */
public class JesonMor extends Game {

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    /**
     * Get all available moves of one player.
     * This method is called when it is the {@link Player}'s turn to make a move.
     * It will iterate all {@link Piece}s belonging to the {@link Player} on board and obtain available moves of
     * each of the {@link Piece}s through method {@link Piece#getAvailableMoves(Game, Place)} of each {@link Piece}.
     * <p>
     * <strong>Attention: Student should make sure all {@link Move}s returned are valid, respect the protection
     * period, and filter out off-board or null moves.</strong>
     *
     * @param player the player whose available moves to get
     * @return an array of available moves
     */
    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        int size = configuration.getSize();
        Place center = configuration.getCentralPlace();
        boolean underProtection = numMoves < configuration.getNumMovesProtection();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = board[x][y];
                if (p != null && p.getPlayer().equals(player)) {
                    Place src = new Place(x, y);
                    Move[] cand = p.getAvailableMoves(this, src);
                    if (cand == null) continue;
                    for (Move m : cand) {
                        if (m == null) continue;
                        Place dst = m.getDestination();
                        // must be on board
                        if (dst.x() < 0 || dst.x() >= size || dst.y() < 0 || dst.y() >= size) {
                            continue;
                        }
                        // cannot move onto center during protection
                        if (underProtection && dst.equals(center)) {
                            continue;
                        }
                        Piece occupant = board[dst.x()][dst.y()];
                        // cannot capture during protection
                        if (underProtection && occupant != null && !occupant.getPlayer().equals(player)) {
                            continue;
                        }
                        moves.add(m);
                    }
                }
            }
        }
        return moves.toArray(Move[]::new);
    }

    @Override
    public void movePiece(@NotNull Move move) {
        Place src = move.getSource();
        Place dst = move.getDestination();
        Piece p = board[src.x()][src.y()];
        board[dst.x()][dst.y()] = p;
        board[src.x()][src.y()] = null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place s = move.getSource();
        Place d = move.getDestination();
        player.setScore(player.getScore()
                + Math.abs(s.x() - d.x()) + Math.abs(s.y() - d.y()));
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // central‑place win
        if (lastMove.getDestination().equals(getCentralPlace())
                && numMoves >= configuration.getNumMovesProtection()) {
            return lastPlayer;
        }
        // opponent has no piece
        Player opponent = null;
        for (Player pl : configuration.getPlayers()) {
            if (!pl.equals(lastPlayer)) {
                opponent = pl;
                break;
            }
        }
        if (opponent != null) {
            boolean any = false;
            int sz = configuration.getSize();
            for (int i = 0; i < sz && !any; i++) {
                for (int j = 0; j < sz; j++) {
                    Piece q = board[i][j];
                    if (q != null && q.getPlayer().equals(opponent)) {
                        any = true;
                        break;
                    }
                }
            }
            if (!any) {
                return lastPlayer;
            }
        }
        return null;
    }

    /**
     * Start the game loop.
     * Tie‑breaker: if multiple moves are equal, use lexicographically smallest src->dst string.
     */
    @Override
    public Player start() {
        board = configuration.getInitialBoard();
        numMoves = 0;
        Player winner;
        Player[] players = configuration.getPlayers();
        int turn = 0;
        refreshOutput();
        while (true) {
            currentPlayer = players[turn % players.length];

            // collect and tie-break
            Move[] avail = getAvailableMoves(currentPlayer);
            Arrays.sort(avail, Comparator.comparing(m ->
                    String.format("%c%d%c%d",
                            (char) ('a' + m.getSource().x()), m.getSource().y() + 1,
                            (char) ('a' + m.getDestination().x()), m.getDestination().y() + 1)));
            Move chosen = currentPlayer.nextMove(this, avail);

            movePiece(chosen);
            numMoves++;
            updateScore(currentPlayer, board[chosen.getDestination().x()][chosen.getDestination().y()], chosen);
            refreshOutput();

            winner = getWinner(currentPlayer, board[chosen.getDestination().x()][chosen.getDestination().y()], chosen);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations!");
                System.out.printf("Winner: %s%s%s\n",
                        winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
            turn++;
        }
    }
}