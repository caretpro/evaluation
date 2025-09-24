
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
            Place destination = new Place(source.row() + move[0], source.col() + move[1]);
            Move newMove = new Move(source, destination);
            if (validateMove(game, newMove)) {
                moves.add(newMove);
            }
        }

        return moves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        int numMovesProtection = game.getConfiguration().getNumMovesProtection();
        if (move.source().row() < 0 || move.source().row() >= game.getConfiguration().getRows() ||
                move.source().col() < 0 || move.source().col() >= game.getConfiguration().getCols() ||
                move.destination().row() < 0 || move.destination().row() >= game.getConfiguration().getRows() ||
                move.destination().col() < 0 || move.destination().col() >= game.getConfiguration().getCols()) {
            return false;
        }

        if (game.getPiece(move.destination()) != null && game.getPiece(move.destination()).getOwner() == this.getOwner()) {
            return false;
        }

        if (game.getTurn() < numMovesProtection && game.getPiece(move.source()).getOwner() != this.getOwner()) {
            return false;
        }

        int rowDiff = Math.abs(move.source().row() - move.destination().row());
        int colDiff = Math.abs(move.source().col() - move.destination().col());

        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false;
        }

        return true;
    }
}