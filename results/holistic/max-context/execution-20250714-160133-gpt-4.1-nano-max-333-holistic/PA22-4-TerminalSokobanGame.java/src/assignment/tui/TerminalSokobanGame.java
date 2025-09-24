
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;
import assignment.entities.Entity;

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
     * @param inputEngine     The terminal input engine.
     * @param renderingEngine The terminal rendering engine.
     * @throws IllegalArgumentException when there are more than two players in the map.
     */
    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine,
            RenderingEngine renderingEngine) {
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
            throw new IllegalArgumentException("The map contains more than two players, which is not supported in terminal mode.");
        }
    }

    @Override
    public void run() {
        while (!shouldStop()) {
            // Render the current game state
            renderingEngine.render(state);
            // Fetch user action
            Action action = inputEngine.fetchAction();
            // Process the action
            ActionResult result = processAction(action);
            // Display message if action failed
            if (result instanceof ActionResult.Failed failed) {
                renderingEngine.message(failed.getContent());
            }
        }
        // Final render after game ends
        renderingEngine.render(state);
        renderingEngine.message("Game Over!");
    }
}