
package assignment.protocol;

import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

/**
 * Rule that prohibits any capturing move or win within the first N moves.
 */
public class FirstNMovesProtectionRule implements Rule {

    private final int protectedMoves;

    /**
     * @param protectedMoves the number of initial moves during which captures or wins are forbidden
     */
    public FirstNMovesProtectionRule(int protectedMoves) {
        this.protectedMoves = protectedMoves;
    }

    @Override
    public boolean validate(@NotNull Game game, @NotNull Move move) {
        // Only enforce protection on the first N moves
        if (game.getNumMoves() < protectedMoves) {
            Piece srcPiece = game.getPiece(move.getSource());
            Piece destPiece = game.getPiece(move.getDestination());

            // If destination is occupied by an enemy piece → capturing → forbidden
            if (destPiece != null && !destPiece.getPlayer().equals(srcPiece.getPlayer())) {
                return false;
            }

            // If moving onto the central place would immediately win → forbidden
            Place central = game.getCentralPlace();
            if (move.getDestination().equals(central)) {
                return false;
            }
        }
        return true;
    }
}