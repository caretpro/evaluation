
package assignment.protocol;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Archer moves like a queen but can only move at most 3 squares;
 * it cannot jump over occupied squares, and cannot capture its own pieces.
 */
public class Archer extends Piece {

    public Archer(Player player) {
        super(player, 'A');
    }

    @Override
    public Move[] getAvailableMoves(@NotNull Game game) {
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        Place src = this.getPlace();

        // eight directions: N, NE, E, SE, S, SW, W, NW
        int[] dx = { 0, +1, +1, +1, 0, -1, -1, -1 };
        int[] dy = { +1, +1,  0, -1, -1, -1,  0, +1 };

        for (int d = 0; d < 8; d++) {
            for (int step = 1; step <= 3; step++) {
                int nx = src.x() + dx[d] * step;
                int ny = src.y() + dy[d] * step;
                if (nx < 0 || ny < 0 || nx >= size || ny >= size) {
                    break;  // off‐board
                }
                Place dst = new Place(nx, ny);
                Piece occupant = game.getPiece(dst);
                // stop if same‐player occupies
                if (occupant != null && occupant.getPlayer().equals(this.getPlayer())) {
                    break;
                }
                Move m = new Move(src, dst);
                moves.add(m);
                // cannot go past opponent piece (capture)
                if (occupant != null) {
                    break;
                }
            }
        }
        return moves.toArray(Move[]::new);
    }
}