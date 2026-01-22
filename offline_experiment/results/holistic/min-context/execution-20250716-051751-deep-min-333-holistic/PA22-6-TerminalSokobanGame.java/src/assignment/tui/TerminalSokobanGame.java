
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;
    private final RenderingEngine renderingEngine;
    private final GameState gameState;

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine,
            RenderingEngine renderingEngine) {
        super(gameState);
        this.gameState = gameState;
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        
        if (gameState.getPlayerCount() > 2) {
            throw new IllegalArgumentException("Terminal-based game supports at most 2 players");
        }
    }

    @Override
    public void run() {
        renderingEngine.render(gameState);
        
        while (!gameState.isGameOver()) {
            Action action = inputEngine.getNextAction();
            ActionResult result = gameState.executeAction(action);
            
            if (result == ActionResult.SUCCESS || result == ActionResult.MOVED) {
                renderingEngine.render(gameState);
            } else if (result == ActionResult.INVALID) {
                System.out.println("Invalid action!");
            }
        }
        
        renderingEngine.renderGameOver(gameState);
    }
}