
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
        
        // Check all four directions
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            
            // Check for non-capture moves (must have clear path)
            boolean canMove = true;
            for (int step = 1; step < size; step++) {
                int x = source.x() + dx * step;
                int y = source.y() + dy * step;
                
                if (x < 0 || x >= size || y < 0 || y >= size) {
                    break; // Out of bounds
                }
                
                Place dest = new Place(x, y);
                Move move = new Move(source, dest);
                
                if (game.getPiece(dest) == null) {
                    if (validateMove(game, move)) {
                        moves.add(move);
                    }
                } else {
                    canMove = false;
                    break; // Piece in the way for non-capture move
                }
            }
            
            // Check for capture moves (must jump over exactly one piece)
            int screenCount = 0;
            for (int step = 1; step < size; step++) {
                int x = source.x() + dx * step;
                int y = source.y() + dy * step;
                
                if (x < 0 || x >= size || y < 0 || y >= size) {
                    break; // Out of bounds
                }
                
                Place dest = new Place(x, y);
                Piece targetPiece = game.getPiece(dest);
                
                if (targetPiece != null) {
                    screenCount++;
                    if (screenCount == 1) {
                        continue; // This is the screen piece
                    } else if (screenCount == 2) {
                        // Potential capture if enemy piece
                        if (targetPiece.getPlayer() != this.getPlayer()) {
                            Move move = new Move(source, dest);
                            if (validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        break;
                    }
                }
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