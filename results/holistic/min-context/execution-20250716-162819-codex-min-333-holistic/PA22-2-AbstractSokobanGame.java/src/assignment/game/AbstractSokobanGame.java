
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

import java.util.Optional;

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
    @Override
    protected boolean shouldStop() {
        return isExitSpecified || state.won();
    }

    /**
     * @param action The action received from the user.
     * @return The result of the action.
     */
    @Override
    protected ActionResult processAction(Action action) {
        if (action instanceof InvalidInput) {
            return ActionResult.failure(action, "Invalid input.");
        }

        if (action instanceof Exit) {
            isExitSpecified = true;
            return ActionResult.success();
        }

        // require a current player from GameState
        Optional<Integer> current = state.currentPlayer();
        if (current.isEmpty()) {
            return ActionResult.failure(action, PLAYER_NOT_FOUND);
        }
        int playerId = current.get();

        if (action instanceof Move move) {
            Position playerPosition = state.playerPos(playerId);
            return processOneStepMove(playerPosition, move);
        }

        if (action instanceof Undo) {
            if (state.undoQuota() <= 0) {
                return ActionResult.failure(action, UNDO_QUOTA_RUN_OUT);
            }
            state.undo();
            return ActionResult.success();
        }

        throw new ShouldNotReachException();
    }

    /**
     * @param playerPosition The position of the player.
     * @param move           One-step move. We assume every move has only one step.
     * @return The action result for the move.
     */
    @NotNull
    private ActionResult processOneStepMove(@NotNull Position playerPosition,
                                            @NotNull Move move) {
        Position nextPlayerPos = move.nextPosition(playerPosition);
        Entity nextEntity = state.getEntity(nextPlayerPos);

        if (nextEntity instanceof Empty) {
            state.move(playerPosition, nextPlayerPos);
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
            Position nextBoxPos = move.nextPosition(nextPlayerPos);
            if (!(state.getEntity(nextBoxPos) instanceof Empty)) {
                return new ActionResult.Failed(move, "Failed to push the box.");
            }
            state.move(nextPlayerPos, nextBoxPos);
            state.move(playerPosition, nextPlayerPos);
            state.checkpoint();
            return new ActionResult.Success(move);
        }

        throw new ShouldNotReachException();
    }
}