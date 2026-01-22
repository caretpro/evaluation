
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

    private final TerminalInputEngine inputEngine;
    private final TerminalRenderingEngine renderingEngine;
    private final GameState gameState;

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
            TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.gameState = gameState;
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        
        if (gameState.getPlayerPositions().size() > 2) {
            throw new IllegalArgumentException("Terminal-based game supports at most 2 players");
        }
    }

    @Override
    public void run() {
        renderingEngine.render(gameState);
        
        while (!gameState.isGameOver()) {
            Action action = inputEngine.readAction();
            ActionResult result = action.perform(gameState);
            
            if (result.successful()) {
                renderingEngine.render(gameState);
            } else {
                renderingEngine.showMessage(result.errorMessage());
            }
        }
        
        renderingEngine.displayGameOver(gameState);
    }
}