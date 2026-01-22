
public GameState(GameMap map) {
    this.boardWidth = map.getMaxWidth();
    this.boardHeight = map.getMaxHeight();
    this.destinations = Collections.unmodifiableSet(new HashSet<>(map.getDestinations()));
    this.undoQuota = map.getUndoQuota();
    Map<Position, Entity> tempEntities = new HashMap<>();
    for (int y = 0; y < boardHeight; y++) {
        for (int x = 0; x < boardWidth; x++) {
            Position pos = new Position(x, y);
            Entity entity = map.getEntity(pos);
            tempEntities.put(pos, entity);
        }
    }
    this.entities = Collections.unmodifiableMap(tempEntities);
    this.history.clear();
    this.currentTransition = new Transition();
}