
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

        // 1) compute the move result without mutating the board
        final Position origPos = gameBoard.getPlayer().getOwner().getPosition();
        final MoveResult result = tryMove(origPos, direction);

        // 2) apply changes only if move was valid and the player remains alive
        if (result instanceof MoveResult.Valid.Alive alive) {
            // Move the player entity
            gameBoard.getEntityCell(alive.origPosition).setEntity(null);
            gameBoard.getEntityCell(alive.newPosition).setEntity(gameBoard.getPlayer());

            // Collect gems
            for (Position gemPos : alive.collectedGems) {
                gameBoard.getEntityCell(gemPos).setEntity(null);
            }

            // Collect extra lives
            if (!alive.collectedExtraLives.isEmpty()) {
                Player p = gameBoard.getPlayer();
                int lives = p.getNumLives();
                for (Position lifePos : alive.collectedExtraLives) {
                    gameBoard.getEntityCell(lifePos).setEntity(null);
                    lives++;
                }
                p.setNumLives(lives);
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
        Objects.requireNonNull(prevMove);

        // Only undo successful "alive" moves
        if (!(prevMove instanceof MoveResult.Valid.Alive alive)) {
            return;
        }

        // Revert extra lives
        if (!alive.collectedExtraLives.isEmpty()) {
            Player p = gameBoard.getPlayer();
            int lives = p.getNumLives();
            for (Position lifePos : alive.collectedExtraLives) {
                gameBoard.getEntityCell(lifePos).setEntity(new ExtraLife());
                lives--;
            }
            p.setNumLives(lives);
        }

        // Revert gems
        for (Position gemPos : alive.collectedGems) {
            gameBoard.getEntityCell(gemPos).setEntity(new Gem());
        }

        // Move player back
        gameBoard.getEntityCell(alive.newPosition).setEntity(null);
        gameBoard.getEntityCell(alive.origPosition).setEntity(gameBoard.getPlayer());
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
     * @return An instance of {@link MoveResult} representing the result of the move.
     */
    @NotNull
    private MoveResult tryMove(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final List<Position> collectedGems = new ArrayList<>();
        final List<Position> collectedExtraLives = new ArrayList<>();
        Position lastValidPosition = position;

        while (true) {
            @Nullable Position newPosition = offsetPosition(lastValidPosition, direction);
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

        Position newPos = position.offsetByOrNull(
                direction.getOffset().dRow(),
                direction.getOffset().dCol(),
                gameBoard.getNumRows(),
                gameBoard.getNumCols()
        );

        if (newPos == null || !(gameBoard.getCell(newPos) instanceof EntityCell)) {
            return null;
        }
        return newPos;
    }
}