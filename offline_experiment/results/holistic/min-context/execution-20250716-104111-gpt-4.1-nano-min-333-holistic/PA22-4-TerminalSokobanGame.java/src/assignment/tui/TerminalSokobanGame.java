
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;

    private final RenderingEngine renderingEngine;

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
    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine,
            RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        // Check the number of players in the game state
        long playerCount = gameState.getPlayers().stream()
                .filter(player -> player.getType() == 'P') // assuming 'P' represents a player
                .count();
        if (playerCount > 2) {
            throw new IllegalArgumentException("The game supports at most two players.");
        }
    }

    @Override
    public void run() {
        boolean gameOver = false;
        while (!gameOver) {
            // Render current game state
            renderingEngine.render(getGameState());
            // Get user input
            String input = inputEngine.read();
            // Process input into an action
            Action action = parseInputToAction(input);
            if (action != null) {
                // Perform action
                ActionResult result = performAction(action);
                // Check if game is over
                if (result.isGameOver()) {
                    gameOver = true;
                    renderingEngine.renderGameOver(result);
                }
            }
        }
    }

    private Action parseInputToAction(String input) {
        switch (input.toLowerCase()) {
            case "w": return Action.MOVE_UP;
            case "a": return Action.MOVE_LEFT;
            case "s": return Action.MOVE_DOWN;
            case "d": return Action.MOVE_RIGHT;
            case "q": return Action.QUIT;
            default: return null;
        }
    }
}