
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;

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
            boolean hasJumped = false;
            
            while (x >= 0 && x < size && y >= 0 && y < size) {
                Place dest = new Place(x, y);
                Piece target = game.getPiece(dest);
                
                if (!hasJumped) {
                    // Can move to empty square without jumping
                    if (target == null) {
                        Move move = new Move(source, dest);
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    } else {
                        hasJumped = true;
                    }
                } else {
                    // After jumping, can only capture opponent's piece
                    if (target != null) {
                        if (target.getPlayer() != this.getPlayer()) {
                            Move move = new Move(source, dest);
                            if (validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        break; // Stop after first piece after jump
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