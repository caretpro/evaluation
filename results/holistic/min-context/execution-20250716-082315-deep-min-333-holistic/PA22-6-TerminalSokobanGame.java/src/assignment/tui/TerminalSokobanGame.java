
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
        
        // Check the number of players
        if (getGameState().getNumberOfPlayers() > 2) {
            throw new IllegalArgumentException("Terminal version only supports up to 2 players");
        }
    }

    @Override
    public void run() {
        renderingEngine.render(getGameState());
        
        while (!getGameState().isGameFinished()) {
            Action action = inputEngine.getAction();
            ActionResult result = getGameState().execute(action);
            
            if (result.wasSuccessful()) {
                renderingEngine.render(getGameState());
            } else {
                renderingEngine.renderError(result.errorMessage());
            }
        }
        
        renderingEngine.renderGameOver(getGameState());
    }
}