
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pa1.model.*;

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

        // Capture starting state
        Position startPos = gameBoard.getPlayer().getPosition();
        int startLives    = gameBoard.getNumLives();

        MoveResult result = tryMove(startPos, direction);

        if (result instanceof MoveResult.Valid valid) {
            // Remove player from old cell by simply moving its position later
            // Remove any collected entities
            for (Position gemPos : valid.collectedGems) {
                gameBoard.removeEntity(gemPos);
            }
            for (Position lifePos : valid.collectedExtraLives) {
                gameBoard.removeEntity(lifePos);
            }

            // Move player object
            gameBoard.getPlayer().setPosition(valid.newPosition);

            // If dead, decrement lives count
            if (valid instanceof MoveResult.Valid.Dead) {
                gameBoard.setNumLives(startLives - 1);
            }
        }

        return result;
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

        if (prevMove instanceof MoveResult.Valid valid) {
            // Restore player position
            gameBoard.getPlayer().setPosition(valid.oldPosition);

            // Restore collected gems and extra lives
            for (Position gemPos : valid.collectedGems) {
                gameBoard.addEntity(gemPos, new Gem());
            }
            for (Position lifePos : valid.collectedExtraLives) {
                gameBoard.addEntity(lifePos, new ExtraLife());
            }

            // If died this move, restore life count
            if (valid instanceof MoveResult.Valid.Dead) {
                gameBoard.setNumLives(gameBoard.getNumLives() + 1);
            }
        }
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

        List<Position> collectedGems = new ArrayList<>();
        List<Position> collectedExtraLives = new ArrayList<>();
        Position lastValidPosition = position;

        while (true) {
            Position newPosition = offsetPosition(lastValidPosition, direction);
            if (newPosition == null) {
                break;
            }
            lastValidPosition = newPosition;

            if (gameBoard.getCell(newPosition) instanceof StopCell) {
                break;
            }
            if (gameBoard.getCell(newPosition) instanceof EntityCell ec) {
                Entity e = ec.getEntity();
                if (e instanceof Mine) {
                    return new MoveResult.Valid.Dead(position, newPosition, collectedGems, collectedExtraLives);
                }
                if (e instanceof Gem) {
                    collectedGems.add(newPosition);
                } else if (e instanceof ExtraLife) {
                    collectedExtraLives.add(newPosition);
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
    private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        Position newPos = position.offsetByOrNull(direction.getOffset(),
                gameBoard.getNumRows(), gameBoard.getNumCols());
        if (newPos == null || !(gameBoard.getCell(newPos) instanceof EntityCell)) {
            return null;
        }
        return newPos;
    }
}