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

	public void TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
			TerminalRenderingEngine renderingEngine) {
		super(gameState);
		this.inputEngine = inputEngine;
		this.renderingEngine = renderingEngine;
		if (gameState.getAllPlayerPositions().size() > 2) {
			throw new IllegalArgumentException(TOO_MANY_PLAYERS);
		}
	}

	@Override
	public void run() {
		renderingEngine.render(state);
		while (!shouldStop()) {
			Action action = inputEngine.fetchAction();
			ActionResult result = processAction(action);
			if (result instanceof ActionResult.Failed failed) {
				renderingEngine.message(failed.getReason());
			}
			renderingEngine.render(state);
		}
		if (state.isWin()) {
			renderingEngine.message(WIN_MESSAGE);
		} else {
			renderingEngine.message(EXIT_MESSAGE);
		}
	}
}
