
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
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int x = source.x() + dx;
            int y = source.y() + dy;
            
            // Move without capturing (must have no pieces in between)
            while (x >= 0 && x < size && y >= 0 && y < size) {
                Place dest = new Place(x, y);
                Move move = new Move(source, dest);
                
                if (game.getPiece(dest) == null) {
                    if (validateMove(game, move)) {
                        moves.add(move);
                    }
                } else {
                    break; // Hit a piece, can't move further without jumping
                }
                
                x += dx;
                y += dy;
            }
            
            // Check for capturing moves (must jump over exactly one piece)
            x = source.x() + dx;
            y = source.y() + dy;
            boolean foundPiece = false;
            
            while (x >= 0 && x < size && y >= 0 && y < size) {
                Place dest = new Place(x, y);
                Piece piece = game.getPiece(dest);
                
                if (piece != null) {
                    if (foundPiece) {
                        // We've already found one piece to jump over, check if we can capture
                        if (piece.getPlayer() != this.getPlayer()) {
                            Move move = new Move(source, dest);
                            if (validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        break;
                    } else {
                        foundPiece = true;
                    }
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