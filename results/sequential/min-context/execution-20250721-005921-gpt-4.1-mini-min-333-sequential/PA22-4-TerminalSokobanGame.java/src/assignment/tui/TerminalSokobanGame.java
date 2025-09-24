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
		while (!gameState.isGameOver()) {
			Action action = inputEngine.readAction();
			ActionResult result = gameState.executeAction(action);
			renderingEngine.render(gameState);
			if (result.isGameWon()) {
				renderingEngine.displayMessage("Congratulations! You won the game.");
				break;
			} else if (result.isGameLost()) {
				renderingEngine.displayMessage("Game over. Try again!");
				break;
			}
		}
	}
}
