
/**
 * Create a running game state from a game map.
 * @param map  the game map from which to create this game state.
 */
public GameState(GameMap map) {
    this.boardWidth = map.getMaxWidth();
    this.boardHeight = map.getMaxHeight();
    this.destinations = map.getDestinations();
    this.undoQuota = map.getUndoLimit().orElse(-1);
    this.entities = new HashMap<>();
    for (int y = 0; y < boardHeight; y++) {
        for (int x = 0; x < boardWidth; x++) {
            Position position = Position.of(x, y);
            Entity entity = map.getEntity(position);
            if (entity != null) {
                this.entities.put(position, entity);
            } else {
                this.entities.put(position, new Empty());
            }
        }
    }
}