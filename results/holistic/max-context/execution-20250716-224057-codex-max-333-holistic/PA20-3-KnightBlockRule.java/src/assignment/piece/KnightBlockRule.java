
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * The Knight piece.
 * It moves in an “L”: two squares in one orthogonal direction,
 * then one square perpendicular, and is blocked if there is
 * a piece on the “leg” square.
 */
public class Knight extends Piece {

    public Knight(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'N';
    }

    @Override
    public Move[] getAvailableMoves(Game game) {
        Place src = getPlace();
        int sx = src.x(), sy = src.y();
        int[][] deltas = {
            { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 },
            { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 }
        };

        List<Move> moves = new ArrayList<>();
        for (int[] d : deltas) {
            int dx = d[0], dy = d[1];
            Place dst = new Place(sx + dx, sy + dy);
            // out of board?
            if (!game.getConfiguration().isOnBoard(dst)) {
                continue;
            }
            // same-color occupied?
            var target = game.getPiece(dst);
            if (target != null && target.getPlayer() == getPlayer()) {
                continue;
            }
            Move m = new Move(src, dst);

            // Check all global rules
            boolean legal = true;
            for (Rule rule : game.getConfiguration().getRules()) {
                if (!rule.validate(game, m)) {
                    legal = false;
                    break;
                }
            }
            if (legal) {
                moves.add(m);
            }
        }
        return moves.toArray(new Move[0]);
    }
}