
/**
 * Constructs a new GameState from the given GameMap.
 *
 * @param map the GameMap to initialize the GameState.
 */
public GameState(GameMap map) {
    this.entities = new HashMap<>(map.map);
    this.boardWidth = map.getMaxWidth();
    this.boardHeight = map.getMaxHeight();
    this.destinations = new HashSet<>(map.getDestinations());
    this.undoQuota = map.getUndoLimit().orElse(-1);
}