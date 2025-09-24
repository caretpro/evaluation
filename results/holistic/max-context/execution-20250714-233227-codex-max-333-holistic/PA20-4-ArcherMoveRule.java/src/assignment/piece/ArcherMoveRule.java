
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * The Archer piece moves like the cannon in Chinese chess:
 * it may move any number of empty squares in a straight line (no screens)
 * or capture by jumping exactly one intervening piece (one screen) and landing on an opponent.
 */
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
        Place src = this.getPlace();
        int size = game.getConfiguration().getSize();
        List<Move> moves = new ArrayList<>();

        // four orthogonal directions
        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (int[] dir : dirs) {
            int dx = dir[0], dy = dir[1];
            int x = src.x() + dx, y = src.y() + dy;
            int screens = 0;

            while (x >= 0 && x < size && y >= 0 && y < size) {
                Piece p = game.getPiece(x, y);
                if (p == null) {
                    // if no screen yet, can slide here (non‑capturing)
                    if (screens == 0) {
                        moves.add(new Move(src, x, y));
                    }
                    // otherwise still just scan for potential capture beyond screen
                } else {
                    // we hit a screen piece
                    screens++;
                    // if this is the first screen, keep scanning for a capture
                    if (screens == 1) {
                        // scan ahead for exactly one opponent
                        int cx = x + dx, cy = y + dy;
                        while (cx >= 0 && cx < size && cy >= 0 && cy < size) {
                            Piece target = game.getPiece(cx, cy);
                            if (target != null) {
                                if (!target.getPlayer().equals(this.getPlayer())) {
                                    moves.add(new Move(src, cx, cy));
                                }
                                break;
                            }
                            cx += dx;
                            cy += dy;
                        }
                    }
                    // after first screen (whether or not we captured), no further moves in this direction
                    break;
                }
                x += dx;
                y += dy;
            }
        }
        return moves.toArray(new Move[0]);
    }
}