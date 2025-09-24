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
		renderingEngine.render(gameState);
		boolean running = true;
		while (running) {
			Action action = ((TerminalInputEngine) inputEngine).getNextAction();
			if (action == null) {
				continue;
			}
			ActionResult result = gameState.performAction(action);
			renderingEngine.render(gameState);
			if (result.isGameOver()) {
				((TerminalRenderingEngine) renderingEngine).showMessage("Game over! Thanks for playing.");
				running = false;
			} else if (result.isQuit()) {
				((TerminalRenderingEngine) renderingEngine).showMessage("Game quit.");
				running = false;
			} else if (!result.isSuccess()) {
				((TerminalRenderingEngine) renderingEngine).showMessage("Invalid move, try again.");
			}
		}
	}
}
