
package pa1.controller;

import pa1.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
    private final Stack<MoveResult> moveHistory; // To keep track of moves for undo

    /**
     * Creates an instance.
     *
     * @param gameBoard The instance of {@link GameBoard} to control.
     */
    public GameBoardController(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.moveHistory = new Stack<>();
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
        Position playerPos = gameBoard.getPlayerPosition();
        if (playerPos == null) {
            // No player position found; cannot move
            return new MoveResult.Invalid(null);
        }

        MoveResult moveResult = tryMove(playerPos, direction);
        if (moveResult instanceof MoveResult.Valid validResult) {
            // Save current state for undo
            moveHistory.push(validResult);
            // Apply move
            gameBoard.movePlayer(validResult.getPosition());

            // Remove collected gems and extra lives from the board
            for (Position gemPos : validResult.getCollectedGems()) {
                gameBoard.setCell(gemPos, new pa1.model.EmptyCell());
            }
            for (Position lifePos : validResult.getCollectedExtraLives()) {
                gameBoard.setCell(lifePos, new pa1.model.EmptyCell());
            }

            // Check if player is dead
            if (validResult instanceof MoveResult.Valid.Dead) {
                // Player died, revert move
                undoMove(validResult);
                return validResult;
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
        if (moveHistory.isEmpty() || moveHistory.peek() != prevMove) {
            // No move to undo or mismatch
            return;
        }
        // Revert move
        gameBoard.movePlayer(prevMove.getPreviousPosition());

        // Restore collected gems and extra lives
        if (prevMove instanceof MoveResult.Valid validMove) {
            for (Position gemPos : validMove.getCollectedGems()) {
                gameBoard.setCell(gemPos, new pa1.model.GemCell());
            }
            for (Position lifePos : validMove.getCollectedExtraLives()) {
                gameBoard.setCell(lifePos, new pa1.model.ExtraLifeCell());
            }
        }
        moveHistory.pop();
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
        Position currentPosition = position;

        while (true) {
            final Position newPosition = offsetPosition(currentPosition, direction);
            if (newPosition == null) {
                break;
            }

            Cell cell = gameBoard.getCell(newPosition);
            if (cell instanceof StopCell) {
                break;
            }

            if (cell instanceof EntityCell entityCell) {
                if (entityCell.getEntity() instanceof Mine) {
                    return new MoveResult.Valid.Dead(position, newPosition);
                }
                if (entityCell.getEntity() instanceof Gem) {
                    collectedGems.add(newPosition);
                } else if (entityCell.getEntity() instanceof ExtraLife) {
                    collectedExtraLives.add(newPosition);
                }
            }

            lastValidPosition = newPosition;
            currentPosition = newPosition;
        }

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
     * @return The given position offset by one in the specified direction. If the new position is outside of the game
     * board, or contains a non-{@link EntityCell}, returns {@code null}.
     */
    @Nullable
    private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final var newPos = position.offsetByOrNull(direction.getOffset(), gameBoard.getNumRows(),
                gameBoard.getNumCols());

        if (newPos == null) {
            return null;
        }
        if (!(gameBoard.getCell(newPos) instanceof EntityCell)) {
            return null;
        }

        return newPos;
    }
}