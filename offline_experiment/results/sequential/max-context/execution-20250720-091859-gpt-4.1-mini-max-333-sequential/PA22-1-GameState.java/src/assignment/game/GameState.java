
/**
 * Constructs a new GameState from the given GameMap.
 *
 * @param map the game map to initialize the game state.
 */
public GameState(GameMap map) {
    this.entities = new HashMap<>(map.map);
    this.boardWidth = map.getMaxWidth();
    this.boardHeight = map.getMaxHeight();
    this.destinations = new HashSet<>(map.getDestinations());
    this.undoQuota = map.getUndoLimit().orElse(-1);
    this.history.clear();
    this.currentTransition = new Transition();
}