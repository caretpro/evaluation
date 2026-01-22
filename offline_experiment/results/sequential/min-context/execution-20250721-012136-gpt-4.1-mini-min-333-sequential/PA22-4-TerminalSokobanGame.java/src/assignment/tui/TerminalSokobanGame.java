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
		boolean running = true;
		GameState state = super.gameState;
		TerminalInputEngine terminalInput = (TerminalInputEngine) inputEngine;
		while (running) {
			renderingEngine.render(state);
			Action action = terminalInput.nextAction();
			ActionResult result = state.applyAction(action);
			renderingEngine.render(state);
			if (state.isGameOver() || action.equals(Action.QUIT)) {
				running = false;
			}
		}
	}
}
