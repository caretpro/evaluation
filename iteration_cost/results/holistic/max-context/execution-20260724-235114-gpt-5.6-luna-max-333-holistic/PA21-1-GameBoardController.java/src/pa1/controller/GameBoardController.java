
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pa1.model.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public class GameBoardController {

    @NotNull
    private final GameBoard gameBoard;

    @NotNull
    private final Map<MoveResult, BoardSnapshot> snapshots = new IdentityHashMap<>();

    public GameBoardController(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public MoveResult makeMove(final Direction direction) {
        Objects.requireNonNull(direction);

        final Position currentPosition =
                Objects.requireNonNull(gameBoard.getPlayer().getOwner()).getPosition();

        final MoveResult result = tryMove(currentPosition, direction);

        if (!(result instanceof MoveResult.Valid.Alive alive)) {
            return result;
        }

        final BoardSnapshot snapshot = new BoardSnapshot(
                gameBoard,
                alive.collectedExtraLives.size()
        );

        final EntityCell playerCell = gameBoard.getPlayer().getOwner();

        replaceCell(alive.origPosition, createEmptyCell());

        for (final Position position : alive.collectedGems) {
            replaceCell(position, createEmptyCell());
        }

        for (final Position position : alive.collectedExtraLives) {
            replaceCell(position, createEmptyCell());
        }

        replaceCell(alive.newPosition, playerCell);

        currentPosition.row(alive.newPosition.row());
        currentPosition.col(alive.newPosition.col());

        for (int i = 0; i < alive.collectedExtraLives.size(); ++i) {
            changeLives(1);
        }

        snapshots.put(result, snapshot);
        return result;
    }

    public void undoMove(final MoveResult prevMove) {
        Objects.requireNonNull(prevMove);

        final BoardSnapshot snapshot = snapshots.remove(prevMove);
        if (snapshot == null) {
            return;
        }

        snapshot.restore(gameBoard);

        final Position playerPosition =
                Objects.requireNonNull(gameBoard.getPlayer().getOwner()).getPosition();

        playerPosition.row(snapshot.playerRow);
        playerPosition.col(snapshot.playerCol);

        for (int i = 0; i < snapshot.extraLivesCollected; ++i) {
            changeLives(-1);
        }
    }

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
                    return new MoveResult.Valid.Dead(
                            copyPosition(position),
                            copyPosition(newPosition)
                    );
                }

                if (entityCell.getEntity() instanceof Gem) {
                    collectedGems.add(copyPosition(newPosition));
                } else if (entityCell.getEntity() instanceof ExtraLife) {
                    collectedExtraLives.add(copyPosition(newPosition));
                }
            }
        } while (true);

        if (lastValidPosition.equals(position)) {
            return new MoveResult.Invalid(copyPosition(position));
        }

        return new MoveResult.Valid.Alive(
                copyPosition(lastValidPosition),
                copyPosition(position),
                collectedGems,
                collectedExtraLives
        );
    }

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

    private void replaceCell(
            @NotNull final Position position,
            @NotNull final Cell cell
    ) {
        gameBoard.getRow(position.row())[position.col()] = cell;
    }

    @NotNull
    private Cell createEmptyCell() {
        return new EntityCell(null);
    }

    private void changeLives(final int amount) {
        if (amount == 0) {
            return;
        }

        final Player player = gameBoard.getPlayer();
        final String primaryMethodName = amount > 0 ? "addLife" : "removeLife";

        try {
            final Method method = Player.class.getMethod(primaryMethodName);

            for (int i = 0; i < Math.abs(amount); ++i) {
                method.invoke(player);
            }

            return;
        } catch (ReflectiveOperationException ignored) {
        }

        final String alternativeMethodName =
                amount > 0 ? "incrementLives" : "decrementLives";

        try {
            final Method method = Player.class.getMethod(alternativeMethodName);

            for (int i = 0; i < Math.abs(amount); ++i) {
                method.invoke(player);
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Unable to update the player's lives.",
                    exception
            );
        }
    }

    @NotNull
    private static Position copyPosition(@NotNull final Position position) {
        return new Position(position.row(), position.col());
    }

    private static final class BoardSnapshot {

        @NotNull
        private final Cell[][] cells;

        private final int playerRow;
        private final int playerCol;
        private final int extraLivesCollected;

        private BoardSnapshot(
                @NotNull final GameBoard gameBoard,
                final int extraLivesCollected
        ) {
            this.cells = new Cell[gameBoard.getNumRows()][gameBoard.getNumCols()];

            for (int row = 0; row < gameBoard.getNumRows(); ++row) {
                System.arraycopy(
                        gameBoard.getRow(row),
                        0,
                        cells[row],
                        0,
                        gameBoard.getNumCols()
                );
            }

            final Position playerPosition =
                    Objects.requireNonNull(gameBoard.getPlayer().getOwner()).getPosition();

            this.playerRow = playerPosition.row();
            this.playerCol = playerPosition.col();
            this.extraLivesCollected = extraLivesCollected;
        }

        private void restore(@NotNull final GameBoard gameBoard) {
            for (int row = 0; row < gameBoard.getNumRows(); ++row) {
                System.arraycopy(
                        cells[row],
                        0,
                        gameBoard.getRow(row),
                        0,
                        gameBoard.getNumCols()
                );
            }
        }
    }
}