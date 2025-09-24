
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;
import assignment.entities.Entity;

import static assignment.utils.StringResources.*;

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
     * @param inputEngine     the terminal input engine.
     * @param renderingEngine the terminal rendering engine.
     * @throws IllegalArgumentException when there are more than two players in the map.
     */
    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
            TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;

        // Check the number of players in the game state
        long playerCount = gameState.getAllPlayerPositions().stream()
                .filter(pos -> {
                    Entity entity = gameState.getEntity(pos);
                    return entity instanceof assignment.entities.Player;
                })
                .count();

        if (playerCount > 2) {
            throw new IllegalArgumentException("The terminal Sokoban game supports at most two players.");
        }
    }

    @Override
    public void run() {
        while (!shouldStop()) {
            // Render current game state
            renderingEngine.render(state);
            // Fetch user action
            Action action = inputEngine.fetchAction();
            // Process the action
            ActionResult result = processAction(action);
            // Optionally, display messages for failed actions
            if (result instanceof ActionResult.Failed failed) {
                renderingEngine.message(failed.message);
            }
            // If the action was Exit, or game is won, loop will terminate
        }
        // Final render or message if needed
        renderingEngine.render(state);
        if (state.isWin()) {
            renderingEngine.message("Congratulations! You won the game.");
        } else {
            renderingEngine.message("Game over. Thanks for playing!");
        }
    }
}