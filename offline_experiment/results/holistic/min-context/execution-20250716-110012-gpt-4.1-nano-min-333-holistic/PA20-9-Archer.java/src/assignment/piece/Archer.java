
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
        Piece[][] board = game.getBoard(); // Assuming getBoard() returns Piece[][]
        int rows = board.length;
        int cols = board[0].length;

        int sourceRow = source.getRow();
        int sourceCol = source.getCol();

        // Directions: up, down, left, right
        int[][] directions = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };

        for (int[] dir : directions) {
            int r = sourceRow + dir[0];
            int c = sourceCol + dir[1];

            // Phase 1: Moving without jumping (normal move)
            while (r >= 0 && r < rows && c >= 0 && c < cols) {
                Piece targetPiece = board[r][c];
                Place targetPlace = new Place(r, c);

                if (targetPiece == null) {
                    // Empty square - valid move
                    Move move = new Move(source, targetPlace);
                    if (this.validateMove(game, move)) {
                        moves.add(move);
                    }
                    r += dir[0];
                    c += dir[1];
                } else {
                    // Encountered a piece - attempt to jump over exactly one piece
                    break;
                }
            }

            // Phase 2: Jump over exactly one piece to capture
            r = sourceRow + dir[0];
            c = sourceCol + dir[1];

            boolean jumped = false;
            while (r >= 0 && r < rows && c >= 0 && c < cols) {
                Piece targetPiece = board[r][c];
                if (!jumped) {
                    // Looking for the first obstacle to jump over
                    if (targetPiece != null) {
                        jumped = true;
                        r += dir[0];
                        c += dir[1];
                    } else {
                        // No obstacle yet, continue
                        r += dir[0];
                        c += dir[1];
                    }
                } else {
                    // After jumping over one piece, look for opponent's piece to capture
                    if (targetPiece != null) {
                        if (targetPiece.getPlayer() != this.getPlayer()) {
                            Place targetPlace = new Place(r, c);
                            Move move = new Move(source, targetPlace);
                            if (this.validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        break; // Stop after attempting capture
                    } else {
                        // Empty square after jump - continue
                        r += dir[0];
                        c += dir[1];
                    }
                }
            }
        }

        return moves.toArray(new Move[0]);
    }
}