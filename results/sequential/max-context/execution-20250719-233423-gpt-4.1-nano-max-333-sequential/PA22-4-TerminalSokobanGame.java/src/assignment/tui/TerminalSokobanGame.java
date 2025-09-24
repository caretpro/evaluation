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
			throw new IllegalArgumentException("Terminal Sokoban supports at most two players.");
		}
	}

	@Override
	public void run() {
		while (!shouldStop()) {
			renderingEngine.render(state);
			renderingEngine.message("Enter command (WASD/HJKL for movement, R for undo, Q to quit):");
			Action action = inputEngine.fetchAction();
			ActionResult result = processAction(action);
			if (result instanceof ActionResult.Failed failed) {
				renderingEngine.message(failed.message);
			}
			if (state.isWin()) {
				renderingEngine.render(state);
				renderingEngine.message("Congratulations! You've won the game!");
				break;
			}
		}
	}
}
