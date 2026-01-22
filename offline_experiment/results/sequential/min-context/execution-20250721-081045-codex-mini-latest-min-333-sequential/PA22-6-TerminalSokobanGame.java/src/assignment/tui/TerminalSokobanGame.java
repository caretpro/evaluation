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
		int count = gameState.getPlayers().size();
		if (count > 2) {
			throw new IllegalArgumentException("Terminal game supports at most two players, but map has " + count);
		}
	}

	@Override
	public void run() {
		renderingEngine.render(state);
		while (!state.isGameOver()) {
			Action action = inputEngine.readAction();
			ActionResult result = doAction(action);
			renderingEngine.render(state);
			renderingEngine.showMessage(result.getText());
		}
		renderingEngine.showMessage(GAME_OVER_BANNER);
	}
}
