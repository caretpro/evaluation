
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.protocol.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Archer piece that moves similar to cannon in Chinese chess.
 * Rules of move of Archer can be found in Wikipedia:
 * {@link <a href="https://en.wikipedia.org/wiki/Xiangqi#Cannon">https://en.wikipedia.org/wiki/Xiangqi#Cannon</a>}.
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
     * Returns all valid orthogonal moves for the cannon (archer), both non-capturing slides
     * (over empty squares only) and capturing jumps (over exactly one screen piece).
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        int size = game.getConfiguration().getSize();
        List<Move> moves = new ArrayList<>();

        // Four orthogonal directions: right, left, up, down
        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (var dir : dirs) {
            int dx = dir[0], dy = dir[1];
            int x = source.x(), y = source.y();

            // Phase I: sliding over empty squares
            while (true) {
                x += dx;
                y += dy;
                if (x < 0 || x >= size || y < 0 || y >= size) {
                    break;  // out of board
                }
                Place dest = new Place(x, y);
                if (game.getPiece(dest) != null) {
                    break;  // screen found; stop slide phase
                }
                Move slide = new Move(source, dest);
                if (validateMove(game, slide)) {
                    moves.add(slide);
                }
            }

            // Phase II: capturing by jumping exactly one screen piece
            // Find the screen piece (adjacent piece that stopped the slide)
            int sx = x, sy = y;
            if (sx < 0 || sx >= size || sy < 0 || sy >= size) {
                continue;  // no screen on board
            }
            if (game.getPiece(new Place(sx, sy)) == null) {
                continue;  // actually no screen
            }

            // From the screen, look for the first piece beyond it
            x = sx;
            y = sy;
            while (true) {
                x += dx;
                y += dy;
                if (x < 0 || x >= size || y < 0 || y >= size) {
                    break;  // out of board
                }
                Place dest = new Place(x, y);
                if (game.getPiece(dest) == null) {
                    continue;  // keep skipping empties
                }
                // Found first piece beyond screen; attempt capture
                Move capture = new Move(source, dest);
                if (validateMove(game, capture)) {
                    moves.add(capture);
                }
                break;  // whether legal or not, stop in this direction
            }
        }

        return moves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[] {
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