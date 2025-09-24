
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.protocol.Configuration;

import java.util.ArrayList;
import java.util.List;

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
        List<Move> moves = new ArrayList<>();
        Configuration config = game.getConfiguration();
        int boardSize = config.getBoardSize();

        // Check horizontal moves
        addHorizontalMoves(game, source, moves, boardSize);

        // Check vertical moves
        addVerticalMoves(game, source, moves, boardSize);

        return moves.toArray(new Move[0]);
    }

    private void addHorizontalMoves(Game game, Place source, List<Move> moves, int boardSize) {

        // Check moves to the right
        for (int x = source.getX() + 1; x < boardSize; x++) {
            Place destination = new Place(x, source.getY());
            Move move = new Move(source, destination);
            if (validateMove(game, move)) {
                moves.add(move);
            }
        }

        // Check moves to the left
        for (int x = source.getX() - 1; x >= 0; x--) {
            Place destination = new Place(x, source.getY());
            Move move = new Move(source, destination);
            if (validateMove(game, move)) {
                moves.add(move);
            }
        }
    }

    private void addVerticalMoves(Game game, Place source, List<Move> moves, int boardSize) {

        // Check moves upwards
        for (int y = source.getY() + 1; y < boardSize; y++) {
            Place destination = new Place(source.getX(), y);
            Move move = new Move(source, destination);
            if (validateMove(game, move)) {
                moves.add(move);
            }
        }

        // Check moves downwards
        for (int y = source.getY() - 1; y >= 0; y--) {
            Place destination = new Place(source.getX(), y);
            Move move = new Move(source, destination);
            if (validateMove(game, move)) {
                moves.add(move);
            }
        }
    }

    private boolean validateMove(Game game, Move move) {
        // TODO: Implement the rules correctly based on the game's logic and the Archer's movement rules.
        // This is a placeholder and needs to be replaced with the actual rule implementations.
        return true;
    }
}