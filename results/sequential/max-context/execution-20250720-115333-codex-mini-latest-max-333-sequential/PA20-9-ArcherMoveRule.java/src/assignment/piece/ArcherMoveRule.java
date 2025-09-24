
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Place;
import assignment.protocol.Move;
import assignment.protocol.Player;

import java.util.ArrayList;
import java.util.List;

public class Archer extends Piece {

    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game) {
        Place src = getPlace();
        List<Move> moves = new ArrayList<>();

        // four straight directions
        int[][] deltas = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (int[] d : deltas) {
            int dx = d[0], dy = d[1];
            int x = src.x() + dx, y = src.y() + dy;
            int screens = 0;

            // slide until board edge
            while (x >= 0 && x < game.getConfiguration().getSize()
                   && y >= 0 && y < game.getConfiguration().getSize()) {
                Place dst = new Place(x, y);
                Piece p = game.getPiece(dst);

                if (p != null) {
                    screens++;
                    // once we hit one screen, keep going to find capture
                    x += dx; y += dy;
                    // look for exactly one screen before an enemy
                    while (x >= 0 && x < game.getConfiguration().getSize()
                           && y >= 0 && y < game.getConfiguration().getSize()) {
                        dst = new Place(x, y);
                        p = game.getPiece(dst);
                        if (p != null) {
                            if (screens == 1 && p.getPlayer() != game.getCurrentPlayer()) {
                                moves.add(new Move(src, dst));
                            }
                            break;
                        }
                        x += dx; y += dy;
                    }
                    break;
                } else {
                    // no screen yet: valid non-capture move
                    moves.add(new Move(src, dst));
                }

                x += dx; y += dy;
            }
        }

        return moves.toArray(new Move[0]);
    }
}