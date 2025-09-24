
package pa1.controller;

import pa1.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller for {@link GameBoard}.
 */
public class GameBoardController {

    @NotNull
    private final GameBoard gameBoard;

    public GameBoardController(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public MoveResult makeMove(final Direction direction) {
        final Position currentPos = gameBoard.getPlayer().getPosition();
        final MoveResult result = tryMove(currentPos, direction);

        if (result instanceof MoveResult.Valid.Alive aliveResult) {
            // Update player position
            gameBoard.getPlayer().setPosition(aliveResult.newPosition);
            
            // Remove collected gems and extra lives
            for (Position gemPos : aliveResult.collectedGems) {
                ((EntityCell) gameBoard.getCell(gemPos)).setEntity(null);
            }
            for (Position lifePos : aliveResult.collectedExtraLives) {
                ((EntityCell) gameBoard.getCell(lifePos)).setEntity(null);
            }
        }

        return result;
    }

    public void undoMove(final MoveResult prevMove) {
        if (prevMove instanceof MoveResult.Valid.Alive aliveResult) {
            // Restore player position
            gameBoard.getPlayer().setPosition(aliveResult.origPosition);
            
            // Restore collected gems
            for (Position gemPos : aliveResult.collectedGems) {
                ((EntityCell) gameBoard.getCell(gemPos)).setEntity(new Gem());
            }
            
            // Restore collected extra lives
            for (Position lifePos : aliveResult.collectedExtraLives) {
                ((EntityCell) gameBoard.getCell(lifePos)).setEntity(new ExtraLife());
            }
        }
    }

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

        return new MoveResult.Valid.Alive(lastValidPosition, position, collectedGems, collectedExtraLives);
    }

    @Nullable
    private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final var newPos = position.offsetByOrNull(direction.getOffset(), gameBoard.getNumRows(),
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