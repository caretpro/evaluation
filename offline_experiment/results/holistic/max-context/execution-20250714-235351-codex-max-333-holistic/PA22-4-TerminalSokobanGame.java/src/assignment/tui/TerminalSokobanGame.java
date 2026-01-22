
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;

import static assignment.utils.StringResources.EXIT_MESSAGE;
import static assignment.utils.StringResources.ONLY_TWO_PLAYERS_ARE_SUPPORTED;
import static assignment.utils.StringResources.WIN_MESSAGE;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;
    private final RenderingEngine renderingEngine;

    /**
     * Create a new instance of TerminalSokobanGame.
     * Terminal‐based game only supports at most two players, although the assignment.game
     * package supports up to 26 players. This is only because it is hard to control
     * too many players in a terminal‐based game.
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

        int playerCount = gameState.getAllPlayerPositions().size();
        if (playerCount > 2) {
            throw new IllegalArgumentException(
                String.format(ONLY_TWO_PLAYERS_ARE_SUPPORTED, playerCount)
            );
        }
    }

    @Override
    public void run() {
        // Initial render
        renderingEngine.render(state);

        // Main game loop
        while (!shouldStop()) {
            // 1. fetch
            Action action = inputEngine.fetchAction();
            // 2. process
            ActionResult result = processAction(action);
            // 3. render
            renderingEngine.render(state);
            // 4. on failure, show message
            if (result instanceof ActionResult.Failed failed) {
                renderingEngine.message(failed.getMessage());
            }
        }

        // final message
        if (state.isWin()) {
            renderingEngine.message(WIN_MESSAGE);
        } else {
            renderingEngine.message(EXIT_MESSAGE);
        }
    }
}