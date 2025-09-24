
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
            return true;
        }

        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        int dx = Math.abs(destX - sourceX);
        int dy = Math.abs(destY - sourceY);

        // Knight moves in L shape: 2 by 1 or 1 by 2
        boolean validMoveShape = (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
        if (!validMoveShape) {
            return false;
        }

        Piece destPiece = game.getPiece(move.getDestination());
        // Destination must be empty or occupied by opponent's piece
        if (destPiece != null && destPiece.getPlayer().equals(piece.getPlayer())) {
            return false;
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}