
package assignment.protocol;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default implementation of the Game engine.
 * Implements:
 *   1. First‑N‑moves protection (no captures or wins before protected count)
 *   2. Tie‑breaking by total moves if deadlock
 *   3. Central‑place win or piece‑capture win
 *   4. Applying all rules in the configuration during move validation
 */
public class DefaultGame extends Game {

    public DefaultGame(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Player start() {
        currentPlayer = configuration.getPlayers()[0];
        while (true) {
            refreshOutput();
            Move[] moves = getAvailableMoves(currentPlayer);
            if (moves.length == 0) {
                // deadlock: choose the player who has made the most moves
                Player winner = getTieBreakerWinner();
                return winner;
            }
            // let player choose a move (mock or real).
            Move chosen = currentPlayer.makeMove(moves.clone());
            movePiece(chosen);
            numMoves++;
            Piece movedPiece = getPiece(chosen.getDestination());
            Player winner = getWinner(currentPlayer, movedPiece, chosen);
            if (winner != null) {
                return winner;
            }
            // advance turn
            currentPlayer = nextPlayer(currentPlayer);
        }
    }

    private Player nextPlayer(Player p) {
        Player[] players = configuration.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(p)) {
                return players[(i + 1) % players.length];
            }
        }
        return players[0];
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // 1) cannot win in protected moves
        if (numMoves < configuration.getNumMovesProtection()) {
            return null;
        }
        // 2) winning by capturing all opponents' pieces
        Set<Player> opponents = Arrays.stream(configuration.getPlayers())
                .filter(p -> !p.equals(lastPlayer)).collect(Collectors.toSet());
        // remove from opponents any whose pieces remain on board
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = board[x][y];
                if (p != null && opponents.contains(p.getPlayer())) {
                    opponents.remove(p.getPlayer());
                }
            }
        }
        if (opponents.isEmpty()) {
            return lastPlayer;
        }
        // 3) winning by reaching central place
        if (lastMove.getDestination().equals(configuration.getCentralPlace())) {
            return lastPlayer;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        // score: 1 point per move, +5 per capture, +10 on winning by central place or capture-all
        int delta = 1;
        if (moveCaptures(piece, move)) {
            delta += 5;
        }
        if (getWinner(player, piece, move) != null) {
            delta += 10;
        }
        player.setScore(player.getScore() + delta);
    }

    private boolean moveCaptures(Piece piece, Move move) {
        Piece dest = getPiece(move.getDestination());
        return dest != null && !dest.getPlayer().equals(piece.getPlayer());
    }

    @Override
    public void movePiece(@NotNull Move move) {
        Place src = move.getSource();
        Place dst = move.getDestination();
        Piece piece = getPiece(src);
        board[dst.x()][dst.y()] = piece;
        board[src.x()][src.y()] = null;
    }

    @Override
    public @NotNull Move[] getAvailableMoves(Player player) {
        List<Move> avail = new ArrayList<>();
        // iterate all board positions
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Place from = new Place(x, y);
                Piece p = getPiece(from);
                if (p == null || !p.getPlayer().equals(player)) continue;
                // ask piece for its raw moves
                for (Place to : p.getAvailableMoves(this, from)) {
                    Move m = new Move(from, to);
                    // all configured rules must pass
                    boolean ok = configuration.getRules().stream()
                            .allMatch(r -> r.validate(this, m));
                    if (ok) {
                        avail.add(m);
                    }
                }
            }
        }
        return avail.toArray(Move[]::new);
    }

    private Player getTieBreakerWinner() {
        // player who made the most moves (numMoves per player tracked via scores)
        return Arrays.stream(configuration.getPlayers())
                .max(Comparator.comparingInt(Player::getScore))
                .orElse(configuration.getPlayers()[0]);
    }
}