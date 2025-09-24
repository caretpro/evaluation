
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.piece.Piece;

/**
 * The blocking rule applying on Knights. The rule is similar to the blocking rule for horse in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Horse'>Wikipedia</a>
 */
public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true; // Not a Knight, rule does not apply
        }

        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        int deltaX = destX - srcX;
        int deltaY = destY - srcY;

        // All possible Knight moves with their corresponding "leg" positions
        int[][] moveOffsets = {
                {2, 1, 1, 0},   // move right 2, up 1; leg at (srcX + 1, srcY)
                {2, -1, 1, 0},  // move right 2, down 1; leg at (srcX + 1, srcY)
                {-2, 1, -1, 0}, // move left 2, up 1; leg at (srcX - 1, srcY)
                {-2, -1, -1, 0},// move left 2, down 1; leg at (srcX - 1, srcY)
                {1, 2, 0, 1},   // move right 1, up 2; leg at (srcX, srcY + 1)
                {1, -2, 0, -1},  // move right 1, down 2; leg at (srcX, srcY - 1)
                {-1, 2, 0, 1},   // move left 1, up 2; leg at (srcX, srcY + 1)
                {-1, -2, 0, -1}  // move left 1, down 2; leg at (srcX, srcY - 1)
        };

        for (int[] offset : moveOffsets) {
            int moveDeltaX = offset[0];
            int moveDeltaY = offset[1];
            int legXOffset = offset[2];
            int legYOffset = offset[3];

            if (deltaX == moveDeltaX && deltaY == moveDeltaY) {
                int legX = srcX + legXOffset;
                int legY = srcY + legYOffset;

                // Check if the leg position is within bounds
                if (legX >= 0 && legX < game.getConfiguration().getSize() &&
                        legY >= 0 && legY < game.getConfiguration().getSize()) {
                    Piece legPiece = game.getPiece(legX, legY);
                    if (legPiece != null) {
                        // Leg is blocked
                        return false;
                    }
                }
                // Leg is free, move is valid
                return true;
            }
        }

        // If move does not match any of the standard Knight moves, consider invalid or not blocked
        // But since the move is not a valid Knight move, we can assume it's invalid, so return false
        return false;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}