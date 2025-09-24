
/**
 * Create a running game state from a game map.
 * @param map the game map from which to create this game state.
 */
public GameState(GameMap map) {
    this.boardWidth = map.getMaxWidth();
    this.boardHeight = map.getMaxHeight();
    this.destinations = new HashSet<>(map.getDestinations());
    this.undoQuota = map.getUndoQuota().orElse(-1);
    this.entities = new HashMap<>();
    for (Map.Entry<Position, Entity> entry : map.getEntities().entrySet()) {
        this.entities.put(entry.getKey(), entry.getValue());
    }
}