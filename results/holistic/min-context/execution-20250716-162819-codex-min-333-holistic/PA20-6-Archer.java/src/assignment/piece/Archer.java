
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.rule.ArcherMoveRule;
import assignment.rule.FirstNMovesProtectionRule;
import assignment.rule.NilMoveRule;
import assignment.rule.OccupiedRule;
import assignment.rule.OutOfBoundaryRule;
import assignment.rule.Rule;
import assignment.rule.VacantRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Archer piece that moves similar to cannon in Chinese chess.
 * Rules of move of Archer can be found in Wikipedia:
 * https://en.wikipedia.org/wiki/Xiangqi#Cannon
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
     * <p>
     * Follows the Xiangqi “cannon” rule: must jump exactly one screen (any piece)
     * before landing on an empty square (move) or an occupied enemy square (capture).
     * </p>
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> candidates = new ArrayList<>();

        int[] dr = { -1, 1, 0, 0 }; // up, down, left, right
        int[] dc = { 0, 0, -1, 1 };

        // pull dimensions from the protocol Configuration
        int rows = game.getConfiguration().getRowCount();
        int cols = game.getConfiguration().getColumnCount();

        for (int i = 0; i < 4; i++) {
            int r = source.getRow();
            int c = source.getCol();
            boolean screenFound = false;

            while (true) {
                r += dr[i];
                c += dc[i];
                // stop if off‑board
                if (r < 0 || r >= rows || c < 0 || c >= cols) {
                    break;
                }
                // before jumping screen: look for the first piece
                if (!screenFound) {
                    if (game.getPieceAt(r, c) != null) {
                        screenFound = true;
                    }
                    continue;
                }
                // after screen: can land (empty or capture), then stop
                candidates.add(new Move(source, new Place(r, c)));
                break;
            }
        }

        // validate against all standard rules
        List<Move> valid = new ArrayList<>();
        for (Move mv : candidates) {
            if (validateMove(game, mv)) {
                valid.add(mv);
            }
        }
        return valid.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        Rule[] rules = new Rule[] {
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new VacantRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
            new ArcherMoveRule(),
        };
        for (Rule rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}