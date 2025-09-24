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

	/**
	 * Create a new instance of TerminalSokobanGame. Terminal-based game only support at most two players, although the assignment.game package supports up to 26 players. This is only because it is hard to control too many players in a terminal-based game.
	 * @param gameState        The game state.
	 * @param inputEngine      the terminal input engine.
	 * @param renderingEngine  the terminal rendering engine.
	 * @throws IllegalArgumentException  when there are more than two players in the map.
	 */
	public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
			TerminalRenderingEngine renderingEngine) {
		super(gameState);
		this.inputEngine = inputEngine;
		this.renderingEngine = renderingEngine;
		int numPlayers = gameState.getPlayers().size();
		if (numPlayers > 2) {
			throw new IllegalArgumentException(format(StringResources.ERROR_TOO_MANY_PLAYERS, numPlayers));
		}
		renderingEngine.render(gameState);
	}

	/**
	 * Obtain the next Action for the current state of the game.
	 * @param state  the current game‑state
	 * @return  the player's next Action
	 */
	Action nextAction(GameState state);
}
