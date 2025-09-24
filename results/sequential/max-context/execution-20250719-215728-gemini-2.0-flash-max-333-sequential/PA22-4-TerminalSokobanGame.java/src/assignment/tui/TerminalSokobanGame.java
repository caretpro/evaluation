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
	 */
	public void TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
			TerminalRenderingEngine renderingEngine) {
		super(gameState);
		this.inputEngine = inputEngine;
		this.renderingEngine = renderingEngine;
		List<Position> playerPositions = gameState.getAllPlayerPositions().stream().toList();
		if (playerPositions.size() > 2) {
			throw new IllegalArgumentException(
					"The map contains more than two players, which is not supported in terminal mode.");
		}
	}

	@Override
	public void run() {
		renderingEngine.render(this.state);
		while (!shouldStop()) {
			renderingEngine.message(INPUT_REQUEST_MESSAGE);
			final Action action = inputEngine.fetchAction();
			final ActionResult result = processAction(action);
			if (result instanceof ActionResult.Success) {
				renderingEngine.render(this.state);
			} else if (result instanceof Failed) {
				Failed failed = (Failed) result;
				renderingEngine.message(failed.getMessage());
			}
		}
		if (this.state.isWin()) {
			renderingEngine.message(WIN_MESSAGE);
		} else {
			renderingEngine.message(EXIT_MESSAGE);
		}
	}
}
