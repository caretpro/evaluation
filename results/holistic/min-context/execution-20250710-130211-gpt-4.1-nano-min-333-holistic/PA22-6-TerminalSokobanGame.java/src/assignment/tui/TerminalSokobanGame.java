
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
     * @param inputEngine     The terminal input engine.
     * @param renderingEngine The terminal rendering engine.
     * @throws IllegalArgumentException when there are more than two players in the map.
     */
    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine,
            RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;

        // Check the number of players in the game map
        long playerCount = gameState.getMap().getPlayers().stream().count();
        if (playerCount > 2) {
            throw new IllegalArgumentException("The game supports at most two players.");
        }
    }

    @Override
    public void run() {
        boolean gameRunning = true;
        while (gameRunning) {
            // Render the current game state
            renderingEngine.render(getGameState());

            // Get user input
            String input = inputEngine.readInput();

            // Process input into an action
            Action action = parseInputToAction(input);
            if (action == null) {
                // Invalid input, prompt again
                continue;
            }

            // Execute the action
            ActionResult result = action.execute(getGameState());

            // Check for game completion or exit command
            if (result.isGameOver()) {
                renderingEngine.renderGameOver(getGameState());
                gameRunning = false;
            } else if (result.isExit()) {
                renderingEngine.renderExitMessage();
                gameRunning = false;
            }
        }
    }

    /**
     * Parses user input string into an Action.
     * This method should be implemented based on the game's input scheme.
     */
    private Action parseInputToAction(String input) {
        switch (input.trim().toLowerCase()) {
            case "w":
                return getActionByName("MOVE_UP");
            case "a":
                return getActionByName("MOVE_LEFT");
            case "s":
                return getActionByName("MOVE_DOWN");
            case "d":
                return getActionByName("MOVE_RIGHT");
            case "quit":
                return getActionByName("EXIT");
            default:
                return null;
        }
    }

    /**
     * Helper method to retrieve an Action by its name.
     */
    private Action getActionByName(String actionName) {
        // Placeholder: replace with actual action retrieval logic
        return null;
    }
}