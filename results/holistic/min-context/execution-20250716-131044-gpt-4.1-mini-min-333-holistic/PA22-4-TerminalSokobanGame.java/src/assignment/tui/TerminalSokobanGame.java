
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
		Collection<?> players = this.gameState.getPlayers();
		if (players.size() > 2) {
			throw new IllegalArgumentException("TerminalSokobanGame supports at most two players.");
		}
	}

	@Override
	public void run() {
		while (true) {
			renderingEngine.render(this.gameState);
			Action action = inputEngine.getNextAction(this.gameState);
			if (action == null) {
				// No action means exit or end of input
				break;
			}
			ActionResult result = this.gameState.performAction(action);
			if (result.isGameOver()) {
				renderingEngine.render(this.gameState);
				System.out.println("Game Over!");
				break;
			}
		}
	}
}