
package assignment.game;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.actions.ActionResult;
import assignment.utils.ShouldNotReachException;
import assignment.utils.StringResources;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import assignment.entities.Box;
import org.jetbrains.annotations.NotNull;

import static assignment.utils.StringResources.PLAYER_NOT_FOUND;
import static assignment.utils.StringResources.UNDO_QUOTA_RUN_OUT;

/**
 * A base implementation of Sokoban Game.
 */
public abstract class AbstractSokobanGame implements SokobanGame {
    @NotNull
    protected final GameState state;

    private boolean isExitSpecified = false;

    protected AbstractSokobanGame(@NotNull GameState gameState) {
        this.state = gameState;
    }

    /**
     * @param action The action received from the user.
     * @return The result of the action.
     */
    protected ActionResult processAction(Action action) {
        // 1. Find the player's current position; if missing, invalid.
        Position playerPos = state.getPlayerPositionById(action.getInitiator());
        if (playerPos == null) {
            return new ActionResult.Failed(action, PLAYER_NOT_FOUND);
        }

        // 2. Dispatch on action type:
        if (action instanceof Move move) {
            return processOneStepMove(playerPos, move);

        } else if (action instanceof Undo) {
            if (state.getUndoQuota().orElse(-1) == 0) {
                return new ActionResult.Failed(action, UNDO_QUOTA_RUN_OUT);
            }
            state.undo();
            return new ActionResult.Success(action);

        } else if (action instanceof Exit) {
            isExitSpecified = true;
            return new ActionResult.Success(action);

        } else if (action instanceof InvalidInput invalid) {
            return new ActionResult.Failed(action, invalid.getMessage());
        }

        // all cases covered
        throw new ShouldNotReachException();
    }

    /**
     * @param playerPosition The position of the player.
     * @param move            One-step move.
     * @return The action result for the move.
     */
    @NotNull
    private ActionResult processOneStepMove(@NotNull Position playerPosition, @NotNull Move move) {
        final Position nextPlayerPos = move.nextPosition(playerPosition);
        final Entity nextEntity = this.state.getEntity(nextPlayerPos);

        if (nextEntity instanceof Empty) {
            this.state.move(playerPosition, nextPlayerPos);
            return new ActionResult.Success(move);
        }

        if (nextEntity instanceof Wall) {
            return new ActionResult.Failed(move, "You hit a wall.");
        }

        if (nextEntity instanceof Player) {
            return new ActionResult.Failed(move, "You hit another player.");
        }

        if (nextEntity instanceof Box box) {
            if (box.getPlayerId() != move.getInitiator()) {
                return new ActionResult.Failed(move, "You cannot move other players' boxes.");
            }
            final Position nextBoxPos = move.nextPosition(nextPlayerPos);
            if (!(this.state.getEntity(nextBoxPos) instanceof Empty)) {
                return new ActionResult.Failed(move, "Failed to push the box.");
            }
            this.state.move(nextPlayerPos, nextBoxPos);
            this.state.move(playerPosition, nextPlayerPos);
            this.state.checkpoint();
            return new ActionResult.Success(move);
        }
        throw new ShouldNotReachException();
    }

    /**
     * @return True if the game should stop running (either user exit or win).
     */
    protected boolean shouldStop() {
        if (this.isExitSpecified) {
            return true;
        }
        return this.state.isWin();
    }

    @Override
    public void run() {
        while (!shouldStop()) {
            // … your game‐loop logic here …
        }
    }
}