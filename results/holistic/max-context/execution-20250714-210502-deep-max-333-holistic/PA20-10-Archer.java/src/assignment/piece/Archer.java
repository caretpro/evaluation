
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;

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

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        ArrayList<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        
        // Check all four orthogonal directions
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int x = source.x() + dx;
            int y = source.y() + dy;
            int piecesInBetween = 0;
            
            // Move along the direction until out of bounds
            while (x >= 0 && x < size && y >= 0 && y < size) {
                Place dest = new Place(x, y);
                Piece targetPiece = game.getPiece(dest);
                Move move = new Move(source, dest);
                
                if (piecesInBetween == 0) {
                    // Can move to empty square or capture with exactly one piece in between
                    if (targetPiece == null) {
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    } else {
                        piecesInBetween++;
                    }
                } else if (piecesInBetween == 1) {
                    // Can only capture if there's exactly one piece in between
                    if (targetPiece != null && targetPiece.getPlayer() != this.getPlayer()) {
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                        break; // Can't jump over multiple pieces
                    }
                } else {
                    break; // Can't jump over more than one piece
                }
                
                x += dx;
                y += dy;
            }
        }
        
        return moves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[] { new OutOfBoundaryRule(), new OccupiedRule(), new VacantRule(), new NilMoveRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), new ArcherMoveRule(), };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}