
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pa1.model.Cell;
import pa1.model.Direction;
import pa1.model.EntityCell;
import pa1.model.GameBoard;
import pa1.model.Gem;
import pa1.model.Mine;
import pa1.model.MoveResult;
import pa1.model.Player;
import pa1.model.Position;
import pa1.model.ExtraLife;
import pa1.model.StopCell;

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
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard must not be null");
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
        // capture current player position
        Position start = gameBoard.player();
        // simulate no-side‑effects move
        MoveResult result = tryMove(start, direction);
        if (!(result instanceof MoveResult.Valid)) {
            // invalid or dead ⇒ do not mutate
            return result;
        }
        MoveResult.Valid.Alive alive = (MoveResult.Valid.Alive) result;

        // 1) clear player at old position
        gameBoard.setCell(start, Cell.free());

        // 2) clear collected gems and extra lives
        for (Position gemPos : alive.gems()) {
            gameBoard.setCell(gemPos, Cell.free());
        }
        for (Position lifePos : alive.extraLives()) {
            gameBoard.setCell(lifePos, Cell.free());
        }

        // 3) place player at new position
        Position target = alive.newPosition();
        gameBoard.setCell(target, new EntityCell(new Player()));
        gameBoard.setPlayer(target);

        // 4) update score and lives on the board
        gameBoard.addScore(alive.gems().size());
        gameBoard.addLives(alive.extraLives().size());

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
        Objects.requireNonNull(prevMove, "prevMove must not be null");
        if (!(prevMove instanceof MoveResult.Valid.Alive alive)) {
            // nothing to undo for invalid or dead moves
            return;
        }

        Position before = alive.oldPosition();
        Position after  = alive.newPosition();

        // 1) remove player from the moved-to cell
        gameBoard.setCell(after, Cell.free());

        // 2) restore gems and extra lives
        for (Position gemPos : alive.gems()) {
            gameBoard.setCell(gemPos, new EntityCell(new Gem()));
        }
        for (Position lifePos : alive.extraLives()) {
            gameBoard.setCell(lifePos, new EntityCell(new ExtraLife()));
        }

        // 3) put player back to original cell
        gameBoard.setCell(before, new EntityCell(new Player()));
        gameBoard.setPlayer(before);

        // 4) revert score and lives
        gameBoard.addScore(-alive.gems().size());
        gameBoard.addLives(-alive.extraLives().size());
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
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(direction, "direction must not be null");

        List<Position> gems = new ArrayList<>();
        List<Position> extraLives = new ArrayList<>();
        Position current = position;

        while (true) {
            Position next = offsetPosition(current, direction);
            if (next == null) {
                break;
            }
            current = next;

            // stop if we hit a StopCell
            if (gameBoard.getCell(current) instanceof StopCell) {
                break;
            }

            // check mines, gems, lives
            if (gameBoard.getCell(current) instanceof EntityCell ec) {
                if (ec.getEntity() instanceof Mine) {
                    return new MoveResult.Valid.Dead(position, current);
                }
                if (ec.getEntity() instanceof Gem) {
                    gems.add(current);
                } else if (ec.getEntity() instanceof ExtraLife) {
                    extraLives.add(current);
                }
            }
        }

        if (current.equals(position)) {
            return new MoveResult.Invalid(position);
        }
        return new MoveResult.Valid.Alive(current, position, gems, extraLives);
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
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(direction, "direction must not be null");

        Position np = position.offsetBy(direction.getOffset(),
                                       gameBoard.numRows(),
                                       gameBoard.numCols());
        if (np == null || !(gameBoard.getCell(np) instanceof EntityCell)) {
            return null;
        }
        return np;
    }
}