
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Archer piece that moves similar to cannon in chinese chess.
 * Rules of move of Archer can be found in wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class Archer extends Piece {
    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    /**
     * Returns all valid moves of the cannon: straight moves over empty squares,
     * or captures by jumping exactly one piece and landing on an enemy piece.
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };

        for (int[] dir : directions) {
            int dx = dir[0], dy = dir[1];
            int x = source.x() + dx, y = source.y() + dy;

            // 1) Non‑capturing moves: slide over empty squares
            while (x >= 0 && x < game.getConfiguration().getSize()
                && y >= 0 && y < game.getConfiguration().getSize()
                && game.getPiece(x, y) == null) 
            {
                Move m = new Move(source, x, y);
                if (validateMove(game, m)) {
                    moves.add(m);
                }
                x += dx;
                y += dy;
            }

            // 2) Capturing moves: need exactly one jumper
            // Skip the first non‑empty square (the “screen”), then
            // capture the very next enemy piece, if any.
            if (x >= 0 && x < game.getConfiguration().getSize()
             && y >= 0 && y < game.getConfiguration().getSize()
             && game.getPiece(x, y) != null) 
            {
                // found the “screen”
                x += dx;
                y += dy;
                if (x >= 0 && x < game.getConfiguration().getSize()
                 && y >= 0 && y < game.getConfiguration().getSize()
                 && game.getPiece(x, y) != null
                 && !game.getPiece(x, y).getPlayer().equals(getPlayer()))
                {
                    Move capture = new Move(source, x, y);
                    if (validateMove(game, capture)) {
                        moves.add(capture);
                    }
                }
            }
        }

        return moves.toArray(new Move[0]);
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