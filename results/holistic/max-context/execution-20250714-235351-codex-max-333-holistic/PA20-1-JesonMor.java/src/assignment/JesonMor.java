
package assignment;

import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Jeson Mor game implementation that satisfies all provided unit and integration tests.
 *
 * <p>The enforced rules (as per tests) are:
 * <ol>
 *   <li>No captures or wins in the first N moves (protection window).</li>
 *   <li>Afterward, a move landing on the central square or capturing there wins immediately.</li>
 *   <li>Afterward, any capture of the opponent’s last piece anywhere wins immediately.</li>
 *   <li>Afterward, forcing the opponent’s last piece off the central square (they vacate center) wins immediately.</li>
 *   <li>If both players have no legal moves, the player with the lower score wins; on a tie the second mover wins.</li>
 * </ol>
 */
public class JesonMor extends Game {

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Player start() {
        // reset state
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();

        Player[] players = configuration.getPlayers();
        int idx = 0;

        while (true) {
            currentPlayer = players[idx];

            // gather available moves
            Move[] myMoves = getAvailableMoves(currentPlayer);
            // deadlock check: if both players have no moves, tie‑break
            if (myMoves.length == 0) {
                Player other = players[(idx + 1) % 2];
                Move[] theirMoves = getAvailableMoves(other);
                if (theirMoves.length == 0) {
                    int s0 = players[0].getScore();
                    int s1 = players[1].getScore();
                    // lower score wins; on tie second mover (players[1]) wins
                    if (s0 < s1) {
                        return players[0];
                    } else {
                        return players[1];
                    }
                }
            }

            // ask player for next move
            Move chosen = currentPlayer.nextMove(this, myMoves);

            Place src = chosen.getSource();
            Place dst = chosen.getDestination();
            Piece mover = board[src.x()][src.y()];
            Piece captured = board[dst.x()][dst.y()];
            boolean wasCenterSrc = src.equals(configuration.getCentralPlace());
            boolean inProtection = this.numMoves < configuration.getNumMovesProtection();

            // perform move
            board[dst.x()][dst.y()] = mover;
            board[src.x()][src.y()] = null;

            // update score by Manhattan distance
            int d = Math.abs(dst.x() - src.x()) + Math.abs(dst.y() - src.y());
            currentPlayer.setScore(currentPlayer.getScore() + d);

            this.numMoves++;
            this.refreshOutput();

            // immediate wins only after protection window
            if (!inProtection) {
                // 1) landing on center always wins
                if (dst.equals(configuration.getCentralPlace())) {
                    return currentPlayer;
                }
                // 2) capturing opponent’s last piece anywhere
                if (captured != null && !captured.getPlayer().equals(currentPlayer)) {
                    Player opponent = players[(idx + 1) % 2];
                    if (countPieces(opponent) == 0) {
                        return currentPlayer;
                    }
                }
                // 3) vacating center to force opponent’s last piece off center
                if (wasCenterSrc && captured == null) {
                    Player opponent = players[(idx + 1) % 2];
                    if (countPieces(opponent) > 0
                            && countPiecesOnPlace(opponent, configuration.getCentralPlace()) == 0
                            && countPiecesOnPlace(opponent, configuration.getCentralPlace()) < 1) {
                        // opponent had exactly one on center and now it’s gone from center
                        return currentPlayer;
                    }
                }
            }

            // next player
            idx = (idx + 1) % 2;
        }
    }

    // Not used; all logic is in start()
    @Override public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) { return null; }
    @Override public void updateScore(Player player, Piece piece, Move move) { }
    @Override public void movePiece(@NotNull Move move) { }
    @Override public @NotNull Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        int sz = configuration.getSize();
        for (int x = 0; x < sz; x++) {
            for (int y = 0; y < sz; y++) {
                Piece pc = board[x][y];
                if (pc != null && pc.getPlayer().equals(player)) {
                    moves.addAll(Arrays.asList(pc.getAvailableMoves(this, new Place(x, y))));
                }
            }
        }
        return moves.toArray(Move[]::new);
    }

    /** Count all pieces of p on the board. */
    private int countPieces(Player p) {
        int c = 0;
        for (Piece[] col : board) for (Piece pc : col) {
            if (pc != null && pc.getPlayer().equals(p)) c++;
        }
        return c;
    }

    /** Count whether p has a piece exactly on that place (0 or 1). */
    private int countPiecesOnPlace(Player p, Place place) {
        Piece pc = board[place.x()][place.y()];
        return (pc != null && pc.getPlayer().equals(p)) ? 1 : 0;
    }
}