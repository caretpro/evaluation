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
		long numberOfPlayers = gameState.getAllPlayerPositions().stream().count();
		if (numberOfPlayers > 2) {
			throw new IllegalArgumentException("Terminal-based game only supports at most two players.");
		}
	}

	@Override
	public void run() {
		renderingEngine.render(this.state);
		while (!shouldStop()) {
			try {
				final Action action = inputEngine.fetchAction();
				final ActionResult result = processAction(action);
				if (result instanceof ActionResult.Success) {
					renderingEngine.render(this.state);
				} else if (result instanceof ActionResult.Failed failure) {
					renderingEngine.message(failure.getMessage());
				}
			} catch (final Exception e) {
				renderingEngine.message(INTERNAL_ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		if (this.state.isWin()) {
			renderingEngine.message(WIN_MESSAGE);
		} else {
			renderingEngine.message(EXIT_MESSAGE);
		}
	}
}
