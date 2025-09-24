
package pa1.controller;

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Controller for {@link GameBoard}.
 *
 * <p>
 * This class is responsible for providing high‑level operations to mutate a {@link GameBoard}. This should be the only
 * class which mutates the game board; other classes should use this controller to perform all board mutations.
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
     * The board is only mutated if the move is valid and the player remains alive. If the move is invalid or
     * the player dies along the way, the board is left unchanged.
     * </p>
     *
     * @param direction Direction to move the player in.
     * @return A {@link MoveResult} describing what happened.
     */
    public MoveResult makeMove(final Direction direction) {
        Objects.requireNonNull(direction, "direction must not be null");

        // 1. Compute move result without side‐effects
        final Position start = gameBoard.player();
        MoveResult result = tryMove(start, direction);

        // 2. If not a live valid move, return immediately (no mutation)
        if (!(result instanceof MoveResult.Valid.Alive alive)) {
            return result;
        }

        // 3. Commit the valid alive move
        Position dest = alive.destination();
        List<Position> gems   = alive.gemsCollected();
        List<Position> lives  = alive.extraLivesCollected();

        // Remove each collected gem and extra life
        for (Position gemPos : gems) {
            gameBoard.setEmpty(gemPos);
        }
        for (Position lifePos : lives) {
            gameBoard.setEmpty(lifePos);
        }

        // Move the player cell
        gameBoard.setEmpty(start);
        gameBoard.setPlayer(dest);

        return alive;
    }

    /**
     * Undoes a previously applied move.
     *
     * <p>
     * Effectively reverses the steps taken by {@link #makeMove(Direction)} for a live move.
     * </p>
     *
     * @param prevMove The {@link MoveResult} to undo.
     */
    public void undoMove(final MoveResult prevMove) {
        Objects.requireNonNull(prevMove, "prevMove must not be null");

        // Only live valid moves have board mutations to revert
        if (prevMove instanceof MoveResult.Valid.Alive alive) {
            Position origin = alive.origin();
            Position dest   = alive.destination();
            List<Position> gems  = alive.gemsCollected();
            List<Position> lives = alive.extraLivesCollected();

            // Move player back
            gameBoard.setEmpty(dest);
            gameBoard.setPlayer(origin);

            // Restore extra lives then gems
            for (Position lifePos : lives) {
                gameBoard.setEntity(lifePos, new ExtraLife());
            }
            for (Position gemPos : gems) {
                gameBoard.setEntity(gemPos, new Gem());
            }
        }
        // Invalid or dead moves made no changes, so nothing to undo.
    }

    /**
     * Computes the result of moving from {@code position} in {@code direction}, without mutating the board.
     *
     * @param position  The starting position.
     * @param direction The intended move direction.
     * @return A {@link MoveResult} describing where (and whether) the player stops or dies.
     */
    @NotNull
    private MoveResult tryMove(@NotNull final Position position,
                               @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        List<Position> collectedGems       = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position lastValid = position;

        // Slide step by step until we hit a block, StopCell, or Mine
        while (true) {
            Position next = offsetPosition(lastValid, direction);
            if (next == null) {
                break;
            }
            lastValid = next;

            // Stop‐cell stops the slide
            if (gameBoard.cell(next) instanceof StopCell) {
                break;
            }

            // EntityCell might be Gem, ExtraLife, or Mine
            if (gameBoard.cell(next) instanceof EntityCell ec) {
                if (ec.getEntity() instanceof Mine) {
                    // stepping on a mine → dead
                    return new MoveResult.Valid.Dead(position, next);
                }
                if (ec.getEntity() instanceof Gem) {
                    collectedGems.add(next);
                } else if (ec.getEntity() instanceof ExtraLife) {
                    collectedExtraLives.add(next);
                }
            }
        }

        // No movement at all → invalid
        if (lastValid.equals(position)) {
            return new MoveResult.Invalid(position);
        }

        // Valid and alive → return positions and collected items
        return new MoveResult.Valid.Alive(
                lastValid,
                position,
                Collections.unmodifiableList(collectedGems),
                Collections.unmodifiableList(collectedExtraLives)
        );
    }

    /**
     * Steps one cell from {@code position} in {@code direction}, if possible.
     *
     * @param position  The current position.
     * @param direction The direction to step.
     * @return The adjacent position if it is inside the board and on an {@link EntityCell}; otherwise {@code null}.
     */
    @Nullable
    private Position offsetPosition(@NotNull final Position position,
                                    @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        Position next = position.offsetByOrNull(
                direction.getOffset(),
                gameBoard.numRows(),
                gameBoard.numCols()
        );
        if (next == null || !(gameBoard.cell(next) instanceof EntityCell)) {
            return null;
        }
        return next;
    }
}