
package pa1.controller;

import pa1.model.Cell;
import pa1.model.Direction;
import pa1.model.EntityCell;
import pa1.model.ExtraLife;
import pa1.model.GameBoard;
import pa1.model.Gem;
import pa1.model.Mine;
import pa1.model.MoveResult;
import pa1.model.Position;
import pa1.model.StopCell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller for {@link GameBoard}.
 *
 * <p>
 * This class is responsible for providing high-level operations to mutate a
 * {@link GameBoard}.
 * </p>
 */
public class GameBoardController {

    @NotNull
    private final GameBoard gameBoard;

    /**
     * Creates an instance.
     *
     * @param gameBoard the game board to control
     */
    public GameBoardController(@NotNull final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    /**
     * Moves the player in the given direction.
     *
     * @param direction direction in which to move
     * @return the result of the attempted move
     */
    @NotNull
    public MoveResult makeMove(@NotNull final Direction direction) {
        Objects.requireNonNull(direction);

        final EntityCell playerCell =
                Objects.requireNonNull(gameBoard.getPlayer().getOwner());
        final MoveResult moveResult =
                tryMove(playerCell.getPosition(), direction);

        if (!(moveResult instanceof MoveResult.Valid.Alive aliveMove)) {
            return moveResult;
        }

        final EntityCell originalCell =
                gameBoard.getEntityCell(aliveMove.origPosition);
        final EntityCell destinationCell =
                gameBoard.getEntityCell(aliveMove.newPosition);

        for (final Position gemPosition : aliveMove.collectedGems) {
            gameBoard.getEntityCell(gemPosition).setEntity(null);
        }

        for (final Position extraLifePosition : aliveMove.collectedExtraLives) {
            gameBoard.getEntityCell(extraLifePosition).setEntity(null);
        }

        originalCell.setEntity(null);
        destinationCell.setEntity(gameBoard.getPlayer());

        return moveResult;
    }

    /**
     * Undoes a previous move.
     *
     * @param prevMove the move to undo
     */
    public void undoMove(@NotNull final MoveResult prevMove) {
        Objects.requireNonNull(prevMove);

        if (!(prevMove instanceof MoveResult.Valid.Alive aliveMove)) {
            return;
        }

        final EntityCell currentCell =
                gameBoard.getEntityCell(aliveMove.newPosition);
        final EntityCell originalCell =
                gameBoard.getEntityCell(aliveMove.origPosition);

        currentCell.setEntity(null);
        originalCell.setEntity(gameBoard.getPlayer());

        for (final Position gemPosition : aliveMove.collectedGems) {
            gameBoard.getEntityCell(gemPosition).setEntity(new Gem());
        }

        for (final Position extraLifePosition : aliveMove.collectedExtraLives) {
            gameBoard.getEntityCell(extraLifePosition).setEntity(new ExtraLife());
        }
    }

    /**
     * Attempts to move the player as far as possible without mutating the board.
     *
     * @param position original player position
     * @param direction direction in which to move
     * @return the result of the attempted move
     */
    @NotNull
    private MoveResult tryMove(
            @NotNull final Position position,
            @NotNull final Direction direction
    ) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final var collectedGems = new ArrayList<Position>();
        final var collectedExtraLives = new ArrayList<Position>();
        Position lastValidPosition = position;

        while (true) {
            final Position newPosition = offsetPosition(lastValidPosition, direction);
            if (newPosition == null) {
                break;
            }

            lastValidPosition = newPosition;
            final Cell cell = gameBoard.getCell(newPosition);

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
        }

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
     * Gets the next position if it is inside the board and contains an entity cell.
     *
     * @param position current position
     * @param direction movement direction
     * @return the next valid position, or {@code null}
     */
    @Nullable
    private Position offsetPosition(
            @NotNull final Position position,
            @NotNull final Direction direction
    ) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final Position newPosition = position.offsetByOrNull(
                direction.getOffset(),
                gameBoard.getNumRows(),
                gameBoard.getNumCols()
        );

        if (newPosition == null) {
            return null;
        }

        if (!(gameBoard.getCell(newPosition) instanceof EntityCell)) {
            return null;
        }

        return newPosition;
    }
}