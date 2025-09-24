
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;

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
        
        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        int deltaX = destX - srcX;
        int deltaY = destY - srcY;

        // Possible Knight moves and their corresponding "leg" positions
        // Each move is represented as (dx, dy) and the blocking position as (blockX, blockY)
        int[][] moves = {
            {2, 1, 1, 0},   // move right 2, up 1; block at (srcX + 1, srcY)
            {2, -1, 1, 0},  // move right 2, down 1; block at (srcX + 1, srcY)
            {-2, 1, -1, 0}, // move left 2, up 1; block at (srcX - 1, srcY)
            {-2, -1, -1, 0},// move left 2, down 1; block at (srcX - 1, srcY)
            {1, 2, 0, 1},   // move right 1, up 2; block at (srcX, srcY + 1)
            {1, -2, 0, -1}, // move right 1, down 2; block at (srcX, srcY - 1)
            {-1, 2, 0, 1},  // move left 1, up 2; block at (srcX, srcY + 1)
            {-1, -2, 0, -1} // move left 1, down 2; block at (srcX, srcY - 1)
        };

        for (int[] moveOption : moves) {
            int dx = moveOption[0];
            int dy = moveOption[1];
            int blockX = moveOption[2] + srcX;
            int blockY = moveOption[3] + srcY;

            if (deltaX == dx && deltaY == dy) {
                // Check if the "leg" position is occupied
                Piece blockingPiece = game.getPiece(blockX, blockY);
                if (blockingPiece != null) {
                    return false; // move is blocked
                }
            }
        }
        return true; // move is not blocked
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}