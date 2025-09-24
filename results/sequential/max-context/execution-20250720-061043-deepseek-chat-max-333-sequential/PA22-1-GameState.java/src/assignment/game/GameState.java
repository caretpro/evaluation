
/**
 * Create a running game state from a game map.
 * @param map  the game map from which to create this game state.
 */
public GameState(GameMap map) {
    this.entities = new HashMap<>();
    map.map.forEach((position, entity) -> this.entities.put(new Position(position.x(), position.y()), entity));
    this.boardWidth = map.getMaxWidth();
    this.boardHeight = map.getMaxHeight();
    this.destinations = new HashSet<>(map.getDestinations());
    this.undoQuota = map.getUndoLimit().orElse(-1);
}