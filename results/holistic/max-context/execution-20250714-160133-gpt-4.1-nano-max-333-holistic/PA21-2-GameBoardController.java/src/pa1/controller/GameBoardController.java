
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
        Objects.requireNonNull(direction);
        Position currentPos = gameBoard.getPlayer().getOwner().getPosition();

        MoveResult moveResult = tryMove(currentPos, direction);

        if (moveResult instanceof MoveResult.Valid validResult) {
            if (validResult instanceof MoveResult.Valid.Alive aliveResult) {
                // Save previous position
                Position prevPos = gameBoard.getPlayer().getOwner().getPosition();

                // Remove collected gems
                for (Position gemPos : aliveResult.collectedGems) {
                    gameBoard.setCell(gemPos, new StopCell());
                }
                // Remove collected extra lives
                for (Position lifePos : aliveResult.collectedExtraLives) {
                    gameBoard.setCell(lifePos, new StopCell());
                }

                // Move player to new position
                gameBoard.getPlayer().getOwner().setPosition(aliveResult.newPosition);

                // Update the old position cell to StopCell
                gameBoard.setCell(prevPos, new StopCell());

                // Set the new position cell to a PlayerCell or appropriate cell
                // Assuming PlayerCell is a subclass of EntityCell
                gameBoard.setCell(aliveResult.newPosition, new PlayerCell(gameBoard.getPlayer()));

                return validResult;
            } else if (validResult instanceof MoveResult.Valid.Dead deadResult) {
                // Player hits a mine and dies
                return deadResult;
            }
        }
        return moveResult;
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
        Objects.requireNonNull(prevMove);
        if (prevMove instanceof MoveResult.Valid validMove) {
            Position currentPos = gameBoard.getPlayer().getOwner().getPosition();
            Position originalPos = validMove.origPosition;

            // Move player back to original position
            gameBoard.getPlayer().getOwner().setPosition(originalPos);

            if (validMove instanceof MoveResult.Valid.Alive aliveMove) {
                // Restore gems
                for (Position gemPos : aliveMove.collectedGems) {
                    gameBoard.setCell(gemPos, new EntityCell(new Gem()));
                }
                // Restore extra lives
                for (Position lifePos : aliveMove.collectedExtraLives) {
                    gameBoard.setCell(lifePos, new EntityCell(new ExtraLife()));
                }
            }
            // Restore the cell at current position to StopCell
            gameBoard.setCell(currentPos, new StopCell());
        }
        // If move was invalid or player died, no action needed
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
                if (entityCell.getEntity() instanceof Mine) {
                    // Player hits a mine and dies
                    return new MoveResult.Valid.Dead(lastValidPosition, newPosition);
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
     * @return The given position offset by one in the specified direction. If the new position is outside of the game
     * board, or contains a non-{@link EntityCell}, returns {@code null}.
     */
    @Nullable
    private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        return position.offsetByOrNull(direction.getRowOffset(), direction.getColOffset(),
                gameBoard.getNumRows(), gameBoard.getNumCols());
    }
}