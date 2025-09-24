
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;

import static assignment.utils.StringResources.GAME_WON_MSG;
import static assignment.utils.StringResources.TOO_MANY_TERMINAL_PLAYERS_FMT;

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

        int playerCount = gameState.players().size();
        if (playerCount > 2) {
            throw new IllegalArgumentException(
                String.format(TOO_MANY_TERMINAL_PLAYERS_FMT, playerCount)
            );
        }
    }

    @Override
    public void run() {
        // Render initial board
        renderingEngine.render(state());

        // Main loop
        while (!state().isGameWon()) {
            Action action = inputEngine.nextAction();
            ActionResult result = doAction(action);

            renderingEngine.render(state());
            renderingEngine.showMessage(result.message());
        }

        renderingEngine.showMessage(GAME_WON_MSG);
    }
}