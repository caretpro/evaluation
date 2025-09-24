
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;

import java.util.Collection;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

	private final TerminalInputEngine inputEngine;

	private final TerminalRenderingEngine renderingEngine;

	private final GameState gameState;

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
		this.gameState = gameState;
		this.inputEngine = inputEngine;
		this.renderingEngine = renderingEngine;
		// Check the number of players
		Collection<Character> playerIds = gameState.getPlayerIds();
		if (playerIds.size() > 2) {
			throw new IllegalArgumentException("TerminalSokobanGame supports at most two players.");
		}
	}

	@Override
	public void run() {
		renderingEngine.render(gameState);
		while (!gameState.isGameOver()) {
			Action action = inputEngine.getNextAction();
			if (action == null) {
				continue; // no action, wait for input
			}
			ActionResult result = gameState.performAction(action);
			renderingEngine.render(gameState);
			if (gameState.isGameOver()) {
				break;
			}
		}
	}
}