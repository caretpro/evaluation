
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
        long playerCount = gameState.getPlayers().stream().filter(p -> p != null).count();
        if (playerCount > 2) {
            throw new IllegalArgumentException("TerminalSokobanGame supports at most two players.");
        }
    }

    @Override
    public void run() {
        boolean gameRunning = true;
        while (gameRunning) {
            // Render current game state
            renderingEngine.render(this.getGameState());

            // Get user input
            String input = inputEngine.readInput();

            // Process input into an action
            Action action = parseInputToAction(input);
            if (action == null) {
                continue;
            }

            // Execute action
            ActionResult result = action.execute(this.getGameState());

            // Check if game is over
            if (result.isGameOver()) {
                renderingEngine.render(this.getGameState());
                System.out.println(GAME_OVER_MESSAGE);
                gameRunning = false;
            }
        }
    }

    /**
     * Parses user input string into an Action.
     * This method should be implemented based on specific input commands.
     */
    private Action parseInputToAction(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String command = input.trim().toLowerCase();
        switch (command) {
            case "w":
            case "up":
                return new MoveAction(Direction.UP);
            case "s":
            case "down":
                return new MoveAction(Direction.DOWN);
            case "a":
            case "left":
                return new MoveAction(Direction.LEFT);
            case "d":
            case "right":
                return new MoveAction(Direction.RIGHT);
            case "quit":
            case "q":
                return new QuitAction();
            default:
                return null;
        }
    }
}