
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
    protected boolean shouldStop() {
        // stop if user asked to exit or game is solved (all boxes on goals)
        return isExitSpecified || state.isSolved();
    }

    /**
     * @param action The action received from the user.
     * @return The result of the action.
     */
    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new ActionResult.Success(action);
        }

        if (action instanceof InvalidInput invalid) {
            return new ActionResult.Failed(invalid, invalid.getMessage());
        }

        if (action instanceof Undo undo) {
            boolean undone = state.undo();
            if (!undone) {
                return new ActionResult.Failed(undo, UNDO_QUOTA_RUN_OUT);
            }
            return new ActionResult.Success(undo);
        }

        if (action instanceof Move move) {
            Optional<Position> optPos = state.findPlayerPosition(move.getInitiator());
            if (optPos.isEmpty()) {
                throw new IllegalStateException(PLAYER_NOT_FOUND);
            }
            return processOneStepMove(optPos.get(), move);
        }

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
        final Entity nextEntity = state.getEntity(nextPlayerPos);

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
            final Position nextBoxPos = move.nextPosition(nextPlayerPos);
            if (!(state.getEntity(nextBoxPos) instanceof Empty)) {
                return new ActionResult.Failed(move, "Failed to push the box.");
            }
            state.move(nextPlayerPos, nextBoxPos);
            state.move(playerPosition, nextPlayerPos);
            // Game history checkpoint reached if any box is moved.
            state.checkpoint();
            return new ActionResult.Success(move);
        }

        throw new ShouldNotReachException();
    }
}