
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

    /**
     * Returns an array of moves that are valid given the current place of the piece.
     * Given the {@link Game} object and the {@link Place} that current knight piece locates, this method should
     * return ALL VALID {@link Move}s according to the current {@link Place} of this knight piece.
     * All the returned {@link Move} should have source equal to the source parameter.
     * <p>
     * Hint: you should consider corner cases when the {@link Move} is not valid on the gameboard.
     * Several tests are provided and your implementation should pass them.
     * <p>
     * <strong>Attention: Student should make sure all {@link Move}s returned are valid.</strong>
     *
     * @param game   the game object
     * @param source the current place of the piece
     * @return an array of available moves
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        ArrayList<Move> moves = new ArrayList<>();
        int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} }; // down, up, right, left
        int maxRow = game.getBoard().getNumRows();
        int maxCol = game.getBoard().getNumCols();

        for (int[] dir : directions) {
            boolean screenFound = false;
            int row = source.getRow() + dir[0];
            int col = source.getCol() + dir[1];

            while (row >= 0 && row < maxRow && col >= 0 && col < maxCol) {
                Place targetPlace = new Place(row, col);
                Piece targetPiece = game.getBoard().getPieceAt(targetPlace);

                if (!screenFound) {
                    // Moving through empty spaces
                    if (targetPiece == null) {
                        // Valid move to empty space
                        moves.add(new Move(source, targetPlace));
                        row += dir[0];
                        col += dir[1];
                    } else {
                        // Found a screen piece
                        screenFound = true;
                        row += dir[0];
                        col += dir[1];
                    }
                } else {
                    // After screen is found, look for capture
                    if (targetPiece != null) {
                        if (targetPiece.getPlayer() != this.getPlayer()) {
                            // Enemy piece can be captured
                            moves.add(new Move(source, targetPlace));
                        }
                        // Stop after attempting capture
                        break;
                    } else {
                        // Empty space beyond screen, cannot move here
                        row += dir[0];
                        col += dir[1];
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