
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
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int x = source.x() + dx;
            int y = source.y() + dy;
            boolean foundScreen = false;

            while (x >= 0 && x < game.size() && y >= 0 && y < game.size()) {
                Place target = new Place(x, y);
                Move move = new Move(source, target);
                
                if (!foundScreen) {
                    if (game.at(target) == null) {
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    } else {
                        foundScreen = true;
                    }
                } else {
                    if (game.at(target) != null) {
                        if (game.at(target).player() != this.player() 
                            && validateMove(game, move)) {
                            moves.add(move);
                        }
                        break;
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
                new FirstNMovesProtectionRule(game.config().movesProtection()), new ArcherMoveRule(), };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}