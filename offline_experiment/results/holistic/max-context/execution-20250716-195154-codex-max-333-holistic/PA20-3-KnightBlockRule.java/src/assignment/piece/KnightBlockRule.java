
package assignment.protocol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A full implementation of the Game rules:
 *  - Protect first N moves (no capture, no win).
 *  - Score update on capture and central-place arrival.
 *  - Tie-breaking: if central-place occupied by same round, winner is the one who just moved.
 *  - Turn-taking and move validation via configured Rules.
 */
public class ConcreteGame extends Game {

    private final List<Rule> rules;

    public ConcreteGame(Configuration configuration) {
        super(configuration);
        // Define the rule chain in order
        this.rules = List.of(
            new FirstNMovesProtectionRule(configuration.getNumMovesProtection()),
            new CentralPlaceRule(),
            new CaptureRule(),
            // Piece-specific rules come last
            new KnightBlockRule(),
            new GeneralMovementRule()
        );
    }

    @Override
    public Player start() {
        Player[] players = configuration.getPlayers();
        currentPlayer = players[0];
        while (true) {
            refreshOutput();
            Move move = currentPlayer.nextMove(this.clone()); // assume players implement nextMove
            movePiece(move);
            Player winner = getWinner(currentPlayer, getPiece(move.getDestination()), move);
            if (winner != null) {
                return winner;
            }
            // next turn
            currentPlayer = players[(Arrays.asList(players).indexOf(currentPlayer) + 1) % players.length];
        }
    }

    @Override
    public void movePiece(@NotNull Move move) {
        // Validate via rules
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                throw new IllegalArgumentException(rule.getDescription());
            }
        }
        // Perform the move
        Place src = move.getSource(), dst = move.getDestination();
        Piece moving = getPiece(src);
        Piece captured = getPiece(dst);
        board[dst.x()][dst.y()] = moving;
        board[src.x()][src.y()] = null;
        numMoves++;
        updateScore(currentPlayer, moving, move);
    }

    @Override
    public @NotNull Move[] getAvailableMoves(Player player) {
        List<Move> avail = new ArrayList<>();
        // scan board for player's pieces
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Place from = new Place(x, y);
                Piece p = getPiece(from);
                if (p != null && p.getPlayer().equals(player)) {
                    for (Place to : p.getPossibleDestinations(from, configuration.getSize())) {
                        Move move = new Move(from, to);
                        boolean ok = true;
                        for (Rule rule : rules) {
                            if (!rule.validate(this, move)) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok) {
                            avail.add(move);
                        }
                    }
                }
            }
        }
        return avail.toArray(new Move[0]);
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // First-N Protection: no win before threshold moves
        if (numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }
        Place center = configuration.getCentralPlace();
        // central-place arrival
        if (lastMove.getDestination().equals(center)) {
            return lastPlayer;
        }
        // capture win
        // note: CaptureRule has already incremented score if applicable
        if (lastPiece != null && getPiece(lastMove.getDestination()) != lastPiece) {
            // capturing someone else => lastPlayer wins
            return lastPlayer;
        }
        // tie-breaker: if both players arrive same round on center, priority to mover
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        // scoring: central-place gets X points; capture gets Y points
        Place dst = move.getDestination();
        if (dst.equals(configuration.getCentralPlace())) {
            player.addScore(configuration.getCentralScore());
        }
        Piece victim = getPiece(dst);
        if (victim != null && !victim.equals(piece)) {
            player.addScore(configuration.getCaptureScore());
        }
    }
}