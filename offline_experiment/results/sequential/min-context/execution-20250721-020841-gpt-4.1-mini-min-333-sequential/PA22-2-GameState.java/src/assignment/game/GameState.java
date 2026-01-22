
public GameState(GameMap map) {
    this.boardWidth = map.getMaxWidth();
    this.boardHeight = map.getMaxHeight();
    this.undoQuota = map.getUndoQuota().orElse(-1);
    this.entities = new HashMap<>();
    for (int y = 0; y < boardHeight; y++) {
        for (int x = 0; x < boardWidth; x++) {
            Position pos = new Position(x, y);
            Entity entity = map.getEntity(pos);
            this.entities.put(pos, entity);
        }
    }
    this.destinations = new HashSet<>(map.getDestinations());
    this.history.clear();
    this.currentTransition = new Transition();
}