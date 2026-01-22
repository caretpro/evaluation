
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;

/**
 * Archer piece that moves similar to cannon in Chinese chess.
 * Rules of move of Archer can be found in Wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
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
        int size = game.getConfiguration().getSize();

        // Directions: up, down, left, right
        int[][] directions = {
                {0, 1},   // up
                {0, -1},  // down
                {-1, 0},  // left
                {1, 0}    // right
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            boolean screenFound = false;
            int x = source.x();
            int y = source.y();

            // Move along the line
            while (true) {
                x += dx;
                y += dy;

                // Check boundary
                if (x < 0 || x >= size || y < 0 || y >= size) {
                    break;
                }

                Piece targetPiece = game.getPiece(x, y);
                if (!screenFound) {
                    // Before finding a screen, move over empty or opponent's piece (but can't capture yet)
                    if (targetPiece == null) {
                        // Move without capturing
                        Move move = new Move(source, new Place(x, y));
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    } else {
                        // Found a potential screen for capturing
                        screenFound = true;
                        // Continue to look for a target beyond the screen
                    }
                } else {
                    // After finding a screen, look for a capture
                    if (targetPiece != null) {
                        // Can only capture opponent's piece
                        if (!targetPiece.getPlayer().equals(this.getPlayer())) {
                            Move move = new Move(source, new Place(x, y));
                            if (validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        // Whether captured or not, stop after first piece beyond screen
                        break;
                    } else {
                        // Empty square beyond the screen - cannot move here for capture
                        // Continue searching
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