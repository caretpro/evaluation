
package pa1.controller;

import pa1.model.Cell;
import pa1.model.Direction;
import pa1.model.EntityCell;
import pa1.model.ExtraLife;
import pa1.model.GameBoard;
import pa1.model.Gem;
import pa1.model.MoveResult;
import pa1.model.Mine;
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
        Objects.requireNonNull(gameBoard, "gameBoard must not be null");
        this.gameBoard = gameBoard;
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
        Objects.requireNonNull(direction, "direction must not be null");

        // 1) Compute outcome without mutating the board.
        MoveResult result = tryMove(gameBoard.getPlayerPosition(), direction);

        // 2) If invalid or dead, do nothing.
        if (!(result instanceof MoveResult.Valid.Alive validAlive)) {
            return result;
        }

        // 3) Apply the valid & alive move:

        // a) Move the player
        gameBoard.setCell(validAlive.prevPosition(), Cell.floor());
        gameBoard.setCell(validAlive.newPosition(), Cell.player());

        // b) Clear collected gems and extra lives
        for (Position gemPos : validAlive.collectedGems()) {
            gameBoard.setCell(gemPos, Cell.floor());
        }
        for (Position lifePos : validAlive.collectedExtraLives()) {
            gameBoard.setCell(lifePos, Cell.floor());
        }

        // c) Update score and lives
        gameBoard.incrementGems(validAlive.collectedGems().size());
        gameBoard.incrementLives(validAlive.collectedExtraLives().size());

        return validAlive;
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
        Objects.requireNonNull(prevMove, "prevMove must not be null");

        if (!(prevMove instanceof MoveResult.Valid.Alive validAlive)) {
            return;
        }

        // a) Move player back
        gameBoard.setCell(validAlive.newPosition(), Cell.floor());
        gameBoard.setCell(validAlive.prevPosition(), Cell.player());

        // b) Restore collected gems and extra lives
        for (Position gemPos : validAlive.collectedGems()) {
            gameBoard.setCell(gemPos, Cell.gem());
        }
        for (Position lifePos : validAlive.collectedExtraLives()) {
            gameBoard.setCell(lifePos, Cell.extraLife());
        }

        // c) Revert counters
        gameBoard.incrementGems(-validAlive.collectedGems().size());
        gameBoard.incrementLives(-validAlive.collectedExtraLives().size());
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

        var collectedGems = new ArrayList<Position>();
        var collectedExtraLives = new ArrayList<Position>();
        Position lastValidPosition = position;

        while (true) {
            Position next = offsetPosition(lastValidPosition, direction);
            if (next == null) {
                break;
            }
            lastValidPosition = next;

            if (gameBoard.getCell(next) instanceof StopCell) {
                break;
            }
            if (gameBoard.getCell(next) instanceof EntityCell ec) {
                if (ec.getEntity() instanceof Mine) {
                    return new MoveResult.Valid.Dead(position, next);
                }
                if (ec.getEntity() instanceof Gem) {
                    collectedGems.add(next);
                } else if (ec.getEntity() instanceof ExtraLife) {
                    collectedExtraLives.add(next);
                }
            }
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
    private Position offsetPosition(@NotNull final Position position,
                                    @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        Position newPos = position.offsetByOrNull(direction.getOffset(),
                                                  gameBoard.getNumRows(),
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