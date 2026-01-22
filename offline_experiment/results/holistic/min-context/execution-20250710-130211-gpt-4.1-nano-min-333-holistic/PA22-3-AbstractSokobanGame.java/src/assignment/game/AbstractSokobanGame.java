
package assignment.tui;

import assignment.game.GameState;
import assignment.actions.Action;
import assignment.actions.ActionResult;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of TerminalSokobanGame with proper constructor.
 */
public class TerminalSokobanGame {
    private final @NotNull GameState gameState;
    private final @NotNull TerminalInputEngine inputEngine;
    private final @NotNull TerminalRenderingEngine renderingEngine;

    /**
     * Constructor accepting GameState, TerminalInputEngine, and TerminalRenderingEngine.
     */
    public TerminalSokobanGame(@NotNull GameState gameState,
                               @NotNull TerminalInputEngine inputEngine,
                               @NotNull TerminalRenderingEngine renderingEngine) {
        this.gameState = gameState;
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    // Additional methods for game logic can be added here...
}