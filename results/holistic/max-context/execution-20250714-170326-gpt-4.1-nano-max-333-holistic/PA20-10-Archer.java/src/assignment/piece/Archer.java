
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
     * Implements movement rules similar to Xiangqi Cannon.
     *
     * @param game   the game object
     * @param source the current place of this Archer piece
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

            while (true) {
                x += dx;
                y += dy;

                // Boundary check
                if (x < 0 || x >= size || y < 0 || y >= size) {
                    break;
                }

                Piece targetPiece = game.getPiece(x, y);

                if (!screenFound) {
                    // Moving over empty squares before the screen
                    if (targetPiece == null) {
                        // Can move to empty square
                        Move move = new Move(source, new Place(x, y));
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                        // Continue moving further
                    } else {
                        // Found the first piece - potential screen for capture
                        screenFound = true;
                        // Can't land here, continue to look for capture
                    }
                } else {
                    // After screen found, look for opponent's piece to capture
                    if (targetPiece != null) {
                        // Can capture only if the piece belongs to opponent
                        if (!targetPiece.getPlayer().equals(this.getPlayer())) {
                            Move move = new Move(source, new Place(x, y));
                            if (validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        // Whether captured or not, stop in this direction
                        break;
                    }
                    // If empty, can't land here, continue searching
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