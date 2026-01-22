
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
        final var player = gameBoard.getPlayer();
        final var playerCell = player.getOwner();
        final var currentPos = playerCell.getPosition();
        
        final var result = tryMove(currentPos, direction);
        
        if (result instanceof MoveResult.Valid.Alive aliveResult) {
            // Update player position by updating the EntityCell's position
            playerCell.setPosition(aliveResult.newPosition);
            
            // Remove collected gems
            for (var gemPos : aliveResult.collectedGems) {
                gameBoard.getEntityCell(gemPos).setEntity(null);
            }
            
            // Remove collected extra lives
            for (var lifePos : aliveResult.collectedExtraLives) {
                gameBoard.getEntityCell(lifePos).setEntity(null);
            }
        }
        
        return result;
    }

    public void undoMove(final MoveResult prevMove) {
        if (prevMove instanceof MoveResult.Valid.Alive aliveResult) {
            // Restore player position by updating the EntityCell's position
            gameBoard.getPlayer().getOwner().setPosition(aliveResult.origPosition);
            
            // Restore collected gems
            for (var gemPos : aliveResult.collectedGems) {
                gameBoard.getEntityCell(gemPos).setEntity(new Gem());
            }
            
            // Restore collected extra lives
            for (var lifePos : aliveResult.collectedExtraLives) {
                gameBoard.getEntityCell(lifePos).setEntity(new ExtraLife());
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