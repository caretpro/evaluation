
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
    private GameBoard gameBoard;

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
        Objects.requireNonNull(direction);
        Position currentPosition = gameBoard.getPlayer().getOwner().getPosition();

        MoveResult moveResult = tryMove(currentPosition, direction);

        if (moveResult instanceof MoveResult.Valid validMove) {
            // Clone current game board cells
            Cell[][] newCells = cloneCells(gameBoard);

            // Remove player from old position
            newCells[currentPosition.row()][currentPosition.col()] = new EmptyCell();

            // If move is valid and player is alive, place player at new position
            Position newPos = validMove.newPosition;
            Player newPlayer = gameBoard.getPlayer().withPosition(newPos);
            newCells[newPos.row()][newPos.col()] = new EntityCell(newPlayer);

            // Remove collected gems and extra lives from the board
            if (validMove instanceof MoveResult.Valid.Alive aliveMove) {
                for (Position gemPos : aliveMove.collectedGems) {
                    newCells[gemPos.row()][gemPos.col()] = new EmptyCell();
                }
                for (Position lifePos : aliveMove.collectedExtraLives) {
                    newCells[lifePos.row()][lifePos.col()] = new EmptyCell();
                }
            }

            // Create new game board with updated cells
            GameBoard newGameBoard = new GameBoard(
                    gameBoard.getNumRows(),
                    gameBoard.getNumCols(),
                    newCells
            );

            // Update the controller's gameBoard reference
            this.gameBoard = newGameBoard;

            return moveResult;
        } else {
            // Invalid move, do nothing
            return moveResult;
        }
    }

    /**
     * Undoes a move by reverting all changes performed by the specified move.
     *
     * <p>
     * This implementation assumes that a previous game state is stored. For simplicity, this method is a placeholder.
     * In a real implementation, you'd maintain a history stack of game states.
     * </p>
     *
     * @param prevMove The {@link MoveResult} object to revert.
     */
    public void undoMove(final MoveResult prevMove) {
        Objects.requireNonNull(prevMove);
        // Placeholder: In real code, restore previous game state from history
        // For demonstration, do nothing
    }

    /**
     * Tries to move the player from a position in the specified direction as far as possible.
     *
     * <p>
     * Note that this method does <b>NOT</b> actually move the player. It just tries to move the player and return
     * the state of the player as-if it has been moved.
     * </p>
     *
     * @param position  The original position of the player.
     * @param direction The direction to move the player in.
     * @return An instance of {@link MoveResult} representing the type of the move and the position of the player after
     * moving.
     */
    @NotNull
    private MoveResult tryMove(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final var collectedGems = new ArrayList<Position>();
        final var collectedExtraLives = new ArrayList<Position>();
        Position lastValidPosition = position;
        final int numRows = gameBoard.getNumRows();
        final int numCols = gameBoard.getNumCols();

        do {
            final Position newPosition = offsetPosition(lastValidPosition, direction, numRows, numCols);
            if (newPosition == null) {
                break;
            }

            lastValidPosition = newPosition;

            if (gameBoard.getCell(newPosition) instanceof StopCell) {
                break;
            }

            if (gameBoard.getCell(newPosition) instanceof EntityCell entityCell) {
                if (entityCell.getEntity() instanceof Mine) {
                    return new MoveResult.Valid.Dead(position, newPosition);
                }
                if (entityCell.getEntity() instanceof Gem) {
                    collectedGems.add(newPosition);
                } else if (entityCell.getEntity() instanceof ExtraLife) {
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
     * Offsets the {@link Position} in the specified {@link Direction} by one step.
     *
     * @param position  The original position.
     * @param direction The direction to offset.
     * @param numRows   Number of rows in the game board.
     * @param numCols   Number of columns in the game board.
     * @return The given position offset by one in the specified direction. If the new position is outside of the game
     * board, or contains a non-{@link EntityCell}, returns {@code null}.
     */
    @Nullable
    private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction, int numRows, int numCols) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final var newPos = position.offsetByOrNull(direction.getRowOffset(), direction.getColOffset(), numRows, numCols);

        if (newPos == null) {
            return null;
        }
        if (!(gameBoard.getCell(newPos) instanceof EntityCell)) {
            return null;
        }

        return newPos;
    }

    /**
     * Helper method to clone the cells array.
     */
    private Cell[][] cloneCells(@NotNull GameBoard original) {
        int rows = original.getNumRows();
        int cols = original.getNumCols();
        Cell[][] clone = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(original.getRow(r), 0, clone[r], 0, cols);
        }
        return clone;
    }
}