
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
        
        Position playerPosition = gameBoard.getPlayer().getOwner().getPosition();
        MoveResult result = tryMove(playerPosition, direction);
        
        if (result instanceof MoveResult.Valid.Alive aliveResult) {
            EntityCell playerCell = gameBoard.getEntityCell(playerPosition);
            Player player = (Player) playerCell.getEntity();
            playerCell.setEntity(null);
            
            EntityCell newCell = gameBoard.getEntityCell(aliveResult.newPosition);
            newCell.setEntity(player);
            player.setOwner(newCell);
            
            for (Position gemPos : aliveResult.collectedGems) {
                EntityCell gemCell = gameBoard.getEntityCell(gemPos);
                gemCell.setEntity(null);
            }
            
            for (Position lifePos : aliveResult.collectedExtraLives) {
                EntityCell lifeCell = gameBoard.getEntityCell(lifePos);
                lifeCell.setEntity(null);
                player.setNumLives(player.getNumLives() + 1);
            }
        }
        
        return result;
    }

    public void undoMove(final MoveResult prevMove) {
        if (prevMove instanceof MoveResult.Valid.Alive aliveResult) {
            EntityCell currentCell = gameBoard.getEntityCell(aliveResult.newPosition);
            Player player = (Player) currentCell.getEntity();
            currentCell.setEntity(null);
            
            EntityCell originalCell = gameBoard.getEntityCell(aliveResult.origPosition);
            originalCell.setEntity(player);
            player.setOwner(originalCell);
            
            for (Position gemPos : aliveResult.collectedGems) {
                EntityCell gemCell = gameBoard.getEntityCell(gemPos);
                gemCell.setEntity(new Gem());
            }
            
            for (Position lifePos : aliveResult.collectedExtraLives) {
                EntityCell lifeCell = gameBoard.getEntityCell(lifePos);
                lifeCell.setEntity(new ExtraLife());
                player.setNumLives(player.getNumLives() - 1);
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