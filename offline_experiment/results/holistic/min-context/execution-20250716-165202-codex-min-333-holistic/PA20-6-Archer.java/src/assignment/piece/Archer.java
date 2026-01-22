
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.protocol.rule.ArcherMoveRule;
import assignment.protocol.rule.FirstNMovesProtectionRule;
import assignment.protocol.rule.NilMoveRule;
import assignment.protocol.rule.OutOfBoundaryRule;
import assignment.protocol.rule.OccupiedRule;
import assignment.protocol.rule.VacantRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Archer piece that moves similar to cannon in Chinese chess.
 * Rules of move of Archer can be found in Wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
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
     * Returns an array of moves that are valid given the current place of the piece.
     * Implements the Xiangqi "cannon" (Archer) rules:
     * <ul>
     *   <li>Moves any number of empty squares orthogonally (no screen) for non-captures.</li>
     *   <li>To capture, must jump exactly one intervening piece ("screen") and land on an opponent piece.</li>
     * </ul>
     *
     * @param game   the game object
     * @param source the current place of this Archer
     * @return an array of available moves
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();

        int maxR = game.getBoard().getNumRows();
        int maxC = game.getBoard().getNumCols();

        // four orthogonal directions: up, down, left, right
        int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        for (int[] d : dirs) {
            int dx = d[0], dy = d[1];
            int r = source.row() + dx;
            int c = source.col() + dy;

            // Phase 1: non-capture moves – slide through empty cells until first occupied
            while (r >= 0 && r < maxR && c >= 0 && c < maxC
                   && game.getBoard().getPieceAt(r, c) == null) {
                Move m = new Move(source, Place.of(r, c));
                if (validateMove(game, m)) {
                    moves.add(m);
                }
                r += dx;
                c += dy;
            }

            // Phase 2: capture – if there's exactly one "screen", jump it and capture the next piece
            if (r >= 0 && r < maxR && c >= 0 && c < maxC
                && game.getBoard().getPieceAt(r, c) != null) {
                // skip the screen piece
                r += dx;
                c += dy;

                // find the first piece beyond the screen
                while (r >= 0 && r < maxR && c >= 0 && c < maxC) {
                    if (game.getBoard().getPieceAt(r, c) != null) {
                        Move m = new Move(source, Place.of(r, c));
                        if (validateMove(game, m)) {
                            moves.add(m);
                        }
                        break;
                    }
                    r += dx;
                    c += dy;
                }
            }
        }

        return moves.toArray(Move[]::new);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new OutOfBoundaryRule[] {
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