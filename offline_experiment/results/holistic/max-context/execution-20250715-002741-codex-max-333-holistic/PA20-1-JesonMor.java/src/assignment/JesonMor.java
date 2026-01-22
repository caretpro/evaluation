
package assignment;

import assignment.piece.Archer;
import assignment.piece.Knight;
import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * JesonMor—the main game orchestration: move loop, scoring, win/draw checking, tie‑breaker.
 */
public class JesonMor extends Game {

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    /**
     * The main game loop.
     * Alternates players in configuration order; on deadlock (no moves), invokes the tie‑breaker.
     */
    @Override
    public Player start() {
        // reset
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        refreshOutput();

        Player[] players = configuration.getPlayers();
        int turn = 0;

        while (true) {
            currentPlayer = players[turn % players.length];

            // collect available moves
            Move[] available = getAvailableMoves(currentPlayer);
            // deadlock: tie‑breaker
            if (available.length == 0) {
                return breakTie();
            }

            // ask player for next move
            Move chosen = currentPlayer.nextMove(this, available);

            // perform move
            Piece moved = board[chosen.getSource().x()][chosen.getSource().y()];
            movePiece(chosen);
            numMoves++;
            updateScore(currentPlayer, moved, chosen);
            refreshOutput();

            // check for a win
            Player winner = getWinner(currentPlayer, moved, chosen);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations!");
                System.out.printf("Winner: %s%s%s\n",
                        winner.getColor(),
                        winner.getName(),
                        Color.DEFAULT);
                return winner;
            }
            turn++;
        }
    }

    /**
     * Tie‑breaker on deadlock: highest score wins; if tied, earliest in config order wins.
     */
    private Player breakTie() {
        Player[] players = configuration.getPlayers();
        Player best = players[0];
        for (int i = 1; i < players.length; i++) {
            Player p = players[i];
            if (p.getScore() > best.getScore()) {
                best = p;
            }
        }
        return best;
    }

    /**
     * Determine if last move produced a win.
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // protection period: no wins allowed
        if (numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }

        // capture‑all: opponent has no remaining pieces
        Player opponent = configuration.getPlayers()[0].equals(lastPlayer)
                ? configuration.getPlayers()[1]
                : configuration.getPlayers()[0];
        boolean oppHasPiece = false;
        for (int x = 0; x < configuration.getSize() && !oppHasPiece; x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = board[x][y];
                if (p != null && p.getPlayer().equals(opponent)) {
                    oppHasPiece = true;
                    break;
                }
            }
        }
        if (!oppHasPiece) {
            return lastPlayer;
        }

        Place src = lastMove.getSource();
        Place dst = lastMove.getDestination();
        Place center = configuration.getCentralPlace();

        // central‑place win: moved *onto* center with non‑Archer
        if (dst.equals(center) && !(lastPiece instanceof Archer)) {
            return lastPlayer;
        }

        // leaving center with Knight
        if (src.equals(center) && (lastPiece instanceof Knight)) {
            return lastPlayer;
        }

        return null;
    }

    /**
     * Score = Manhattan distance + 5 for captures (after protection expires).
     */
    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place s = move.getSource();
        Place d = move.getDestination();
        int gain = Math.abs(s.x() - d.x()) + Math.abs(s.y() - d.y());

        // capture bonus if there *was* an opponent piece before the move
        if (numMoves > configuration.getNumMovesProtection()) {
            // before moving, board[d] still held the captured piece
            Piece before = board[d.x()][d.y()];
            if (before != null && !before.getPlayer().equals(player)) {
                gain += 5;
            }
        }
        player.setScore(player.getScore() + gain);
    }

    /**
     * Move a piece on the board: clear source, set destination.
     */
    @Override
    public void movePiece(@NotNull Move move) {
        Place s = move.getSource();
        Place d = move.getDestination();
        Piece p = board[s.x()][s.y()];
        board[s.x()][s.y()] = null;
        board[d.x()][d.y()] = p;
    }

    /**
     * Gather all legal moves for a player, filtering out off‑board, onto own piece,
     * and forbidden captures/central during protection.
     */
    @Override
    public @NotNull Move[] getAvailableMoves(Player player) {
        List<Move> list = new ArrayList<>();
        int size = configuration.getSize();
        Place center = configuration.getCentralPlace();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = board[x][y];
                if (p != null && p.getPlayer().equals(player)) {
                    Place src = new Place(x, y);
                    for (Move m : p.getAvailableMoves(this, src)) {
                        Place d = m.getDestination();
                        // bounds
                        if (d.x() < 0 || d.x() >= size || d.y() < 0 || d.y() >= size) {
                            continue;
                        }
                        Piece dest = board[d.x()][d.y()];
                        // cannot land on own piece
                        if (dest != null && dest.getPlayer().equals(player)) {
                            continue;
                        }
                        // protection: no captures or center landings
                        if (numMoves < configuration.getNumMovesProtection()) {
                            if (dest != null || d.equals(center)) {
                                continue;
                            }
                        }
                        list.add(m);
                    }
                }
            }
        }
        return list.toArray(Move[]::new);
    }
}