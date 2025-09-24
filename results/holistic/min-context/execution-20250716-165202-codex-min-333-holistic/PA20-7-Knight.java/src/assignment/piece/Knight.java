
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Knight piece that moves similar to knight in chess.
 * Rules of move of Knight can be found in wikipedia (https://en.wikipedia.org/wiki/Knight_(chess)).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Knight_(chess)'>Wikipedia</a>
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
        List<Move> candidates = new ArrayList<>();

        // All 8 knight offsets: (±2, ±1) and (±1, ±2)
        int[][] offsets = {
            {+2, +1}, {+2, -1}, {-2, +1}, {-2, -1},
            {+1, +2}, {+1, -2}, {-1, +2}, {-1, -2}
        };

        int srcRow = source.row;
        int srcCol = source.col;
        for (int[] o : offsets) {
            int destRow = srcRow + o[0];
            int destCol = srcCol + o[1];
            Place dest = new Place(destRow, destCol);
            Move move = new Move(source, dest, this);
            if (validateMove(game, move)) {
                candidates.add(move);
            }
        }
        return candidates.toArray(Move[]::new);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[] {
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new VacantRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
            new KnightMoveRule(),
            new KnightBlockRule(),
        };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}