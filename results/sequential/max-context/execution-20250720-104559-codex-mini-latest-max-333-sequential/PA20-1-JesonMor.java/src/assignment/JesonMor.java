
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Jeson Mor game implementation.
 */
public class JesonMor extends Game {
    private Player lastMover = null;   // track who just moved when deadlock/tie occurs

    public JesonMor(Configuration configuration) {
        super(configuration);
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
    @Override
    public @NotNull Move[] getAvailableMoves(Player player) {
        ArrayList<Move> all = new ArrayList<>();
        int size = configuration.getSize();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = board[x][y];
                if (p != null && p.getPlayer().equals(player)) {
                    Place src = new Place(x, y);
                    for (Move m : p.getAvailableMoves(this, src)) {
                        // source must still have this piece
                        if (!src.equals(m.getSource()) || board[x][y] != p) {
                            continue;
                        }
                        Place dst = m.getDestination();
                        // during protection phase, cannot land on occupied square
                        if (numMoves < configuration.getNumMovesProtection()
                            && board[dst.x()][dst.y()] != null) {
                            continue;
                        }
                        all.add(m);
                    }
                }
            }
        }
        return all.toArray(Move[]::new);
    }

    /**
     * Start the game.
     * Players alternate moves until someone wins, or until both are dead‑locked (tie‑breaker: last mover wins).
     */
    @Override
    public Player start() {
        Player winner = null;
        this.board = configuration.getInitialBoard();
        this.numMoves = 0;
        this.currentPlayer = null;
        this.lastMover = null;
        this.refreshOutput();

        while (true) {
            boolean anyMovedThisRound = false;

            for (Player player : configuration.getPlayers()) {
                this.currentPlayer = player;
                Move[] available = this.getAvailableMoves(player);
                if (available.length == 0) {
                    // cannot move => skip turn
                    continue;
                }

                anyMovedThisRound = true;
                Move chosen = player.nextMove(this, available);

                // Apply move
                Piece movedPiece = this.getPiece(chosen.getSource());
                boolean leavingCentral = chosen.getSource().equals(getCentralPlace());
                this.movePiece(chosen);
                this.numMoves++;
                this.updateScore(player, movedPiece, chosen);
                this.lastMover = player;
                this.refreshOutput();

                // Check immediate wins
                winner = this.getWinner(player, movedPiece, chosen);
                if (winner != null) {
                    break;
                }
                // Central square departure win
                if (leavingCentral) {
                    winner = player;
                    break;
                }
            }

            // dead‑lock (no one could move): tie‑breaker => lastMover wins
            if (!anyMovedThisRound) {
                winner = lastMover;
            }

            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations!");
                System.out.printf("Winner: %s%s%s\n",
                                  winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
        }
    }

    /**
     * Get the winner after a move, or null if none yet.
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }

        // 1) Capture‐all knights => lastPlayer wins
        Place dest = lastMove.getDestination();
        Piece atDest = this.getPiece(dest);
        if (lastPiece == atDest) {
            boolean opponentHasKnight = false;
            for (int x = 0; x < configuration.getSize() && !opponentHasKnight; x++) {
                for (int y = 0; y < configuration.getSize(); y++) {
                    Piece p = board[x][y];
                    if (p != null && p.getPlayer() != lastPlayer && p instanceof Knight) {
                        opponentHasKnight = true;
                        break;
                    }
                }
            }
            if (!opponentHasKnight) {
                return lastPlayer;
            }
        }

        // 2) Move into central square => lastPlayer wins
        if (lastMove.getDestination().equals(this.getCentralPlace())) {
            return lastPlayer;
        }

        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place src = move.getSource();
        Place dst = move.getDestination();
        player.setScore(player.getScore() +
                        Math.abs(src.x() - dst.x()) +
                        Math.abs(src.y() - dst.y()));
    }

    @Override
    public void movePiece(Move move) {
        Place s = move.getSource();
        Place d = move.getDestination();
        Piece p = board[s.x()][s.y()];
        board[d.x()][d.y()] = p;
        board[s.x()][s.y()] = null;
    }
}