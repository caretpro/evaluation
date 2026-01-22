
package assignment.game;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import assignment.utils.ShouldNotReachException;
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
     * @return True if the game should stop running.
     * For example when the user specified to exit the game or the user won the game.
     */
    protected boolean shouldStop() {
        // The game should stop if exit has been specified or the game is won.
        return isExitSpecified || state.isWin();
    }

    /**
     * @param action The action received from the user.
     * @return The result of the action.
     */
    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new ActionResult.Success(action);
        } else if (action instanceof Undo) {
            // Check if undo quota is available
            if (state.getUndoQuota().isPresent() && state.getUndoQuota().get() <= 0) {
                return new ActionResult.Failed(action, UNDO_QUOTA_RUN_OUT);
            }
            // Perform undo
            state.undo();
            return new ActionResult.Success(action);
        } else if (action instanceof Move move) {
            // Process move action
            Position playerPos = state.getPlayerPositionById(move.getInitiator());
            if (playerPos == null) {
                return new ActionResult.Failed(move, String.format(PLAYER_NOT_FOUND, move.getInitiator()));
            }
            return processOneStepMove(playerPos, move);
        } else if (action instanceof InvalidInput) {
            // Invalid input, no state change
            return new ActionResult.Failed(action, "Invalid input.");
        } else {
            // Unknown action type
            throw new ShouldNotReachException();
        }
    }

    /**
     * Processes a one-step move for the player.
     *
     * @param playerPosition The position of the player.
     * @param move           The move action.
     * @return The result of the move.
     */
    @NotNull
    private ActionResult processOneStepMove(@NotNull Position playerPosition, @NotNull Move move) {
        final Position nextPlayerPos = move.nextPosition(playerPosition);
        final Entity nextEntity = this.state.getEntity(nextPlayerPos);

        if (nextEntity instanceof Empty) {
            this.state.move(playerPosition, nextPlayerPos); // move if next place is empty
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
            if (!(this.state.getEntity(nextBoxPos) instanceof Empty))
                return new ActionResult.Failed(move, "Failed to push the box.");
            this.state.move(nextPlayerPos, nextBoxPos);
            this.state.move(playerPosition, nextPlayerPos);
            // Game history checkpoint reached if any box is moved.
            this.state.checkpoint();
            return new ActionResult.Success(move);
        }
        throw new ShouldNotReachException();
    }
}