
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * The Archer (cannon) moves any number of empty squares along a rank/file
 * or, to capture, must jump exactly one piece then land on an opponent.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Xiangqi#Cannon">Xiangqi Cannon</a>
 */
public class Archer extends Piece {

    @Override
    public List<Move> getAvailableMoves(Game game, Position from) {
        List<Move> moves = new ArrayList<>();
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        for (int[] d : dirs) {
            int dx = d[0], dy = d[1];
            int x = from.x(), y = from.y();

            // 1) Non-capturing moves: go as far as empty squares allow
            while (true) {
                x += dx;
                y += dy;
                Position p = new Position(x, y);
                if (!p.inBounds() || game.hasPiece(p)) {
                    break;
                }
                moves.add(new MoveImpl(from, p));
            }

            // 2) Capturing move: jump exactly one piece, then land on first enemy
            //    piece (cannot land on empty or own piece)
            boolean jumped = false;
            // Reset to start
            x = from.x();
            y = from.y();
            while (true) {
                x += dx;
                y += dy;
                Position p = new Position(x, y);
                if (!p.inBounds()) {
                    break;
                }
                if (!jumped) {
                    if (game.hasPiece(p)) {
                        jumped = true;
                    }
                } else {
                    // after jumping exactly one, must land on an opponent
                    if (game.hasPiece(p)) {
                        if (game.getPiece(p).getOwner() != getOwner()) {
                            moves.add(new MoveImpl(from, p));
                        }
                        break;  // whether enemy or own, stop searching this direction
                    } else {
                        // empty beyond the jump is illegal for capture
                        break;
                    }
                }
            }
        }

        return moves;
    }
}