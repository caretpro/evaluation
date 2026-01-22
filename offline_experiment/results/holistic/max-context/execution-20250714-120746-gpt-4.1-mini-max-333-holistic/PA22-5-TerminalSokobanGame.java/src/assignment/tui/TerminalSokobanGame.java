
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
		int playerCount = gameState.getAllPlayerPositions().size();
		if (playerCount > 2) {
			throw new IllegalArgumentException("TerminalSokobanGame supports at most two players, but found " + playerCount);
		}
	}

	public void run() {
		while (!shouldStop()) {
			// Render the current game state
			renderingEngine.render(state);

			// Fetch an action from input engine
			Action action = inputEngine.fetchAction();

			// Process the action
			ActionResult result = processAction(action);

			// Display the result message
			if (result instanceof ActionResult.Success) {
				renderingEngine.message("Action succeeded.");
			} else if (result instanceof ActionResult.Failed failed) {
				renderingEngine.message("Action failed: " + failed.getMessage());
			} else {
				// Defensive fallback
				renderingEngine.message("Unknown action result.");
			}
		}

		// After loop ends, render final state and display win or exit message
		renderingEngine.render(state);
		if (state.isWin()) {
			renderingEngine.message("You win!");
		} else {
			renderingEngine.message("Game exited.");
		}
	}
}