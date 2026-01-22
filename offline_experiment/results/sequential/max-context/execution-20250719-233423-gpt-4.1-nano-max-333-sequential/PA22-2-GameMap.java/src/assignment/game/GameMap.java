
public static GameMap parse(String mapText) {
    if (mapText == null || mapText.isEmpty()) {
        throw new IllegalArgumentException("Map text cannot be null or empty");
    }
    String[] lines = mapText.split("\\r?\\n");
    if (lines.length < 2) {
        throw new IllegalArgumentException("Map text must contain at least undo limit and one map line");
    }
    int undoLimit;
    try {
        undoLimit = Integer.parseInt(lines[0].trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid undo limit format");
    }
    if (undoLimit < -1) {
        throw new IllegalArgumentException("Undo limit cannot be less than -1");
    }
    int height = lines.length - 1;
    int width = lines[1].length();
    // Validate that all lines have the same length
    for (int i = 2; i <= height; i++) {
        if (lines[i].length() != width) {
            throw new IllegalArgumentException("All map lines must have the same length");
        }
    }
    Map<Position, Entity> mapEntities = new HashMap<>();
    Set<Position> destinations = new HashSet<>();
    Map<Character, Position> playersPositions = new HashMap<>();
    Map<Character, List<Position>> boxesByPlayerId = new HashMap<>();
    Set<Character> playerIds = new HashSet<>();
    for (int y = 0; y < height; y++) {
        String line = lines[y + 1];
        for (int x = 0; x < width; x++) {
            char ch = line.charAt(x);
            Position pos = Position.of(x, y);
            switch (ch) {
                case '#':
                    mapEntities.put(pos, new assignment.entities.Wall());
                    break;
                case '@':
                    destinations.add(pos);
                    mapEntities.put(pos, new assignment.entities.Empty());
                    break;
                case '.':
                    mapEntities.put(pos, new assignment.entities.Empty());
                    break;
                default:
                    if (Character.isUpperCase(ch)) {
                        if (playersPositions.containsKey(ch)) {
                            throw new IllegalArgumentException("Multiple players with same ID: " + ch);
                        }
                        playersPositions.put(ch, pos);
                        playerIds.add(ch);
                        mapEntities.put(pos, new assignment.entities.Player(ch));
                    } else if (Character.isLowerCase(ch)) {
                        char playerId = Character.toUpperCase(ch);
                        mapEntities.put(pos, new assignment.entities.Box(playerId));
                        boxesByPlayerId.computeIfAbsent(playerId, k -> new ArrayList<>()).add(pos);
                    } else {
                        mapEntities.put(pos, new assignment.entities.Empty());
                    }
                    break;
            }
        }
    }
    if (playerIds.isEmpty()) {
        throw new IllegalArgumentException("No players found in the map");
    }
    int totalBoxes = boxesByPlayerId.values().stream().mapToInt(List::size).sum();
    int totalDestinations = destinations.size();
    if (totalBoxes != totalDestinations) {
        throw new IllegalArgumentException("Number of boxes (" + totalBoxes
                + ") does not match number of destinations (" + totalDestinations + ")");
    }
    for (Map.Entry<Character, List<Position>> entry : boxesByPlayerId.entrySet()) {
        char pid = entry.getKey();
        if (!playerIds.contains(pid)) {
            throw new IllegalArgumentException("Box with player ID " + pid + " has no corresponding player");
        }
    }
    for (char pid : playerIds) {
        if (!boxesByPlayerId.containsKey(pid) || boxesByPlayerId.get(pid).isEmpty()) {
            throw new IllegalArgumentException("Player " + pid + " has no boxes");
        }
    }
    GameMap gameMap = new GameMap(width, height, destinations, undoLimit);
    for (Map.Entry<Position, Entity> entry : mapEntities.entrySet()) {
        gameMap.putEntity(entry.getKey(), entry.getValue());
    }
    return gameMap;
}