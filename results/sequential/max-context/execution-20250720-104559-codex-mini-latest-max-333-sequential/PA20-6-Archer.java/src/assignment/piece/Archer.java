
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;

/**
 * Archer piece that moves similar to cannon in chinese chess.
 * Rules of move of Archer can be found in wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
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
     * Archer moves orthogonally: slides through empty squares for non‐captures,
     * and to capture it must jump exactly one intervening piece ("screen") before landing on an enemy piece.
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        int size = game.getConfiguration().getSize();
        ArrayList<Move> moves = new ArrayList<>();

        // four orthogonal directions: {dx, dy}
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : dirs) {
            int dx = dir[0], dy = dir[1];
            int x = source.x() + dx, y = source.y() + dy;

            // 1) Non‐capture slides: move through empty squares
            while (x >= 0 && x < size && y >= 0 && y < size && game.getPiece(x, y) == null) {
                Move slide = new Move(source, new Place(x, y));
                if (validateMove(game, slide)) {
                    moves.add(slide);
                }
                x += dx;
                y += dy;
            }

            // 2) Now x,y is either off‐board or at the first "screen" piece
            if (x < 0 || x >= size || y < 0 || y >= size) {
                continue;   // no screen → no capture in this direction
            }

            // 3) Jump over that one screen, then attempt exactly one capture
            x += dx;
            y += dy;
            if (x >= 0 && x < size && y >= 0 && y < size) {
                Move capture = new Move(source, new Place(x, y));
                if (validateMove(game, capture)) {
                    moves.add(capture);
                }
            }
        }

        return moves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[]{
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