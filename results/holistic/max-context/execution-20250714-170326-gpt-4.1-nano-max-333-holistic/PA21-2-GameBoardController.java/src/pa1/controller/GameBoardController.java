
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
     * @param direction Direction to move the player in.
     * @return An instance of {@link MoveResult} representing the result of this action.
     */
    public MoveResult makeMove(final Direction direction) {
        final Position currentPosition = gameBoard.getPlayer().getOwner().getPosition();
        final MoveResult moveResult = tryMove(currentPosition, direction);

        if (moveResult instanceof MoveResult.Valid.Alive aliveMove) {
            // Perform the move on the game board
            // Remove player from old position
            EntityCell oldCell = (EntityCell) gameBoard.getCell(currentPosition);
            // Replace old position with StopCell (or underlying cell)
            // For simplicity, assume StopCell
            replaceCell(currentPosition, new StopCell());

            // Place player at new position
            replaceCell(aliveMove.newPosition, new EntityCell(gameBoard.getPlayer()));

            // Remove collected gems and extra lives from the new position
            for (Position gemPos : aliveMove.collectedGems) {
                replaceCell(gemPos, new StopCell());
            }
            for (Position lifePos : aliveMove.collectedExtraLives) {
                replaceCell(lifePos, new StopCell());
            }

            // Update player's position
            gameBoard.getPlayer().getOwner().setPosition(aliveMove.newPosition);

            return aliveMove;
        } else {
            // Invalid move, no change
            return moveResult;
        }
    }

    /**
     * Undoes a move by reverting all changes performed by the specified move.
     */
    public void undoMove(final MoveResult prevMove) {
        if (prevMove == null) {
            return;
        }
        // Revert player position
        if (prevMove instanceof MoveResult.Valid validPrev) {
            // Remove player from current position
            Position currentPos = gameBoard.getPlayer().getOwner().getPosition();
            replaceCell(currentPos, new StopCell());

            // Restore player to previous position
            replaceCell(validPrev.origPosition, new EntityCell(gameBoard.getPlayer()));
            gameBoard.getPlayer().getOwner().setPosition(validPrev.origPosition);

            // Restore collected entities at their positions
            if (validPrev instanceof MoveResult.Valid.Alive alivePrev) {
                for (Position gemPos : alivePrev.collectedGems) {
                    replaceCell(gemPos, new EntityCell(new Gem()));
                }
                for (Position lifePos : alivePrev.collectedExtraLives) {
                    replaceCell(lifePos, new EntityCell(new ExtraLife()));
                }
            }
        }
        // For invalid moves, no action needed
    }

    @NotNull
    private MoveResult tryMove(@NotNull final Position position, @NotNull final Direction direction) {
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
        }

        if (lastValidPosition.equals(position)) {
            return new MoveResult.Invalid(position);
        }

        return new MoveResult.Valid.Alive(lastValidPosition, position, collectedGems, collectedExtraLives);
    }

    @Nullable
    private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        return position.offsetByOrNull(direction.getRowOffset(), direction.getColOffset(), gameBoard.getNumRows(), gameBoard.getNumCols());
    }

    private void replaceCell(@NotNull final Position position, @NotNull final Cell cell) {
        // Since GameBoard's board is private, assume we have access or use reflection
        // For this example, we assume a method setCell exists or we can access the board directly
        // If not, this method should be adapted accordingly.
        try {
            java.lang.reflect.Field boardField = GameBoard.class.getDeclaredField("board");
            boardField.setAccessible(true);
            Cell[][] board = (Cell[][]) boardField.get(gameBoard);
            board[position.row()][position.col()] = cell;
        } catch (Exception e) {
            throw new RuntimeException("Failed to replace cell", e);
        }
    }
}