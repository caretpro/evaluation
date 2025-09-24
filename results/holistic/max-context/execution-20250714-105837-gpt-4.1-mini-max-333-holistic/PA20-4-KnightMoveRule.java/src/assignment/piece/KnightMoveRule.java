
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true; // Not a knight move, so this rule does not apply
        }

        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());

        // Check valid knight move pattern
        if (!((dx == 2 && dy == 1) || (dx == 1 && dy == 2))) {
            return false;
        }

        // Check destination is within board boundaries
        int size = game.getConfiguration().getSize();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();
        if (destX < 0 || destX >= size || destY < 0 || destY >= size) {
            return false;
        }

        // Check destination piece
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece != null && destPiece.getPlayer().equals(piece.getPlayer())) {
            // Cannot capture own piece
            return false;
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}