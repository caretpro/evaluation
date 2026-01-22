
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;

import static assignment.utils.StringResources.GAME_EXIT_MESSAGE;
import static assignment.utils.StringResources.GAME_WIN_MESSAGE;
import static assignment.utils.StringResources.PROMPT_ACTION;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

	private final TerminalInputEngine inputEngine;

	private final TerminalRenderingEngine renderingEngine;

	/**
	 * Create a new instance of TerminalSokobanGame.
	 * Terminal-based game only support at most two players, although the assignment.game package supports up to 26 players.
	 * This is only because it is hard to control too many players in a terminal-based game.
	 *
	 * @param gameState       The game state.
	 * @param inputEngine     the terminal input engine.
	 * @param renderingEngine the terminal rendering engine.
	 * @throws IllegalArgumentException when there are more than two players in the map.
	 */
	public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
			TerminalRenderingEngine renderingEngine) {
		super(gameState);
		this.inputEngine = inputEngine;
		this.renderingEngine = renderingEngine;

		int playerCount = gameState.getAllPlayerPositions().size();
		if (playerCount > 2) {
			throw new IllegalArgumentException("TerminalSokobanGame supports at most two players, but found " + playerCount);
		}
	}

	public void run() {
		while (!shouldStop()) {
			renderingEngine.render(state);
			renderingEngine.message(PROMPT_ACTION);
			Action action = inputEngine.fetchAction();
			ActionResult result = processAction(action);
			if (result instanceof ActionResult.Failed failed) {
				renderingEngine.message(failed.getMessage());
			}
		}
		renderingEngine.render(state);
		if (state.isWin()) {
			renderingEngine.message(GAME_WIN_MESSAGE);
		} else {
			renderingEngine.message(GAME_EXIT_MESSAGE);
		}
	}
}