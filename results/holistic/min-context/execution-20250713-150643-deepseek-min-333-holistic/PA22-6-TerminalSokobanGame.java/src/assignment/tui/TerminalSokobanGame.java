
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
        
        if (gameState.getPlayerCount() > 2) {
            throw new IllegalArgumentException("Terminal-based game supports at most 2 players");
        }
    }

    @Override
    public void run() {
        renderingEngine.render(gameState);
        
        while (!gameState.isOver()) {
            Action action = inputEngine.getAction();
            ActionResult result = gameState.execute(action);
            
            if (result.success()) {
                renderingEngine.render(gameState);
            } else {
                renderingEngine.render(result.message());
            }
        }
        
        renderingEngine.render(gameState);
    }
}