
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

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
        int maxRow = game.getConfiguration().getSize().rows();
        int maxCol = game.getConfiguration().getSize().cols();

        // Directions: up, down, left, right
        int[][] directions = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };

        for (int[] dir : directions) {
            int row = source.row() + dir[0];
            int col = source.col() + dir[1];

            boolean screenFound = false;

            while (row >= 0 && row < maxRow && col >= 0 && col < maxCol) {
                Place targetPlace = new Place(row, col);
                Piece targetPiece = game.getPieceAt(targetPlace);

                if (!screenFound) {
                    // Moving over empty squares before the screen
                    if (targetPiece == null) {
                        // Valid move without capture
                        Move move = new Move(source, targetPlace);
                        if (this.validateMove(game, move)) {
                            moves.add(move);
                        }
                        row += dir[0];
                        col += dir[1];
                    } else {
                        // Found the screen piece
                        screenFound = true;
                        row += dir[0];
                        col += dir[1];
                    }
                } else {
                    // After screen, look for capture
                    if (targetPiece != null) {
                        // Can capture if the piece is opponent's
                        if (targetPiece.getPlayer() != this.getPlayer()) {
                            Move move = new Move(source, targetPlace);
                            if (this.validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        // Stop after attempting capture
                        break;
                    } else {
                        // Empty square after screen - cannot move here
                        row += dir[0];
                        col += dir[1];
                    }
                }
            }
        }

        return moves.toArray(new Move[0]);
    }
}