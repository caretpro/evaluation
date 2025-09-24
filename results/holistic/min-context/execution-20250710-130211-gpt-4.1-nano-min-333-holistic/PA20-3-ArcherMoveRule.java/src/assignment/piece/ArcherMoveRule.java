
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true; // Not an Archer piece, rule does not apply
        }

        Place source = move.getSource();
        Place target = move.getDestination();

        int sourceX = source.getX();
        int sourceY = source.getY();
        int targetX = target.getX();
        int targetY = target.getY();

        // Check if move is in a straight line
        if (sourceX != targetX && sourceY != targetY) {
            return false; // Not a straight line
        }

        int countBetween = 0;

        // Determine direction and count pieces between source and target
        if (sourceX == targetX) {
            int step = (targetY > sourceY) ? 1 : -1;
            for (int y = sourceY + step; y != targetY; y += step) {
                Place intermediate = source.withY(y);
                if (game.getPiece(intermediate) != null) {
                    countBetween++;
                }
            }
        } else {
            int step = (targetX > sourceX) ? 1 : -1;
            for (int x = sourceX + step; x != targetX; x += step) {
                Place intermediate = source.withX(x);
                if (game.getPiece(intermediate) != null) {
                    countBetween++;
                }
            }
        }

        Piece targetPiece = game.getPiece(target);

        // If target is empty, move is valid only if no pieces in between
        if (targetPiece == null) {
            return countBetween == 0;
        } else {
            // If capturing, exactly one piece must be between source and target
            return countBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}