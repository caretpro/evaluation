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
import assignment.game.GameState;
import assignment.game.Position;
import assignment.game.SokobanGame;
import assignment.utils.ShouldNotReachException;
import assignment.utils.StringResources;
import assignment.utils.NotImplementedException;
import assignment.utils.ShouldNotReachException;

import static assignment.utils.StringResources.PLAYER_NOT_FOUND;
import static assignment.utils.StringResources.UNDO_QUOTA_RUN_OUT;


/**
 * A base implementation of Sokoban Game.
 */
public abstract class AbstractSokobanGame implements SokobanGame {
    protected final GameState state;

    private boolean isExitSpecified = false;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    /**
     * @return True is the game should stop running.
     * For example when the user specified to exit the game or the user won the game.
     */
    protected boolean shouldStop() {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * @param action The action received from the user.
     * @return The result of the action.
     */
    protected ActionResult processAction(Action action) {
        // TODO
        throw new NotImplementedException();
    }
}
