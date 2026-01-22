
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;

import static assignment.utils.StringResources.EXIT_GAME_MESSAGE;
import static assignment.utils.StringResources.TOO_MANY_PLAYERS_IN_TUI;
import static assignment.utils.StringResources.WIN_MESSAGE;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;
    private final RenderingEngine renderingEngine;

    /**
     * Create a new instance of TerminalSokobanGame.
     * Terminal‑based game only supports at most two players, although
     * the assignment.game package supports up to 26 players.
     * This is only because it is hard to control too many players
     * in a terminal‑based game.
     *
     * @param gameState       The game state.
     * @param inputEngine     the terminal input engine.
     * @param renderingEngine the terminal rendering engine.
     * @throws IllegalArgumentException when there are more than two players in the map.
     */
    public TerminalSokobanGame(GameState gameState,
                               TerminalInputEngine inputEngine,
                               TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        if (state.getAllPlayerPositions().size() > 2) {
            throw new IllegalArgumentException(TOO_MANY_PLAYERS_IN_TUI);
        }
    }

    @Override
    public void run() {
        // Initial rendering
        renderingEngine.render(state);

        // Game loop: continue until exit requested or win condition
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);

            // Re-render on successful move/undo
            if (result.isSuccess()) {
                renderingEngine.render(state);
            }

            // Always show feedback
            renderingEngine.message(result.getMessage());
        }

        // Final banner
        if (state.isWin()) {
            renderingEngine.message(WIN_MESSAGE);
        } else {
            renderingEngine.message(EXIT_GAME_MESSAGE);
        }
    }
}