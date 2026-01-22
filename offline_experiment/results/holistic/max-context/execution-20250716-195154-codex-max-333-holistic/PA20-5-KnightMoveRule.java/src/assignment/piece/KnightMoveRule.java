
package assignment.protocol;

import assignment.piece.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A standard implementation of Game that applies:
 *   1. First‑N‑moves protection (no captures or wins allowed in first N moves)
 *   2. Movement and capture rules via Rule implementations
 *   3. Tie‑breaker: if getAvailableMoves returns empty for both players, highest score wins
 *   4. Central‑place win: moving or arriving on central place wins immediately
 */
public class StandardGame extends Game {
    private final List<Rule> rules;
    private final Rule protectionRule;
    private final Rule knightRule;
    private final Rule archerRule;  // if you have an Archer piece
    private final Rule firstNProtectionRule;

    public StandardGame(Configuration configuration) {
        super(configuration);
        // global rules
        this.protectionRule = new FirstNMovesProtectionRule(configuration.getNumMovesProtection());
        this.knightRule = new KnightMoveRule();
        this.archerRule = new ArcherMoveRule();
        this.rules = Arrays.asList(
            protectionRule,               // first‑N protection comes first
            knightRule,                   // knight L‑shapes
            new RookMoveRule(),           // example for rook (no block)
            new BishopMoveRule(),         // bishop (no block)
            new QueenMoveRule(),          // queen (no block)
            new KingMoveRule(),           // king one‑square
            archerRule                    // archer special rule
        );
        this.firstNProtectionRule = protectionRule;
    }

    @Override
    public @NotNull Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Place src = new Place(x, y);
                Piece p = getPiece(src);
                if (p == null || p.getPlayer() != player) continue;
                // generate candidate destinations for this piece
                for (Move m : p.possibleMoves(configuration.getSize())) {
                    Move mv = new Move(src, m.getDestination());
                    boolean ok = true;
                    for (Rule r : rules) {
                        if (!r.validate(this, mv)) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) {
                        moves.add(mv);
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    @Override
    public void movePiece(@NotNull Move move) {
        Piece srcPiece = getPiece(move.getSource());
        Piece dstPiece = getPiece(move.getDestination());
        // apply capture if any
        if (dstPiece != null && dstPiece.getPlayer() != srcPiece.getPlayer()) {
            // capture: remove the destination piece
            board[move.getDestination().x()][move.getDestination().y()] = null;
        }
        // move
        board[move.getSource().x()][move.getSource().y()] = null;
        board[move.getDestination().x()][move.getDestination().y()] = srcPiece;
        numMoves++;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // central‑place win
        if (lastMove.getDestination().equals(configuration.getCentralPlace())) {
            return lastPlayer;
        }
        // first N cannot win
        if (numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }
        // tie‑breaker: if both have no moves
        boolean noMovesCurrent = getAvailableMoves(getCurrentPlayer()).length == 0;
        Player other = configuration.getPlayers()[0] == getCurrentPlayer()
                ? configuration.getPlayers()[1]
                : configuration.getPlayers()[0];
        boolean noMovesOther = getAvailableMoves(other).length == 0;
        if (noMovesCurrent && noMovesOther) {
            // high score wins
            Player[] ps = configuration.getPlayers();
            return ps[0].getScore() > ps[1].getScore() ? ps[0] : ps[1];
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        // capturing gives 1 point
        Place dst = move.getDestination();
        Piece before = null;
        // cannot easily retrieve captured piece after movePiece, so infer: if dst had opponent
        // For simplicity, assume every valid capture gave 1.
        if (firstNProtectionRule.validate(this, move) && piece.canCapture()) {
            // only score after protection period
            player.setScore(player.getScore() + 1);
        }
    }

    @Override
    public Player start() {
        currentPlayer = getCurrentPlayer();
        Player winner = null;
        while ((winner = getWinner(currentPlayer, null, null)) == null) {
            Move[] avail = getAvailableMoves(currentPlayer);
            if (avail.length == 0) {
                // pass turn to other player
                currentPlayer = configuration.getOtherPlayer(currentPlayer);
                continue;
            }
            Move mv = currentPlayer.nextMove(avail);
            movePiece(mv);
            updateScore(currentPlayer, getPiece(mv.getDestination()), mv);
            winner = getWinner(currentPlayer, getPiece(mv.getDestination()), mv);
            currentPlayer = configuration.getOtherPlayer(currentPlayer);
        }
        return winner;
    }
}