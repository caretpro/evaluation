
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.utils.ShouldNotReachException;

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

	@Override
	public void run() {
		while (!this.shouldStop()) {
			this.renderingEngine.render(this.state);
			Action action = this.inputEngine.fetchAction();
			ActionResult result = this.processAction(action);
			if (result instanceof ActionResult.Success) {
				this.renderingEngine.message("Action succeeded.");
			} else if (result instanceof ActionResult.Failed failed) {
				this.renderingEngine.message("Action failed: " + failed.getMessage());
			} else {
				throw new ShouldNotReachException();
			}
		}
		this.renderingEngine.render(this.state);
		if (this.state.isWin()) {
			this.renderingEngine.message("Congratulations! You have won the game!");
		} else {
			this.renderingEngine.message("Game exited.");
		}
	}
}