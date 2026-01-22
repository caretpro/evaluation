
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

    private boolean exitRequested = false;

    protected AbstractSokobanGame(@NotNull GameState gameState) {
        this.state = gameState;
    }

    /**
     * @return True if the game should stop running (exit requested or win condition).
     */
    protected boolean shouldStop() {
        return exitRequested || state.isWin();
    }

    /**
     * @param action The action received from the user.
     * @return The result of the action.
     */
    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            exitRequested = true;
            return new ActionResult.Success(action);
        }

        if (action instanceof InvalidInput) {
            return new ActionResult.Failed(action, ((InvalidInput) action).getReason());
        }

        if (action instanceof Undo) {
            int playerId = action.getInitiator();
            if (!state.canUndo(playerId)) {
                return new ActionResult.Failed(action, String.format(UNDO_QUOTA_RUN_OUT, playerId));
            }
            state.undo(playerId);
            return new ActionResult.Success(action);
        }

        if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = move.getInitiator();
            Position playerPos = state.playerPosition(playerId)
                    .orElseThrow(() ->
                        new IllegalStateException(String.format(PLAYER_NOT_FOUND, playerId))
                    );
            return processOneStepMove(playerPos, move);
        }

        // any other action types are unsupported
        throw new ShouldNotReachException();
    }

    /**
     * @param playerPosition The position of the player.
     * @param move           One-step move. We assume every move has only one step.
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
            // Game history checkpoint reached if any box is moved.
            this.state.checkpoint();
            return new ActionResult.Success(move);
        }

        throw new ShouldNotReachException();
    }
}