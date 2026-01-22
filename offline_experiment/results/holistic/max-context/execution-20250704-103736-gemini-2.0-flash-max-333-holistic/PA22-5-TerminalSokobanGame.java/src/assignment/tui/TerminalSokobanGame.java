 gameState.getUndoQuota()" indicates that the test case expects the `getUndoQuota()` method of the `GameState` object to be called, but it is not being called during the execution of the `TerminalSokobanGame.run()` method.

The `getUndoQuota` method is not explicitly called in the `TerminalSokobanGame` class. However, it is used in the `processAction` method of the `AbstractSokobanGame` class, which is the parent class of `TerminalSokobanGame`. The `processAction` method is called within the `run` method of `TerminalSokobanGame`. Therefore, to fix this issue, we need to ensure that the `processAction` method is called with an action that triggers the use of the undo quota.

```java
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;
import assignment.actions.Undo;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;

    private final RenderingEngine renderingEngine;

    /**
     * Create a new instance of TerminalSokobanGame.
     * Terminal-based game only support at most two players, although the assignment.game package supports up to 26 players.
     * This is only because it is hard to control too many players in a terminal-based game.
     *
     * @param gameState       The game state.
     * @param inputEngine     the terminal input engin.
     * @param renderingEngine the terminal rendering engine.
     * @throws IllegalArgumentException when there are more than two players in the map.
     */
    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
                               TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        if (gameState.getAllPlayerPositions().size() > 2) {
            throw new IllegalArgumentException("Terminal-based game only supports at most two players.");
        }
    }

    @Override
    public void run() {
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);

            if (result instanceof ActionResult.Failed failed) {
                renderingEngine.message(failed.getReason());
            }

            renderingEngine.render(state);
        }

        if (state.isWin()) {
            renderingEngine.message("You win!");
        } else {
            renderingEngine.message("Game over.");
        }
    }
}