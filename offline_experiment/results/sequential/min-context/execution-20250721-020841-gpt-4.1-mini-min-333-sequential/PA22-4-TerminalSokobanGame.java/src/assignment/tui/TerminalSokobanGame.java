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
		if (gameState.getPlayers().size() > 2) {
			throw new IllegalArgumentException("Terminal-based game supports at most two players.");
		}
	}

	@Override
	public void run() {
		renderingEngine.render(super.gameState);
		boolean running = true;
		while (running) {
			Action action = ((TerminalInputEngine) inputEngine).getNextAction();
			if (action == null) {
				continue;
			}
			ActionResult result = super.gameState.performAction(action);
			renderingEngine.render(super.gameState);
			if (result.isGameOver()) {
				renderingEngine.showMessage(assignment.utils.StringResources.GAME_OVER_MESSAGE);
				running = false;
			} else if (result.isQuit()) {
				renderingEngine.showMessage(assignment.utils.StringResources.GOODBYE_MESSAGE);
				running = false;
			} else if (!result.isSuccess()) {
				renderingEngine.showMessage(assignment.utils.StringResources.INVALID_ACTION_MESSAGE);
			}
		}
	}
}
