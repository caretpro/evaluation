
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;

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
		// Check the number of players
		if (gameState.getPlayers().size() > 2) {
			throw new IllegalArgumentException("TerminalSokobanGame supports at most two players.");
		}
	}

	@Override
	public void run() {
		while (true) {
			renderingEngine.render(gameState);
			Action action = inputEngine.getNextAction();
			if (action == null) {
				// No action means exit or end
				break;
			}
			ActionResult result = gameState.applyAction(action);
			if (result.isGameOver()) {
				renderingEngine.render(gameState);
				System.out.println("Game Over!");
				break;
			}
		}
	}
}