
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Knight piece that moves similar to knight in chess.
 * Rules of move of Knight can be found in wikipedia (https://en.wikipedia.org/wiki/Knight_(chess)).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Knight_(chess)'>Wikipedia</a>
 */
public class Knight extends Piece {
    public Knight(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'K';
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
        List<Move> moves = new ArrayList<>();
        int[][] possibleMoves = { { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 }, { 1, -2 }, { 1, 2 }, { 2, -1 },
                { 2, 1 } };

        for (int[] move : possibleMoves) {
            int newRow = source.getRow() + move[0];
            int newCol = source.getCol() + move[1];
            if (newRow >= 0 && newRow < game.getConfiguration().getRows() && newCol >= 0
                    && newCol < game.getConfiguration().getCols()) {
                Place destination = new Place(newRow, newCol);
                Piece pieceAtDestination = game.getPiece(destination);
                if (pieceAtDestination == null || pieceAtDestination.getOwner() != this.getOwner()) {
                    Move newMove = new Move(source, destination);
                    moves.add(newMove);
                }
            }
        }

        return moves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        return true;
    }
}