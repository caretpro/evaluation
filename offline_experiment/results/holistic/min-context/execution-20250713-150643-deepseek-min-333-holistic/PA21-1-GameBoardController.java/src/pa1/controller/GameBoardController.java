
package pa1.controller;

import pa1.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class GameBoardController {
    @NotNull
    private final GameBoard gameBoard;

    public GameBoardController(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    public MoveResult makeMove(final Direction direction) {
        Objects.requireNonNull(direction);
        
        Position currentPosition = gameBoard.getPlayer().getPosition();
        MoveResult result = tryMove(currentPosition, direction);
        
        if (result instanceof MoveResult.Valid.Alive aliveResult) {
            // Update player position
            gameBoard.getPlayer().setPosition(aliveResult.getNewPosition());
            
            // Remove collected gems
            for (Position gemPos : aliveResult.getCollectedGems()) {
                gameBoard.setCell(gemPos.getRow(), gemPos.getColumn(), new EntityCell(null));
            }
            
            // Remove collected extra lives
            for (Position lifePos : aliveResult.getCollectedExtraLives()) {
                gameBoard.setCell(lifePos.getRow(), lifePos.getColumn(), new EntityCell(null));
            }
            
            return aliveResult;
        }
        return result;
    }

    public void undoMove(final MoveResult prevMove) {
        Objects.requireNonNull(prevMove);
        
        if (prevMove instanceof MoveResult.Valid.Alive aliveResult) {
            // Revert player position
            gameBoard.getPlayer().setPosition(aliveResult.getOriginalPosition());
            
            // Restore gems
            for (Position gemPos : aliveResult.getCollectedGems()) {
                gameBoard.setCell(gemPos.getRow(), gemPos.getColumn(), new EntityCell(new Gem()));
            }
            
            // Restore extra lives
            for (Position lifePos : aliveResult.getCollectedExtraLives()) {
                gameBoard.setCell(lifePos.getRow(), lifePos.getColumn(), new EntityCell(new ExtraLife()));
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

            if (gameBoard.getCell(newPosition.getRow(), newPosition.getColumn()) instanceof StopCell) {
                break;
            }

            if (gameBoard.getCell(newPosition.getRow(), newPosition.getColumn()) instanceof EntityCell entityCell) {
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

        final var newPos = position.offsetByOrNull(direction.getOffset(), gameBoard.getNumRows(), gameBoard.getNumCols());

        if (newPos == null) {
            return null;
        }
        if (!(gameBoard.getCell(newPos.getRow(), newPos.getColumn()) instanceof EntityCell)) {
            return null;
        }

        return newPos;
    }
}