
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;

import static assignment.utils.StringResources.*;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;
    private final RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
            TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        
        if (gameState.getAllPlayerPositions().size() > 2) {
            throw new IllegalArgumentException("Terminal-based game supports at most two players");
        }
    }

    @Override
    public void run() {
        while (!shouldStop()) {
            renderingEngine.render(state);
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof ActionResult.Failed failed) {
                renderingEngine.message(failed.getReason());
            }
            // The undo quota check is already handled in processAction()
        }
        
        renderingEngine.render(state);
        if (state.isWin()) {
            renderingEngine.message("Congratulations! You won!");
        }
    }
}