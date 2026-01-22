
package pa1.controller;

import pa1.model.*;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

/**
 * Controller for {@link pa1.InertiaTextGame}.
 *
 * <p>
 * All game state mutations should be performed by this class.
 * </p>
 */
public class GameController {

    @NotNull
    private final GameState gameState;

    /**
     * Constructor accepting a GameState instance.
     *
     * @param gameState The game state to control.
     */
    public GameController(@NotNull GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        MoveResult lastMove = gameState.getMoveStack().pop();
        if (lastMove == null) {
            // No moves to undo
            return false;
        }
        if (!(lastMove instanceof MoveResult.Valid.Alive aliveMove)) {
            // The last move was invalid or not an alive move; nothing to undo
            return false;
        }

        // Revert the player's position to the original position
        gameState.getGameBoardController().undoMove(aliveMove);

        // No need to manually adjust move count here; assume processMove handles move count
        return true;
    }

    // Additional methods can be added here as needed...
}