
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;

import static assignment.utils.StringResources.GAME_SOLVED;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

	private final InputEngine inputEngine;

	private final RenderingEngine renderingEngine;

	private final GameState gameState;

	/**
	 * Create a new instance of TerminalSokobanGame.
	 * Terminal-based game only support at most two players, although the assignment.game package supports up to 26 players.
	 * This is only because it is hard to control too many players in a terminal-based game.
	 *
	 * @param gameState       The game state.
	 * @param inputEngine     the terminal input engin.
	 * @param renderingEngine the terminal rendering engine.
	 * @throws IllegalArgumentException when there are more than two players in the map.
	 */
	public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
			TerminalRenderingEngine renderingEngine) {
		super(gameState);
		this.inputEngine = inputEngine;
		this.renderingEngine = renderingEngine;
		this.gameState = gameState;
		if (gameState.getPlayers().size() > 2) {
			throw new IllegalArgumentException("Terminal-based game only support at most two players.");
		}
	}

	@Override
	public void run() {
		while (!gameState.isSolved()) {
			renderingEngine.render(gameState);
			Action action = inputEngine.getNextAction();
			ActionResult result = executeAction(action);
			if (result.isSuccessful()) {
				gameState.applyAction(action);
			} else {
				renderingEngine.renderMessage(result.getMessage());
			}
		}
		renderingEngine.render(gameState);
		renderingEngine.renderMessage(GAME_SOLVED);
	}
}