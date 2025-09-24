package pa1.controller;

import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;
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
     * Creates an instance.
     *
     * @param gameState The instance of {@link GameState} to control.
     */
    public GameController(@NotNull final GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Processes a Move action performed by the player.
     *
     * @param direction The direction the player wants to move to.
     * @return An instance of {@link MoveResult} indicating the result of the action.
     */
    public MoveResult processMove(@NotNull final Direction direction) {
        Objects.requireNonNull(direction);

        final var result = this.gameState.getGameBoardController().makeMove(direction);

        if (result instanceof MoveResult.Valid v) {
            this.gameState.incrementNumMoves();

            if (v instanceof MoveResult.Valid.Alive va) {
                this.gameState.increaseNumLives(va.collectedExtraLives.size());

                this.gameState.getMoveStack().push(va);
            } else if (v instanceof MoveResult.Valid.Dead) {
                this.gameState.incrementNumDeaths();
                this.gameState.decrementNumLives();
            }
        }

        return result;
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        if (this.gameState.getMoveStack().isEmpty()) {
            return false;
        }

        final var prevState = this.gameState.getMoveStack().pop();
        // This condition is impossible under this implementation, but just do it anyways.
        if (!(prevState instanceof MoveResult.Valid.Alive)) {
            return false;
        }

        MoveResult.Valid.Alive aliveState = (MoveResult.Valid.Alive) prevState;
        this.gameState.decreaseNumLives(aliveState.collectedExtraLives.size());

        this.gameState.getGameBoardController().undoMove(aliveState);
        return true;
    }
}
