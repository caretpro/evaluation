
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * The Archer piece (moves like the cannon in Xiangqi).
 */
public class Archer extends Piece {

    public Archer(Player player) {
        super(player, 'A', 2);
    }

    @Override
    public Move[] getAvailableMoves(Game game) {
        Place src = this.getPlace();
        List<Move> moves = new ArrayList<>();

        // four orthogonal directions
        int[] dxs = {1, -1, 0, 0};
        int[] dys = {0, 0, 1, -1};

        for (int dir = 0; dir < 4; dir++) {
            int dx = dxs[dir], dy = dys[dir];
            int x = src.x() + dx, y = src.y() + dy;
            int screens = 0;

            // walk until edge of board
            while (x >= 0 && y >= 0 && x < game.getConfiguration().getSize() && y < game.getConfiguration().getSize()) {
                Place dst = new Place(x, y);
                Piece intervening = game.getPiece(dst);

                if (intervening != null) {
                    screens++;
                    // we must step over this screen and continue scanning
                    x += dx; y += dy;
                    continue;
                }

                // empty destination: can move only if no screen yet
                if (screens == 0) {
                    moves.add(new Move(src, dst));
                }
                // otherwise fall through to next square

                x += dx; y += dy;
            }

            // now look for capture beyond exactly one screen
            // reset to just past first screen
            if (screens >= 1) {
                x = src.x() + dx;
                y = src.y() + dy;
                int seen = 0;
                while (x >= 0 && y >= 0 && x < game.getConfiguration().getSize() && y < game.getConfiguration().getSize()) {
                    Place dst = new Place(x, y);
                    Piece p = game.getPiece(dst);
                    if (p != null) {
                        seen++;
                        if (seen == 2) {
                            // second piece is potential capture
                            if (!p.getPlayer().equals(this.getPlayer())) {
                                moves.add(new Move(src, dst));
                            }
                            break;
                        }
                    }
                    x += dx; y += dy;
                }
            }
        }

        return moves.toArray(Move[]::new);
    }
}