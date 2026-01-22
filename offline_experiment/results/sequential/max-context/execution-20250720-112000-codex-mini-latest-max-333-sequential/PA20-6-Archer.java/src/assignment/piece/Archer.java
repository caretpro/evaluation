
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Archer piece that moves like the Xiangqi cannon: along ranks/files,
 * non‑capture moves over empty squares; captures only by jumping exactly one piece (“screen”).
 *
 * @see <a href="https://en.wikipedia.org/wiki/Xiangqi#Cannon">Xiangqi Cannon</a>
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
    public Move[] getAvailableMoves(Game game, Place source) {
        int size = game.getConfiguration().getSize();
        List<Move> moves = new ArrayList<>();
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        for (int[] d : dirs) {
            int dx = d[0], dy = d[1];
            int x = source.x() + dx, y = source.y() + dy;

            // 1) non‑capture sliding: move as far as empty squares go
            while (x >= 0 && x < size && y >= 0 && y < size && game.getPiece(x,y) == null) {
                Move m = new Move(source, x, y);
                if (validateMove(game, m)) {
                    moves.add(m);
                }
                x += dx; y += dy;
            }

            // 2) capture over exactly one screen: skip over exactly one piece
            if (x >= 0 && x < size && y >= 0 && y < size && game.getPiece(x,y) != null) {
                // that piece is the screen
                x += dx; y += dy;
                // the very next occupied square may be captured
                if (x >= 0 && x < size && y >= 0 && y < size && game.getPiece(x,y) != null) {
                    Move m = new Move(source, x, y);
                    if (validateMove(game, m)) {
                        moves.add(m);
                    }
                }
            }
        }
        return moves.toArray(Move[]::new);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[] {
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new VacantRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
            new ArcherMoveRule(),
        };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}