
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;
import assignment.protocol.Player;
import assignment.rule.ArcherMoveRule;
import assignment.rule.FirstNMovesProtectionRule;
import assignment.rule.NilMoveRule;
import assignment.rule.OccupiedRule;
import assignment.rule.OutOfBoundaryRule;
import assignment.rule.VacantRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Archer piece that moves similar to cannon in Chinese chess.
 * Rules of move of Archer can be found in Wikipedia:
 * https://en.wikipedia.org/wiki/Xiangqi#Cannon
 *
 * @see <a href="https://en.wikipedia.org/wiki/Xiangqi#Cannon">Wikipedia: Cannon</a>
 */
public class Archer extends Piece {

    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    /**
     * Returns all valid archer (cannon) moves from {@code source} on the given board.
     * A cannon moves any number of empty squares orthogonally if not capturing. To capture,
     * it must jump exactly one intervening piece (the “screen”) and land on an enemy piece.
     *
     * @param game   the game object
     * @param source the current position of this cannon
     * @return an array of all legal Moves from source
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();

        // Four orthogonal directions: right, left, up, down
        int[][] deltas = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        for (int[] d : deltas) {
            int dx = d[0], dy = d[1];

            // Phase 1: non‑capturing moves (like a rook) until blocked
            for (int step = 1; ; step++) {
                int nx = source.x() + dx * step;
                int ny = source.y() + dy * step;
                // manual boundary check
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                    break;
                }
                Move m = new Move(source, nx, ny);
                if (!validateMove(game, m)) {
                    break;
                }
                moves.add(m);
            }

            // Phase 2: capturing moves—must jump exactly one screen piece, then capture next piece
            boolean screenFound = false;
            for (int step = 1; ; step++) {
                int nx = source.x() + dx * step;
                int ny = source.y() + dy * step;
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                    break;
                }
                Place p = new Place(nx, ny);
                if (!screenFound) {
                    if (game.getPiece(p) != null) {
                        screenFound = true;
                    }
                } else {
                    if (game.getPiece(p) != null) {
                        Move m = new Move(source, p);
                        if (validateMove(game, m)) {
                            moves.add(m);
                        }
                        break; // only the first piece beyond the screen can be captured
                    }
                }
            }
        }

        return moves.toArray(new Move[0]);
    }

    /**
     * Validate a move against the standard rules plus the Archer‑specific rule.
     */
    private boolean validateMove(Game game, Move move) {
        // Correctly create an array of Rule instances (instead of malformed var syntax)
        var rules = new assignment.rule.Rule[] {
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new VacantRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
            new ArcherMoveRule()
        };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}