
package assignment.game;

import org.jetbrains.annotations.NotNull;

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
        // your existing implementation...
        // (omitted here; you already have this)
    }

    // *** Remove @Override here (there is no shouldStop() in SokobanGame) ***
    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }
}