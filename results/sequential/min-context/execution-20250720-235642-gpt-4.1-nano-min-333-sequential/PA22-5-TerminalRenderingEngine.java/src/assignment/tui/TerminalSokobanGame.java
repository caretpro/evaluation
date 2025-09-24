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
     * @param inputEngine     the terminal input engin.
     * @param renderingEngine the terminal rendering engine.
     * @throws IllegalArgumentException when there are more than two players in the map.
     */
    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        if (gameState.getAllPlayerPositions().size() > 2) {
            throw new IllegalArgumentException("TerminalSokobanGame only support at most two players.");
        }
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(GAME_READY_MESSAGE);
        renderingEngine.render(state);
        while (!shouldStop()) {
            final String undoQuotaMessage = state.getUndoQuota()
                    .map(it -> String.format(UNDO_QUOTA_TEMPLATE, it))
                    .orElse(UNDO_QUOTA_UNLIMITED);
            renderingEngine.message(undoQuotaMessage);
            renderingEngine.message(">>> ");
            final Action action = inputEngine.fetchAction();
            final ActionResult result = processAction(action);
            if (result instanceof ActionResult.Failed r) {
                renderingEngine.message(r.getReason());
            }
            renderingEngine.render(state);
        }
        renderingEngine.message(GAME_EXIT_MESSAGE);
        if (this.state.isWin()) {
            renderingEngine.message(WIN_MESSAGE);
        }
    }
}
