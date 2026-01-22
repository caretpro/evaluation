
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;
import assignment.protocol.Player;
import assignment.rule.FirstNMovesProtectionRule;
import assignment.rule.KnightBlockRule;
import assignment.rule.KnightMoveRule;
import assignment.rule.NilMoveRule;
import assignment.rule.OccupiedRule;
import assignment.rule.OutOfBoundaryRule;
import assignment.rule.VacantRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Knight piece that moves similar to knight in chess.
 * Rules of move of Knight can be found in wikipedia (https://en.wikipedia.org/wiki/Knight_(chess)).
 *
 * @see <a href="https://en.wikipedia.org/wiki/Knight_(chess)">Wikipedia</a>
 */
public class Knight extends Piece {
    public Knight(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'K';
    }

    /**
     * Returns an array of moves that are valid given the current place of the piece.
     * Given the {@link Game} object and the {@link Place} that current knight piece locates, this method should
     * return ALL VALID {@link Move}s according to the current {@link Place} of this knight piece.
     * All the returned {@link Move} should have source equal to the source parameter.
     *
     * @param game   the game object
     * @param source the current place of the piece
     * @return an array of available moves
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        // All eight L‑shaped offsets a knight can make:
        int[][] deltas = {
            {+2, +1}, {+2, -1}, {-2, +1}, {-2, -1},
            {+1, +2}, {+1, -2}, {-1, +2}, {-1, -2}
        };

        List<Move> validMoves = new ArrayList<>();
        for (int[] d : deltas) {
            Move candidate = new Move(source, source.x() + d[0], source.y() + d[1]);
            if (validateMove(game, candidate)) {
                validMoves.add(candidate);
            }
        }
        return validMoves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new OutOfBoundaryRule();
        var validators = new java.util.function.Predicate<assignment.rule.Rule>[] {
            new OccupiedRule()::validate,
            new VacantRule()::validate,
            new NilMoveRule()::validate,
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection())::validate,
            new KnightMoveRule()::validate,
            new KnightBlockRule()::validate
        };

        if (!rules.validate(game, move)) {
            return false;
        }
        for (var validator : validators) {
            if (!validator.test(game, move)) {
                return false;
            }
        }
        return true;
    }
}