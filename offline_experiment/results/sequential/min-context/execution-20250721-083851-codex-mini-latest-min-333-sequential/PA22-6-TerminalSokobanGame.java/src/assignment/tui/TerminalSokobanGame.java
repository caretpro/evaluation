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
	 * @param inputEngine      the terminal input engin.
	 * @param renderingEngine  the terminal rendering engine.
	 * @throws IllegalArgumentException  when there are more than two players in the map.
	 */
	public void TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
			TerminalRenderingEngine renderingEngine) {
		super(gameState);
		this.inputEngine = inputEngine;
		this.renderingEngine = renderingEngine;
		int numPlayers = gameState.getPlayers().size();
		if (numPlayers > 2) {
			throw new IllegalArgumentException(String.format(TOO_MANY_PLAYERS_MSG, numPlayers));
		}
	}

	@Override
	public void run() {
		renderingEngine.render(game);
		while (!game.isGameOver()) {
			Action action = inputEngine.nextAction();
			ActionResult result = step(action);
			renderingEngine.render(game);
			result.getMessage().ifPresent(renderingEngine::showMessage);
		}
		renderingEngine.render(game);
		renderingEngine.showMessage(GAME_OVER_MSG);
	}
}
