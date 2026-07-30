
package pa1.controller;

import pa1.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller for {@link GameBoard}.
 *
 * <p>
 * This class is responsible for providing high-level operations to mutate a {@link GameBoard}. This should be the only
 * class which mutates the game board; other classes should use this class to mutate the game board.
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
     * @param direction Direction to move the player in.
     * @return An instance of {@link MoveResult} representing the result of this action.
     */
    @NotNull
    public MoveResult makeMove(final Direction direction) {
        Objects.requireNonNull(direction);

        final var player = gameBoard.getPlayer();
        final var playerCell = Objects.requireNonNull(player.getOwner());
        final var result = tryMove(playerCell.getPosition(), direction);

        if (!(result instanceof MoveResult.Valid.Alive alive)) {
            return result;
        }

        final var originalCell = gameBoard.getEntityCell(alive.origPosition);
        final var destinationCell = gameBoard.getEntityCell(alive.newPosition);

        originalCell.setEntity(null);

        for (final var gemPosition : alive.collectedGems) {
            gameBoard.getEntityCell(gemPosition).setEntity(null);
        }

        for (final var extraLifePosition : alive.collectedExtraLives) {
            gameBoard.getEntityCell(extraLifePosition).setEntity(null);
        }

        destinationCell.setEntity(player);

        return result;
    }

    /**
     * Undoes a move by reverting all changes performed by the specified move.
     *
     * @param prevMove The {@link MoveResult} object to revert.
     */
    public void undoMove(final MoveResult prevMove) {
        Objects.requireNonNull(prevMove);

        if (!(prevMove instanceof MoveResult.Valid.Alive alive)) {
            return;
        }

        final var player = gameBoard.getPlayer();
        final var originalCell = gameBoard.getEntityCell(alive.origPosition);
        final var destinationCell = gameBoard.getEntityCell(alive.newPosition);

        destinationCell.setEntity(null);
        originalCell.setEntity(player);

        for (final var gemPosition : alive.collectedGems) {
            gameBoard.getEntityCell(gemPosition).setEntity(new Gem());
        }

        for (final var extraLifePosition : alive.collectedExtraLives) {
            gameBoard.getEntityCell(extraLifePosition).setEntity(new ExtraLife());
        }
    }

    /**
     * Tries to move the player from a position in the specified direction as far as possible.
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

        return new MoveResult.Valid.Alive(
                lastValidPosition,
                position,
                collectedGems,
                collectedExtraLives
        );
    }

    /**
     * Offsets the {@link Position} in the specified {@link Direction} by one step.
     *
     * @param position  The original position.
     * @param direction The direction to offset.
     * @return The offset position, or {@code null} if it is outside the board or not an entity cell.
     */
    @Nullable
    private Position offsetPosition(
            @NotNull final Position position,
            @NotNull final Direction direction
    ) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final var newPosition = position.offsetByOrNull(
                direction.getOffset(),
                gameBoard.getNumRows(),
                gameBoard.getNumCols()
        );

        if (newPosition == null || !(gameBoard.getCell(newPosition) instanceof EntityCell)) {
            return null;
        }

        return newPosition;
    }
}