
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
     * Returns an array of moves that are valid given the current place of the piece.
     * Given the {@link Game} object and the {@link Place} that current archer piece locates, this method should
     * return ALL VALID {@link Move}s according to the current {@link Place} of this archer piece.
     * All the returned {@link Move} should have source equal to the source parameter.
     *
     * @param game   the game object
     * @param source the current place of the piece
     * @return an array of available moves
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        int size = game.getConfiguration().getSize();
        List<Move> moves = new ArrayList<>();

        // Explore in four orthogonal directions
        int[][] deltas = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] delta : deltas) {
            int dx = delta[0], dy = delta[1];
            boolean hasJumped = false;  // whether we've jumped over exactly one piece

            int x = source.x() + dx;
            int y = source.y() + dy;
            while (x >= 0 && x < size && y >= 0 && y < size) {
                if (!hasJumped) {
                    // before jump: we may move freely over empty squares
                    if (game.getPiece(x, y) == null) {
                        // ordinary move to empty square
                        Move m = new Move(source, x, y);
                        if (validateMove(game, m)) {
                            moves.add(m);
                        }
                        x += dx; y += dy;
                    } else {
                        // we hit the screen (first piece)—now enter jump mode
                        hasJumped = true;
                        x += dx; y += dy;
                    }
                } else {
                    // after jump: only exactly one capture is allowed, then stop
                    if (game.getPiece(x, y) != null) {
                        Move m = new Move(source, x, y);
                        if (validateMove(game, m)) {
                            moves.add(m);
                        }
                        break;
                    }
                    x += dx; y += dy;
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