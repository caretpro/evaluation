
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * JesonMor: the classical 2‑player knight duel on an odd‑sized board.
 */
public class JesonMor extends Game {

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    /**
     * Start the game.
     * Players alternate turns in configuration.getPlayers() order until one wins or a tie lock occurs.
     */
    @Override
    public Player start() {
        // reset state
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        refreshOutput();

        Player[] players = configuration.getPlayers();
        int turn = 0;

        // For deadlock detection: if two consecutive full rounds produce no captures AND no central wins, it's a tie.
        int roundsWithoutProgress = 0;
        int captureOrWinThreshold = players.length; // one full cycle of all players

        while (true) {
            currentPlayer = players[turn];
            // collect all legal moves for current player
            Move[] avail = getAvailableMoves(currentPlayer);
            Move move = currentPlayer.nextMove(this, avail);

            // if no move => deadlock
            if (move == null) {
                return deadlockBreaker();
            }

            // execute move
            Piece moved = board[move.getSource().x()][move.getSource().y()];
            movePiece(move);
            numMoves++;
            updateScore(currentPlayer, moved, move);
            refreshOutput();

            // check winner
            Player winner = getWinner(currentPlayer, moved, move);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n",
                                  winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }

            // track progress: central‑square or capture resets counter
            if (lastActionWasProgress) {
                roundsWithoutProgress = 0;
            } else if (++roundsWithoutProgress >= captureOrWinThreshold) {
                return deadlockBreaker();
            }

            turn = (turn + 1) % players.length;
        }
    }

    // flag set by movePiece to indicate capture or central‑square win on that move
    private boolean lastActionWasProgress = false;

    /**
     * Break a deadlock by highest score (tie -> first‑mover wins).
     */
    private Player deadlockBreaker() {
        Player[] players = configuration.getPlayers();
        Player winner = players[0];
        for (int i = 1; i < players.length; i++) {
            Player p = players[i];
            if (p.getScore() > winner.getScore()) {
                winner = p;
            }
        }
        return winner;
    }

    /**
     * Determine winner after the last move.
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // no real move yet
        if (lastPiece == null || lastMove == null) {
            lastActionWasProgress = false;
            return null;
        }
        // protection phase: neither captures nor central wins allowed
        if (numMoves <= configuration.getNumMovesProtection()) {
            lastActionWasProgress = false;
            return null;
        }
        Place dest = lastMove.getDestination();

        // central-square win
        if (dest.equals(getCentralPlace())) {
            lastActionWasProgress = true;
            return lastPlayer;
        }
        // capture win
        if (lastPiece instanceof Knight && captureFlag) {
            lastActionWasProgress = true;
            return lastPlayer;
        }

        lastActionWasProgress = false;
        return null;
    }

    /**
     * Update the score: Manhattan distance of the move.
     */
    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.getSource().x() - move.getDestination().x());
        int dy = Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + dx + dy);
    }

    /**
     * Perform the knight move (including capture detection).
     */
    @Override
    public void movePiece(@NotNull Move move) {
        Place s = move.getSource(), d = move.getDestination();
        Piece p = board[s.x()][s.y()];
        Piece target = board[d.x()][d.y()];
        captureFlag = (target != null && !target.getPlayer().equals(p.getPlayer()));
        board[d.x()][d.y()] = p;
        board[s.x()][s.y()] = null;
    }

    // helper set in movePiece → getWinner
    private boolean captureFlag = false;

    /**
     * Generate all legal knight moves for a player, honoring protection rules.
     */
    @Override
    public @NotNull Move[] getAvailableMoves(Player player) {
        int size = configuration.getSize();
        List<Move> moves = new ArrayList<>();
        Place central = getCentralPlace();
        int protection = configuration.getNumMovesProtection();

        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {2, 1, -1, -2, -2, -1, 1, 2};

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = board[x][y];
                if (p instanceof Knight && p.getPlayer().equals(player)) {
                    Place src = new Place(x, y);
                    for (int k = 0; k < 8; k++) {
                        int nx = x + dx[k], ny = y + dy[k];
                        if (nx < 0 || nx >= size || ny < 0 || ny >= size) continue;
                        Place dst = new Place(nx, ny);
                        Piece occ = board[nx][ny];
                        boolean capture = (occ != null && !occ.getPlayer().equals(player));
                        // first-N protection: ban captures & central until expired
                        if (numMoves < protection && (capture || dst.equals(central))) {
                            continue;
                        }
                        // can't land on own piece
                        if (occ != null && occ.getPlayer().equals(player)) {
                            continue;
                        }
                        moves.add(new Move(src, dst));
                    }
                }
            }
        }
        return moves.toArray(Move[]::new);
    }
}