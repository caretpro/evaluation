
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
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int x = source.x() + dx;
            int y = source.y() + dy;
            boolean foundScreen = false;
            
            while (isValidPlace(game, x, y)) {
                Place dest = new Place(x, y);
                Move move = new Move(source, dest);
                
                if (!foundScreen) {
                    if (game.pieceAt(dest) == null) {
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    } else {
                        foundScreen = true;
                    }
                } else {
                    Piece target = game.pieceAt(dest);
                    if (target != null && target.getPlayer() != this.getPlayer()) {
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                        break;
                    } else if (target != null) {
                        break;
                    }
                }
                
                x += dx;
                y += dy;
            }
        }
        
        return moves.toArray(new Move[0]);
    }

    private boolean isValidPlace(Game game, int x, int y) {
        return x >= 0 && y >= 0 && x < game.boardWidth() && y < game.boardHeight();
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