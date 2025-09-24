
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.ActionResult;
import assignment.game.AbstractSokobanGame;
import assignment.game.GameState;
import assignment.game.InputEngine;
import assignment.game.RenderingEngine;

import static assignment.utils.StringResources.TOO_MANY_PLAYERS;

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
    public TerminalSokobanGame(GameState gameState,
                               InputEngine inputEngine,
                               RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;

        // Check the number of players (max. 2 in terminal mode)
        int playerCount = gameState.players().size();
        if (playerCount > 2) {
            throw new IllegalArgumentException(
                String.format(TOO_MANY_PLAYERS, playerCount)
            );
        }
    }

    @Override
    public void run() {
        boolean continueGame = true;
        while (continueGame) {
            // Render the current state
            renderingEngine.render(state());

            // Read an action from the user
            Action action = inputEngine.readAction();

            // Execute the action
            ActionResult result = execute(action);

            // If there was an error, report it
            if (result.isError()) {
                System.out.println(result.errorMessage());
            }

            // If the game is over (win/quit), end the loop
            if (result.isFinished()) {
                System.out.println(result.infoMessage());
                continueGame = false;
            }
        }
    }
}