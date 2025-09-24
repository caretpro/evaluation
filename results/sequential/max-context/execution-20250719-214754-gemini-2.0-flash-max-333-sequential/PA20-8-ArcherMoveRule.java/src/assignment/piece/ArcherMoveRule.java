 it didn't account for the case where the destination is blocked by two or more pieces. The Archer should only be able to jump over exactly one piece.

```java
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }

        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        if ((sourceX == destX && sourceY == destY)) {
            return false;
        }

        if (sourceX == destX) {
            int count = 0;
            int start = Math.min(sourceY, destY) + 1;
            int end = Math.max(sourceY, destY);
            for (int i = start; i < end; i++) {
                if (game.getPiece(sourceX, i) != null) {
                    count++;
                }
            }
            if (count == 1 && game.getPiece(destX, destY) == null) {
                return true;
            }
            if (count == 1 && game.getPiece(destX, destY) != null) {
                return true;
            }
        } else if (sourceY == destY) {
            int count = 0;
            int start = Math.min(sourceX, destX) + 1;
            int end = Math.max(sourceX, destX);
            for (int i = start; i < end; i++) {
                if (game.getPiece(i, sourceY) != null) {
                    count++;
                }
            }
            if (count == 1 && game.getPiece(destX, destY) == null) {
                return true;
            }
            if (count == 1 && game.getPiece(destX, destY) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}