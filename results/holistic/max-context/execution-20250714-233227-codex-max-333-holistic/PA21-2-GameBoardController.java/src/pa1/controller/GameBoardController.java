
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

        // 1) Record original player position
        Position origPos = gameBoard.getPlayer().getOwner().getPosition();
        // 2) Compute the move result without mutating
        MoveResult result = tryMove(origPos, direction);

        // If invalid move or dead, do not mutate the board
        if (result instanceof MoveResult.Invalid || result instanceof MoveResult.Valid.Dead) {
            return result;
        }

        // At this point we have a valid & alive move
        MoveResult.Valid.Alive alive = (MoveResult.Valid.Alive) result;
        Position newPos = alive.newPosition;

        // 3) Remove player from original cell
        gameBoard.getEntityCell(origPos).setEntity(null);

        // 4) Collect gems / extra lives on their cells
        for (Position gemPos : alive.collectedGems) {
            gameBoard.getEntityCell(gemPos).setEntity(null);
        }
        for (Position lifePos : alive.collectedExtraLives) {
            gameBoard.getEntityCell(lifePos).setEntity(null);
        }

        // 5) Move player: update its position on the Player object, then place it on the new cell
        gameBoard.getPlayer().getOwner().setPosition(newPos);
        gameBoard.getEntityCell(newPos).setEntity(gameBoard.getPlayer());

        return alive;
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

        // Invalid moves did not change the board at all
        if (prevMove instanceof MoveResult.Invalid) {
            return;
        }

        // Reverse a valid & alive move
        if (prevMove instanceof MoveResult.Valid.Alive alive) {
            // 1) Remove player from its current cell
            Position curr = alive.newPosition;
            gameBoard.getEntityCell(curr).setEntity(null);

            // 2) Restore any picked‑up gems
            for (Position gemPos : alive.collectedGems) {
                gameBoard.getEntityCell(gemPos).setEntity(new Gem());
            }
            // 3) Restore any picked‑up extra lives
            for (Position lifePos : alive.collectedExtraLives) {
                gameBoard.getEntityCell(lifePos).setEntity(new ExtraLife());
            }

            // 4) Move player back to original cell (on the Player object) and place on board
            Position orig = alive.origPosition;
            gameBoard.getPlayer().getOwner().setPosition(orig);
            gameBoard.getEntityCell(orig).setEntity(gameBoard.getPlayer());
        }
        // Dead moves never mutated the board—only restore player back to its spot
        else if (prevMove instanceof MoveResult.Valid.Dead dead) {
            Position deadPos = dead.newPosition;
            gameBoard.getPlayer().getOwner().setPosition(deadPos);
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

        var collectedGems = new ArrayList<Position>();
        var collectedExtraLives = new ArrayList<Position>();
        Position lastValidPosition = position;

        do {
            Position newPosition = offsetPosition(lastValidPosition, direction);
            if (newPosition == null) {
                break;
            }
            lastValidPosition = newPosition;

            // stop at non‑entity stopping cells
            if (gameBoard.getCell(newPosition) instanceof StopCell) {
                break;
            }

            // record any collisions
            if (gameBoard.getCell(newPosition) instanceof EntityCell ec) {
                if (ec.getEntity() instanceof Mine) {
                    return new MoveResult.Valid.Dead(position, newPosition);
                }
                if (ec.getEntity() instanceof Gem) {
                    collectedGems.add(newPosition);
                } else if (ec.getEntity() instanceof ExtraLife) {
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
     * board, or contains a non‑{@link EntityCell}, returns {@code null}.
     */
    @Nullable
    private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        Position newPos = position.offsetByOrNull(direction.getOffset(),
                                                  gameBoard.getNumRows(),
                                                  gameBoard.getNumCols());
        if (newPos == null || !(gameBoard.getCell(newPos) instanceof EntityCell)) {
            return null;
        }
        return newPos;
    }
}