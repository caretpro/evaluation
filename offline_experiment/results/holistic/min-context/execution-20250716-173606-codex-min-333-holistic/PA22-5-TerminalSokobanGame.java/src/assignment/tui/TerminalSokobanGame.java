
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
    public TerminalSokobanGame(GameState gameState,
                               InputEngine inputEngine,
                               RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;

        // Check the number of players (max 2 in terminal)
        if (playerCount > 2) {
            throw new IllegalArgumentException(
                "TerminalSokobanGame supports at most two players, but got " + playerCount
            );
        }
    }

    @Override
    public void run() {
        // Initial render of the map
        renderingEngine.render(state);

        // Main game loop
        while (!state.isFinished()) {
            // Prompt for and read an action from the terminal
            Action action = inputEngine.getAction(state);

            // Execute the action and receive a result
            ActionResult result = state.performAction(action);

            // Apply the result to the game state
            state = result.getState();

            // Render the result of the action (e.g. moved, invalid, win/lose)
            renderingEngine.render(result);

            // Render the updated map/state
            renderingEngine.render(state);
        }

        // Game finished → display final message
        if (state.isWon()) {
            renderingEngine.renderMessage(WIN_MESSAGE);
        } else {
            renderingEngine.renderMessage(LOSE_MESSAGE);
        }
    }
}