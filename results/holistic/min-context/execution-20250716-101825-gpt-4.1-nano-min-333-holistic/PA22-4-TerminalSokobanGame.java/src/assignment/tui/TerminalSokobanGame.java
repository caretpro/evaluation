
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
        // Check the number of players
        long playerCount = getGameState().getPlayers().stream().count();
        if (playerCount > 2) {
            throw new IllegalArgumentException("TerminalSokobanGame supports at most two players.");
        }
    }

    @Override
    public void run() {
        boolean gameOver = false;
        while (!gameOver) {
            // Render the current game state
            renderingEngine.render(getGameState());
            // Get user input
            String input = inputEngine.readInput();
            // Process input into an action
            Action action = parseInputToAction(input);
            if (action != null) {
                // Execute the action
                ActionResult result = action.execute(getGameState());
                // Check if the game has ended
                if (result.isGameOver()) {
                    gameOver = true;
                }
            }
        }
        // Final render to show end state
        renderingEngine.render(getGameState());
    }

    /**
     * Parses user input string into an Action.
     * This method should be implemented based on input commands.
     */
    private Action parseInputToAction(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        switch (input.trim().toLowerCase()) {
            case "w":
                return getActionForDirection("UP");
            case "s":
                return getActionForDirection("DOWN");
            case "a":
                return getActionForDirection("LEFT");
            case "d":
                return getActionForDirection("RIGHT");
            case "q":
                return getQuitAction();
            default:
                return null;
        }
    }

    /**
     * Returns an Action corresponding to a direction.
     */
    private Action getActionForDirection(String direction) {
        // Replace with actual implementation to create direction actions
        // For example, assuming an Action implementation like MoveAction
        // return new MoveAction(direction);
        return null; // Placeholder
    }

    /**
     * Returns an Action to quit the game.
     */
    private Action getQuitAction() {
        // Replace with actual quit action implementation
        // For example, assuming an Action implementation like QuitAction
        // return new QuitAction();
        return null; // Placeholder
    }
}