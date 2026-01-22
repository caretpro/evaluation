
package pa1.controller;

import pa1.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controller for {@link GameBoard}.
 *
 * <p>
 * This class is responsible for providing high-level operations to mutate a {@link GameBoard}. This should be the only
 * class which mutates the game board; Other classes should use this class to mutate the game board.
 * </p>
 */
public class GameBoardController {

    @NotNull
    private final GameBoard gameBoard;

    /**
     * Creates an instance.
     *
     * @param gameBoard The instance of {@link GameBoard} to control.
     */
    public GameBoardController(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    /**
     * Moves the player in the given direction.
     *
     * <p>
     * You should ensure that the game board is only mutated if the move is valid and results in the player still being
     * alive. If the player dies after moving or the move is invalid, the game board should remain in the same state as
     * before this method was called.
     * </p>
     *
     * @param direction Direction to move the player in.
     * @return An instance of {@link MoveResult} representing the result of this action.
     */
    public MoveResult makeMove(final Direction direction) {
        Position currentPosition = getPlayerPosition();
        if (currentPosition == null) {
            return new MoveResult.Invalid(null);
        }

        // Try to move in the specified direction
        MoveResult moveResult = tryMove(currentPosition, direction);

        if (moveResult instanceof MoveResult.Valid validResult) {
            List<Position> collectedGems = validResult.getCollectedGems();
            List<Position> collectedExtraLives = validResult.getCollectedExtraLives();

            Position previousPosition = currentPosition;
            Position newPosition = validResult.getNewPosition();

            // Move player from old to new position
            gameBoard.setCell(previousPosition, new EmptyCell());
            gameBoard.setCell(newPosition, new PlayerCell());

            // Remove collected entities from the board
            for (Position gemPos : collectedGems) {
                gameBoard.setCell(gemPos, new EmptyCell());
            }
            for (Position lifePos : collectedExtraLives) {
                gameBoard.setCell(lifePos, new EmptyCell());
            }

            // Check if player is dead
            if (moveResult instanceof MoveResult.Valid.Dead) {
                // Revert move
                gameBoard.setCell(newPosition, new EmptyCell());
                gameBoard.setCell(previousPosition, new PlayerCell());
                // Restore collected gems
                for (Position pos : collectedGems) {
                    gameBoard.setCell(pos, new EntityCell(new Gem()));
                }
                // Restore collected extra lives
                for (Position pos : collectedExtraLives) {
                    gameBoard.setCell(pos, new EntityCell(new ExtraLife()));
                }
                return moveResult; // Player died, move invalid
            }

            return validResult;
        } else {
            // Invalid move, do nothing
            return moveResult;
        }
    }

    /**
     * Undoes a move by reverting all changes performed by the specified move.
     *
     * <p>
     * Hint: Undoing a move is effectively the same as reversing everything you have done to make a move.
     * </p>
     *
     * @param prevMove The {@link MoveResult} object to revert.
     */
    public void undoMove(final MoveResult prevMove) {
        if (!(prevMove instanceof MoveResult.Valid validMove)) {
            return; // Cannot undo invalid move
        }

        Position previousPosition = validMove.getPreviousPosition();
        Position currentPosition = validMove.getNewPosition();

        // Move player back to previous position
        gameBoard.setCell(currentPosition, new EmptyCell());
        gameBoard.setCell(previousPosition, new PlayerCell());

        // Restore collected gems
        for (Position pos : validMove.getCollectedGems()) {
            gameBoard.setCell(pos, new EntityCell(new Gem()));
        }

        // Restore collected extra lives
        for (Position pos : validMove.getCollectedExtraLives()) {
            gameBoard.setCell(pos, new EntityCell(new ExtraLife()));
        }
    }

    /**
     * Helper method to get the player's current position.
     *
     * @return The player's position, or null if not found.
     */
    private Position getPlayerPosition() {
        int rows = gameBoard.getNumRows();
        int cols = gameBoard.getNumCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Position pos = new Position(r, c);
                Cell cell = gameBoard.getCell(pos);
                if (cell instanceof PlayerCell) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Implements the tryMove method to simulate moving the player without mutating the game state.
     *
     * @param position  The starting position.
     * @param direction The direction to move.
     * @return A MoveResult indicating the move outcome.
     */
    private MoveResult tryMove(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final var collectedGems = new ArrayList<Position>();
        final var collectedExtraLives = new ArrayList<Position>();
        Position lastValidPosition = position;

        do {
            final Position newPosition = offsetPosition(lastValidPosition, direction);
            if (newPosition == null) {
                break;
            }

            lastValidPosition = newPosition;

            Cell cell = gameBoard.getCell(newPosition);
            if (cell instanceof StopCell) {
                break;
            }

            if (cell instanceof EntityCell entityCell) {
                Entity entity = entityCell.getEntity();
                if (entity instanceof Mine) {
                    return new MoveResult.Valid.Dead(position, newPosition);
                }
                if (entity instanceof Gem) {
                    collectedGems.add(newPosition);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(newPosition);
                }
            }
        } while (true);

        if (lastValidPosition.equals(position)) {
            return new MoveResult.Invalid(position);
        }

        return new MoveResult.Valid.Alive(lastValidPosition, position, collectedGems, collectedExtraLives);
    }

    /**
     * Offsets the position in the specified direction.
     */
    @Nullable
    private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final var newPos = position.offsetByOrNull(direction.getOffset(), gameBoard.getNumRows(), gameBoard.getNumCols());

        if (newPos == null) {
            return null;
        }
        Cell cell = gameBoard.getCell(newPos);
        if (!(cell instanceof EntityCell)) {
            return null;
        }
        return newPos;
    }

    // Inner classes representing empty and player cells, implementing Cell
    private static class EmptyCell extends Cell {
        @Override
        public char toASCIIChar() {
            return ' ';
        }
        @Override
        public char toUnicodeChar() {
            return ' ';
        }
    }

    private static class PlayerCell extends Cell {
        @Override
        public char toASCIIChar() {
            return 'P';
        }
        @Override
        public char toUnicodeChar() {
            return 'P';
        }
    }
}