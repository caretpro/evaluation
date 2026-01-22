
package assignment.protocol;

import assignment.piece.VacantRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A single concrete Game implementation that supports Archer and Knight pieces,
 * first‑N‑moves capture protection, central‐place win, capture‐all‐opponent win,
 * and a tie‑breaker when a player has no moves.
 */
public class GameImpl extends Game {

    private final List<Rule> globalRules = List.of(new VacantRule());
    private final Scanner scanner = new Scanner(System.in);

    public GameImpl(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Player start() {
        this.currentPlayer = getPlayers()[0];
        while (true) {
            refreshOutput();
            Move move = new ConsolePlayer(currentPlayer).nextMove(this);
            // validate
            for (Rule r : globalRules) {
                if (!r.validate(this, move)) {
                    System.out.println(r.getDescription());
                    continue;
                }
            }
            Piece piece = getPiece(move.getSource());
            // apply move
            movePiece(move);
            numMoves++;
            updateScore(currentPlayer, piece, move);
            // check winner
            Player w = getWinner(currentPlayer, piece, move);
            if (w != null) {
                refreshOutput();
                System.out.println("Winner: " + w.getName());
                return w;
            }
            // next player
            currentPlayer = nextPlayer();
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // first‐N moves protection blocking win
        if (numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }
        // central‐place win
        if (lastMove.getDestination().equals(configuration.getCentralPlace())) {
            return lastPlayer;
        }
        // capture-all-opponent win
        boolean opponentHasAny = false;
        for (Player p : configuration.getPlayers()) {
            if (!p.equals(lastPlayer)) {
                for (int x = 0; x < configuration.getSize(); x++) {
                    for (int y = 0; y < configuration.getSize(); y++) {
                        Piece pc = getPiece(x, y);
                        if (pc != null && pc.getPlayer().equals(p)) {
                            opponentHasAny = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!opponentHasAny) {
            return lastPlayer;
        }
        // tie‐breaker: if opponent cannot move, lastPlayer wins
        Player opponent = nextPlayer();
        if (getAvailableMoves(opponent).length == 0) {
            return lastPlayer;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        // no scoring before protection
        if (numMoves <= configuration.getNumMovesProtection()) {
            return;
        }
        // capture adds piece’s value
        Piece destPiece = getPiece(move.getDestination());
        if (destPiece != null && !destPiece.getPlayer().equals(player)) {
            player.addScore(destPiece.getValue());
        }
    }

    @Override
    public void movePiece(@NotNull Move move) {
        Piece p = getPiece(move.getSource());
        board[move.getSource().x()][move.getSource().y()] = null;
        board[move.getDestination().x()][move.getDestination().y()] = p;
    }

    @Override
    public @NotNull Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = getPiece(x, y);
                if (p != null && p.getPlayer().equals(player)) {
                    // delegate by piece type
                    moves.addAll(switch (p.getLabel()) {
                        case 'A' -> generateArcherMoves(player, x, y);
                        case 'K' -> generateKnightMoves(player, x, y);
                        default -> List.<Move>of();
                    });
                }
            }
        }
        // filter out moves that violate “first N moves protection” capturing
        if (numMoves < configuration.getNumMovesProtection()) {
            moves = moves.stream()
                         .filter(m -> getPiece(m.getDestination()) == null)
                         .collect(Collectors.toList());
        }
        return moves.toArray(new Move[0]);
    }

    private List<Move> generateArcherMoves(Player player, int sx, int sy) {
        List<Move> out = new ArrayList<>();
        for (int dx = 0; dx < configuration.getSize(); dx++) {
            for (int dy = 0; dy < configuration.getSize(); dy++) {
                if (dx == sx && dy == sy) continue;
                // same row or same column, line‐of‐sight (no blocking)
                if (dx == sx || dy == sy) {
                    // check line clear
                    int stepX = Integer.signum(dx - sx);
                    int stepY = Integer.signum(dy - sy);
                    boolean blocked = false;
                    int cx = sx + stepX, cy = sy + stepY;
                    while (cx != dx || cy != dy) {
                        if (getPiece(cx, cy) != null) {
                            blocked = true; break;
                        }
                        cx += stepX; cy += stepY;
                    }
                    if (!blocked) {
                        out.add(new Move(sx, sy, dx, dy));
                    }
                }
            }
        }
        // cannot capture own piece
        return out.stream()
                  .filter(m -> {
                      Piece dst = getPiece(m.getDestination());
                      return dst == null || !dst.getPlayer().equals(player);
                  })
                  .collect(Collectors.toList());
    }

    private List<Move> generateKnightMoves(Player player, int sx, int sy) {
        int[][] deltas = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
        List<Move> out = new ArrayList<>();
        for (var d : deltas) {
            int nx = sx + d[0], ny = sy + d[1];
            if (0 <= nx && nx < configuration.getSize() && 0 <= ny && ny < configuration.getSize()) {
                Piece dst = getPiece(nx, ny);
                if (dst == null || !dst.getPlayer().equals(player)) {
                    out.add(new Move(sx, sy, nx, ny));
                }
            }
        }
        return out;
    }

    private Player nextPlayer() {
        Player[] ps = configuration.getPlayers();
        return ps[(List.of(ps).indexOf(currentPlayer) + 1) % ps.length];
    }

    /**
     * A minimal console‐based Player that keeps prompting until input is valid.
     */
    private class ConsolePlayer extends Player {
        ConsolePlayer(Player p) { super(p); }
        @Override
        public Move nextMove(Game game) {
            while (true) {
                System.out.printf("Player %s, enter move: ", getName());
                String line = scanner.nextLine().trim();
                try {
                    String[] parts = line.split("->");
                    Place src = Place.fromString(parts[0]);
                    Place dst = Place.fromString(parts[1]);
                    Move m = new Move(src, dst);
                    // move must be available
                    for (Move av : game.getAvailableMoves(this)) {
                        if (av.equals(m)) return m;
                    }
                } catch (Exception e) {
                    // simply retry
                }
                System.out.println("Invalid input or move. Try again.");
            }
        }
    }
}