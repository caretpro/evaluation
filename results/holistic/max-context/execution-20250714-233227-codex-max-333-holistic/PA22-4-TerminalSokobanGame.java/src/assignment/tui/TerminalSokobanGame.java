
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;

import java.util.Set;

import static assignment.utils.StringResources.GAME_EXIT;
import static assignment.utils.StringResources.GAME_WON;
import static assignment.utils.StringResources.TOO_MANY_PLAYERS;
import static assignment.utils.StringResources.ACTION_FAILED;
import static assignment.utils.StringResources.ACTION_SUCCESSFUL;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;
    private final RenderingEngine renderingEngine;

    /**
     * Create a new instance of TerminalSokobanGame.
     * Terminal‐based game only supports at most two players, although the assignment.game package
     * supports up to 26 players. This is only because it is hard to control too many players
     * in a terminal‐based game.
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

        // Check the number of players: at most two allowed
        Set<?> players = gameState.getAllPlayerPositions();
        if (players.size() > 2) {
            throw new IllegalArgumentException(TOO_MANY_PLAYERS);
        }
    }

    @Override
    public void run() {
        // Main game loop: render, fetch action, process, show messages, until win or exit
        while (!shouldStop()) {
            renderingEngine.render(state);
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result.isSuccess()) {
                renderingEngine.message(ACTION_SUCCESSFUL);
            } else {
                renderingEngine.message(ACTION_FAILED + result.getMessage());
            }
        }

        // Final render and end‐of‐game message
        renderingEngine.render(state);
        if (state.isWin()) {
            renderingEngine.message(GAME_WON);
        } else {
            renderingEngine.message(GAME_EXIT);
        }
    }
}