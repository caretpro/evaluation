
package assignment.piece;

import assignment.protocol.Color;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An Archer moves any number of squares orthogonally, but may not jump over pieces.
 * It can capture on its line of movement.
 */
public class Archer extends Piece {

    public Archer(Player player) {
        super(player, 'A');
    }

    @Override
    public @NotNull Move[] getAvailableMoves(Game game) {
        Place src = this.getPlace();
        int size = game.getConfiguration().getSize();
        List<Move> moves = new ArrayList<>();

        // Four orthogonal directions
        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (var d : dirs) {
            int dx = d[0], dy = d[1];
            int x = src.x() + dx, y = src.y() + dy;
            while (x >= 0 && x < size && y >= 0 && y < size) {
                var dest = new Place(x, y);
                var occupant = game.getPiece(dest);
                // First N moves protection: cannot capture or win if within protection window
                boolean protectedPhase = game.getNumMoves() < game.getConfiguration().getNumMovesProtection();
                // If empty square – always a legal move
                if (occupant == null) {
                    moves.add(new Move(src, dest));
                } else {
                    // occupied—if same player, block further movement
                    if (occupant.getPlayer() == this.getPlayer()) {
                        break;
                    }
                    // occupied by opponent—capture allowed only outside protection
                    if (!protectedPhase) {
                        moves.add(new Move(src, dest));
                    }
                    break;
                }
                x += dx;
                y += dy;
            }
        }
        return moves.toArray(Move[]::new);
    }

    @Override
    public String toString() {
        return String.format("%s%c%s", getPlayer().getColor(), getLabel(), Color.DEFAULT);
    }
}