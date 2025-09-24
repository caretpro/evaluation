
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;
import java.util.ArrayList;

/**
 * Archer piece that moves similar to cannon in Chinese chess.
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
     * All the returned {@link Move}s should have source equal to the source parameter.
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
        Piece[][] board = game.getBoard(); // Access the game board
        int rows = board.length;
        int cols = board[0].length;

        int sourceRow = source.getRow(); // Access source row
        int sourceCol = source.getCol(); // Access source column

        // Directions: up, down, left, right
        int[][] directions = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };

        for (int[] dir : directions) {
            boolean jumped = false; // indicates if we've jumped over a piece
            int r = sourceRow + dir[0];
            int c = sourceCol + dir[1];

            while (r >= 0 && r < rows && c >= 0 && c < cols) {
                Place targetPlace = new Place(r, c);
                Piece targetPiece = board[r][c];

                if (!jumped) {
                    // Not yet jumped over a piece
                    if (targetPiece == null) {
                        // Empty square - valid move
                        Move move = new Move(source, targetPlace);
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                        r += dir[0];
                        c += dir[1];
                    } else {
                        // Encountered a piece - can jump over if next is valid
                        jumped = true;
                        r += dir[0];
                        c += dir[1];
                    }
                } else {
                    // Already jumped over one piece, now look for capture
                    if (targetPiece != null) {
                        // Can only capture if opponent's piece
                        if (targetPiece.getPlayer() != this.getPlayer()) {
                            Move move = new Move(source, targetPlace);
                            if (validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        break; // Stop after attempting capture
                    } else {
                        // Empty square after jump - cannot land here
                        r += dir[0];
                        c += dir[1];
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