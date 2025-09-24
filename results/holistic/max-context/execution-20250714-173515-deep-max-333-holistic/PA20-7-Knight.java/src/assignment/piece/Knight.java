
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        ArrayList<Move> validMoves = new ArrayList<>();
        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {2, 1, -1, -2, -2, -1, 1, 2};
        
        for (int i = 0; i < 8; i++) {
            int newX = source.x() + dx[i];
            int newY = source.y() + dy[i];
            Move move = new Move(source, newX, newY);
            if (validateMove(game, move)) {
                validMoves.add(move);
            }
        }
        
        return validMoves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[] { 
            new OutOfBoundaryRule(), 
            new OccupiedRule(), 
            new VacantRule(), 
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), 
            new KnightMoveRule(),
            new KnightBlockRule()
        };
        
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}