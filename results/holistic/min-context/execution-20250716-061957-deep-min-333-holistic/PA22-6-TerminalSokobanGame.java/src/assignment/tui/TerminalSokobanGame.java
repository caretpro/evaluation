
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

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
            TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        
        if (super.getGameState().getPlayerCount() > 2) {
            throw new IllegalArgumentException("Terminal-based game supports at most 2 players");
        }
    }

    @Override
    public void run() {
        renderingEngine.render(super.getGameState());
        
        while (!super.getGameState().isGameOver()) {
            Action action = inputEngine.getNextAction();
            ActionResult result = super.getGameState().executeAction(action);
            
            if (result == ActionResult.SUCCESS) {
                renderingEngine.render(super.getGameState());
            } else {
                renderingEngine.renderMessage(result.getMessage());
            }
        }
        
        renderingEngine.renderGameOver(super.getGameState());
    }
}