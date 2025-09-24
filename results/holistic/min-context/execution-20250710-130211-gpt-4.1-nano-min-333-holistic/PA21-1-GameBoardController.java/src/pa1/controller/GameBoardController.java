
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
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
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
        // Find the current player position
        Position playerPosition = null;
        for (int row = 0; row < gameBoard.getNumRows(); row++) {
            for (int col = 0; col < gameBoard.getNumCols(); col++) {
                Position pos = new Position(row, col);
                Cell cell = gameBoard.getCell(pos);
                if (cell instanceof EntityCell entityCell) {
                    if (entityCell.getEntity() instanceof Player) {
                        playerPosition = pos;
                        break;
                    }
                }
            }
            if (playerPosition != null) {
                break;
            }
        }
        if (playerPosition == null) {
            return new MoveResult.Invalid(null);
        }

        // Attempt move
        MoveResult moveResult = tryMove(playerPosition, direction);

        if (moveResult instanceof MoveResult.Valid validResult) {
            // Save current state for undo
            GameBoard previousState;
            try {
                // Assuming GameBoard has a clone() method that returns a deep copy
                previousState = (GameBoard) gameBoard.clone();
            } catch (CloneNotSupportedException e) {
                previousState = null; // fallback if clone not supported
            }

            // Remove player from old position
            gameBoard.setCell(playerPosition, new pa1.model.EmptyCell());

            Position newPos = null;
            if (validResult instanceof MoveResult.Valid.Alive aliveResult) {
                newPos = aliveResult.getPosition();
            } else if (validResult instanceof MoveResult.Valid.Dead deadResult) {
                newPos = deadResult.getPosition();
            }

            // Place player at new position
            gameBoard.setCell(newPos, new EntityCell(new Player()));

            if (validResult instanceof MoveResult.Valid.Alive aliveResult) {
                // Remove collected gems
                for (Position gemPos : aliveResult.getCollectedGems()) {
                    gameBoard.setCell(gemPos, new pa1.model.EmptyCell());
                }
                // Remove collected extra lives
                for (Position lifePos : aliveResult.getCollectedExtraLives()) {
                    gameBoard.setCell(lifePos, new pa1.model.EmptyCell());
                }
            }

            // Verify if player is alive after move
            boolean playerAlive = true;
            Position playerNewPos = null;
            if (validResult instanceof MoveResult.Valid.Alive aliveResult) {
                playerNewPos = aliveResult.getPosition();
            } else if (validResult instanceof MoveResult.Valid.Dead deadResult) {
                playerNewPos = deadResult.getPosition();
            }

            Cell cellAtNewPos = gameBoard.getCell(playerNewPos);
            if (!(cellAtNewPos instanceof EntityCell entityCell) || !(entityCell.getEntity() instanceof Player)) {
                // Player not found at new position, assume dead
                playerAlive = false;
            }

            if (!playerAlive && previousState != null) {
                // Revert to previous state
                restoreGameBoard(previousState);
                return new MoveResult.Valid.Dead(playerPosition, playerPosition);
            }

            return validResult;
        } else {
            return moveResult;
        }
    }

    /**
     * Undoes a move by reverting all changes performed by the specified move.
     *
     * @param prevMove The {@link MoveResult} object to revert.
     */
    public void undoMove(final MoveResult prevMove) {
        if (prevMove == null) {
            return;
        }
        // Implementation depends on move history management
        throw new UnsupportedOperationException("undoMove requires move history management");
    }

    /**
     * Tries to move the player from a position in the specified direction as far as possible.
     *
     * @param position  The original position of the player.
     * @param direction The direction to move the player in.
     * @return An instance of {@link MoveResult} representing the type of the move and the position of the player after moving.
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

    /**
     * Helper method to restore the game board to a previous state.
     * Assumes deep copy or cloning is available.
     */
    private void restoreGameBoard(@NotNull final GameBoard previousState) {
        // Assuming GameBoard has a method setCellsFrom or similar
        // For example:
        // gameBoard.setCellsFrom(previousState);
        // Or replace internal state if possible
        // Placeholder:
        throw new UnsupportedOperationException("restoreGameBoard method needs implementation based on GameBoard's API");
    }
}