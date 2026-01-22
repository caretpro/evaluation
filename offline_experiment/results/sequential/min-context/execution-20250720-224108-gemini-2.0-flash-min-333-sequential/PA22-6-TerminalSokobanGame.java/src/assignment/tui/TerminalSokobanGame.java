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
		if (gameState.getPlayerPositions().size() > 2) {
			throw new IllegalArgumentException("Terminal-based game only supports at most two players.");
		}
	}

	@Override
	public void run() {
		renderingEngine.render(getGameState());
		while (!getGameState().isWin()) {
			Action action = inputEngine.getNextAction(getGameState());
			if (action == null) {
				continue;
			}
			ActionResult result = action.execute();
			if (result.isSuccessful()) {
				setGameState(result.getNewGameState());
				renderingEngine.render(getGameState());
			} else {
				renderingEngine.renderMessage(result.getMessage());
			}
		}
		renderingEngine.renderMessage(WIN_MESSAGE);
	}
}
