
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.protocol.Piece;
import assignment.validation.FirstNMovesProtectionRule;
import assignment.validation.NilMoveRule;
import assignment.validation.OccupiedRule;
import assignment.validation.OutOfBoundaryRule;
import assignment.validation.VacantRule;
import assignment.validation.archer.ArcherMoveRule;

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
     * Returns all valid moves for this Archer from the given source.
     * Follows Xiangqi cannon rules:
     * 1) To move without capture, there must be no pieces in between source and target.
     * 2) To capture, there must be exactly one piece (the screen) between source and target.
     *
     * @param game   the game object
     * @param source the current location of the Archer
     * @return an array of all valid moves
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();

        int rows = game.getConfiguration().numRows();
        int cols = game.getConfiguration().numCols();
        int protection = game.getConfiguration().numMovesProtection();
        int srcRow = source.getRow();
        int srcCol = source.getCol();

        // Four orthogonal directions: down, up, right, left
        int[][] deltas = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
        for (int[] d : deltas) {
            int dr = d[0], dc = d[1];
            int r = srcRow + dr;
            int c = srcCol + dc;
            boolean screenFound = false;

            while (r >= 0 && r < rows && c >= 0 && c < cols) {
                Place dest = new Place(r, c);
                Move m = new Move(source, dest);

                if (!screenFound) {
                    // Before screen: only non-capturing moves if path is clear
                    if (game.getPiece(dest) == null) {
                        if (validateMove(game, m, protection)) {
                            moves.add(m);
                        }
                        r += dr;
                        c += dc;
                        continue;
                    } else {
                        // First occupied square is the screen
                        screenFound = true;
                    }
                } else {
                    // After screen: only the first capture beyond the screen
                    if (game.getPiece(dest) != null) {
                        if (validateMove(game, m, protection)) {
                            moves.add(m);
                        }
                        break;
                    }
                }
                r += dr;
                c += dc;
            }
        }

        return moves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move, int protection) {
        Rule[] rules = {
            new OutOfBoundaryRule(),
            new NilMoveRule(),
            new VacantRule(),
            new OccupiedRule(),
            new FirstNMovesProtectionRule(protection),
            new ArcherMoveRule()
        };
        for (Rule rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}