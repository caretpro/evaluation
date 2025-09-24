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
		int playerCount = gameState.getAllPlayerPositions().size();
		if (playerCount > 2) {
			throw new IllegalArgumentException(
					"Terminal-based game supports at most two players, but found " + playerCount);
		}
	}

	@Override
	public void run() {
		renderingEngine.render(state);
		while (!shouldStop()) {
			Action action = inputEngine.fetchAction();
			ActionResult result = processAction(action);
			renderingEngine.render(state);
			if (result instanceof ActionResult.Success) {
			} else if (result instanceof ActionResult.Failed failed) {
				renderingEngine.message(failed.getMessage());
			}
			if (state.isWin()) {
				renderingEngine.message("Congratulations! You have won the game!");
				break;
			}
		}
		if (!state.isWin()) {
			renderingEngine.message("Game exited. Goodbye!");
		}
	}
}
