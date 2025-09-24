
public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine,
        TerminalRenderingEngine renderingEngine) {
    super(gameState);
    this.inputEngine = inputEngine;
    this.renderingEngine = renderingEngine;
    int playerCount = gameState.getAllPlayerPositions().size();
    if (playerCount > 2) {
        throw new IllegalArgumentException(
                "Terminal-based game supports at most two players, but found " + playerCount);
    }
}