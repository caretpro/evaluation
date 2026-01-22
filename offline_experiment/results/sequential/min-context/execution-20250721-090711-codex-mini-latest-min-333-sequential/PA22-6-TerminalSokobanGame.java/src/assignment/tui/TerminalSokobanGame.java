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

	@Override
	public void TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
			TerminalRenderingEngine renderingEngine) {
		super(gameState);
		this.inputEngine = inputEngine;
		this.renderingEngine = renderingEngine;
		int playerCount = gameState.getPlayerCount();
		if (playerCount > 2) {
			throw new IllegalArgumentException(String.format(TOO_MANY_PLAYERS_IN_TERMINAL, playerCount));
		}
	}

	@Override
	public void run() {
		renderingEngine.render(getState());
		while (!isOver()) {
			Action action = inputEngine.next();
			ActionResult result = step(action);
			renderingEngine.render(getState());
			renderingEngine.display(result);
		}
		renderingEngine.render(getState());
		renderingEngine.display(ActionResult.WIN);
	}
}
