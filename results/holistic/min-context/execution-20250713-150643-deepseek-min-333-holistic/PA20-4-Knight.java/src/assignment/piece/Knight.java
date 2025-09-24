
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
        
        int[][] possibleMoves = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        
        for (int[] move : possibleMoves) {
            int newRow = source.getRowIndex() + move[0];
            int newCol = source.getColumnIndex() + move[1];
            Place destination = new Place(newRow, newCol);
            Move potentialMove = new Move(source, destination);
            
            if (validateMove(game, potentialMove)) {
                validMoves.add(potentialMove);
            }
        }
        
        return validMoves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[] { new OutOfBoundaryRule(), new OccupiedRule(), new VacantRule(), new NilMoveRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), new KnightMoveRule(),
                new KnightBlockRule(), };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}