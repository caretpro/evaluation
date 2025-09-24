
/**
 * Constructs a new GameController with the given GameState.
 *
 * @param gameState the game state to control; must not be null
 */
public GameController(final GameState gameState) {
    this.gameState = Objects.requireNonNull(gameState, "gameState must not be null");
}