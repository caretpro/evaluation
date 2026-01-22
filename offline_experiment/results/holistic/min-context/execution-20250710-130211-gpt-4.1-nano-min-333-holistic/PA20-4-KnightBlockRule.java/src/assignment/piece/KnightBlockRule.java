
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The blocking rule applying on Knights. The rule is similar to the blocking rule for horse in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Horse'>Wikipedia</a>
 */
public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place source = move.getSource();
        Place target = move.getTarget();

        int deltaX = target.getX() - source.getX();
        int deltaY = target.getY() - source.getY();

        // Check for move in an "L" shape: 2 in one direction and 1 in perpendicular
        if (!((Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) || (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 2))) {
            return false; // Not a valid knight move pattern
        }

        int sourceX = source.getX();
        int sourceY = source.getY();

        int blockX = sourceX;
        int blockY = sourceY;

        // For moves where deltaX == 2 or -2, the block is at one step in X direction
        if (Math.abs(deltaX) == 2) {
            blockX = sourceX + deltaX / 2;
            blockY = sourceY;
        }
        // For moves where deltaY == 2 or -2, the block is at one step in Y direction
        else if (Math.abs(deltaY) == 2) {
            blockX = sourceX;
            blockY = sourceY + deltaY / 2;
        }

        Place blockPlace = new Place(blockX, blockY);

        // Check if the blocking position is occupied
        if (game.getPiece(blockPlace) != null) {
            return false; // Move is blocked
        }

        return true; // Move is valid
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}