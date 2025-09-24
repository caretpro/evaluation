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
		renderingEngine.render(state);
		while (!shouldStop()) {
			try {
				final Action action = inputEngine.fetchAction();
				final ActionResult result = processAction(action);
				if (result instanceof ActionResult.Failed) {
					renderingEngine.message(((ActionResult.Failed) result).getMessage());
				}
			} catch (final Exception e) {
				renderingEngine.message(StringResources.INTERNAL_ERROR_MESSAGE);
				e.printStackTrace();
			} finally {
				renderingEngine.render(state);
			}
		}
		renderingEngine.message(StringResources.GAME_OVER_MESSAGE);
	}
}
