
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.actions.MoveAction;
import assignment.actions.QuitAction;
import assignment.actions.Direction;
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
        int playerCount = gameState.getPlayerCount(); // Assuming getPlayerCount() exists
        if (playerCount > 2) {
            throw new IllegalArgumentException("TerminalSokobanGame supports at most two players. Found: " + playerCount);
        }
    }

    @Override
    public void run() {
        boolean gameOver = false;
        while (!gameOver) {
            // Render current game state
            renderingEngine.render(this.getGameState());

            // Get user input
            String input = inputEngine.readInput();

            // Process input into an action
            Action action = parseInputToAction(input);
            if (action == null) {
                // Invalid input, continue to next iteration
                continue;
            }

            // Execute action
            ActionResult result = action.execute(this.getGameState());

            // Check if the game has ended
            if (result.isGameOver()) {
                gameOver = true;
                // Render final state
                renderingEngine.render(this.getGameState());
                // Display game over message
                System.out.println(GAME_OVER_MESSAGE);
            }
        }
    }

    /**
     * Parses user input string into an Action.
     * This method should map input commands to game actions.
     *
     * @param input the user input string.
     * @return the corresponding Action, or null if input is invalid.
     */
    private Action parseInputToAction(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        switch (input.trim().toLowerCase()) {
            case "w":
                return new MoveAction(Direction.UP);
            case "s":
                return new MoveAction(Direction.DOWN);
            case "a":
                return new MoveAction(Direction.LEFT);
            case "d":
                return new MoveAction(Direction.RIGHT);
            case "q":
                return new QuitAction();
            default:
                return null;
        }
    }
}