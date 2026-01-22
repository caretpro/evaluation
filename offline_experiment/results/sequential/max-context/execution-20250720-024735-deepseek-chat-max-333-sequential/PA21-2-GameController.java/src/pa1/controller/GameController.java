
/**
 * Creates an instance.
 * @param gameState  The instance of {@link GameState} to control.
 */
public GameController(@NotNull final GameState gameState) {
    this.gameState = Objects.requireNonNull(gameState);
}