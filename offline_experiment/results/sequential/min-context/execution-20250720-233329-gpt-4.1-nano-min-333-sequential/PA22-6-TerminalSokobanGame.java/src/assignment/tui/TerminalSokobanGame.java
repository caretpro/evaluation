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
		int playerCount = gameState.getPlayers().size();
		if (playerCount > 2) {
			throw new IllegalArgumentException("TerminalSokobanGame supports at most two players.");
		}
	}

	@Override
	public void run() {
		boolean gameRunning = true;
		while (gameRunning) {
			renderingEngine.render(super.getGameState());
			String input = inputEngine.readInput();
			Action action = parseInputToAction(input);
			if (action == null) {
				continue;
			}
			ActionResult result = action.execute(super.getGameState());
			if (result.isGameOver()) {
				renderingEngine.render(super.getGameState());
				System.out.println(GAME_OVER_MESSAGE);
				gameRunning = false;
			}
		}
	}
}
