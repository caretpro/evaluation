
/**
 * Creates a new GameController with the specified game state.
 *
 * @param gameState The game state to control.
 */
public GameController(final GameState gameState) {
    this.gameState = Objects.requireNonNull(gameState, "gameState must not be null");
}