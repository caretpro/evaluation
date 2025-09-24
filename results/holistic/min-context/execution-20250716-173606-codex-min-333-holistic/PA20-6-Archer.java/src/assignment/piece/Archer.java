
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.protocol.rule.ArcherMoveRule;
import assignment.protocol.rule.FirstNMovesProtectionRule;
import assignment.protocol.rule.NilMoveRule;
import assignment.protocol.rule.OccupiedRule;
import assignment.protocol.rule.OutOfBoundaryRule;
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
     * Returns all valid Moves for this Archer from the given source Place.
     * Slides orthogonally like a Rook but captures by jumping exactly one piece
     * (the “screen”)—enforced by ArcherMoveRule.
     *
     * @param game   the game object
     * @param source the current place of this Archer
     * @return all valid moves from source
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int boardSize = game.getConfiguration().getDimension();

        // Four orthogonal directions: up/down/left/right
        int[][] deltas = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
        for (var d : deltas) {
            for (int step = 1; step < boardSize; step++) {
                Place dest = new Place(source.row(), source.col(), source.depth());
                dest = new Place(source.row() + d[0] * step,
                                 source.col() + d[1] * step,
                                 source.depth());
                Move move = new Move(source, dest);
                if (validateMove(game, move)) {
                    moves.add(move);
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
            new ArcherMoveRule(),
        };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}