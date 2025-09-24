
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
    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine,
            RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        long playerCount = gameState.getAllPlayerPositions().size();
        if (playerCount > 2) {
            throw new IllegalArgumentException("The map contains more than two players.");
        }
    }

    @Override
    public void run() {
        while (!shouldStop()) {
            // Render current game state
            renderingEngine.render(this.state);
            // Fetch user action
            Action action = inputEngine.fetchAction();
            // Process action
            ActionResult result = processAction(action);
            // Show message if action failed
            if (result instanceof ActionResult.Failed failed) {
                renderingEngine.message(failed.getReason());
            }
            // Re-render after processing action
            renderingEngine.render(this.state);
            // If game is won, notify and stop
            if (this.state.isWin()) {
                renderingEngine.message("Congratulations! You've won the game!");
                break;
            }
        }
    }
}