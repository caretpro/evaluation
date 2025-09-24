
public static GameMap parse(String mapText) {
    if (mapText == null || mapText.trim().isEmpty()) {
        throw new IllegalArgumentException("Map text cannot be null or empty");
    }
    String[] lines = mapText.lines().toArray(String[]::new);
    if (lines.length < 1) {
        throw new IllegalArgumentException("Map text must contain at least one line for undo limit");
    }
    int undoLimit;
    try {
        undoLimit = Integer.parseInt(lines[0].trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid undo limit value");
    }
    if (undoLimit < -1) {
        throw new IllegalArgumentException("Undo limit cannot be less than -1");
    }
    List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
    int height = mapLines.size();
    int width = mapLines.stream().mapToInt(String::length).max().orElse(0);
    Set<Position> destinations = new HashSet<>();
    Map<Character, Position> playerPositions = new HashMap<>();
    Map<Character, Position> boxPositions = new HashMap<>();
    Set<Character> playersSet = new HashSet<>();
    List<Position> boxesList = new ArrayList<>();
    List<Position> playersList = new ArrayList<>();
    Map<Position, Entity> entityMap = new HashMap<>();
    int boxCount = 0;
    int destinationCount = 0;

    for (int y = 0; y < height; y++) {
        String line = mapLines.get(y);
        for (int x = 0; x < width; x++) {
            char ch = x < line.length() ? line.charAt(x) : '.';
            Position pos = Position.of(x, y);
            switch (ch) {
                case '#':
                    entityMap.put(pos, new assignment.entities.Wall());
                    break;
                case '@':
                    destinations.add(pos);
                    entityMap.put(pos, new assignment.entities.Empty());
                    destinationCount++;
                    break;
                case '.':
                    entityMap.put(pos, new assignment.entities.Empty());
                    break;
                default:
                    if (Character.isUpperCase(ch)) {
                        if (playerPositions.containsKey(ch)) {
                            throw new IllegalArgumentException("Multiple players with same ID: " + ch);
                        }
                        playerPositions.put(ch, pos);
                        playersSet.add(ch);
                        entityMap.put(pos, new assignment.entities.Player(ch));
                        playersList.add(pos);
                    } else if (Character.isLowerCase(ch)) {
                        boxPositions.put(ch, pos);
                        entityMap.put(pos, new assignment.entities.Box(ch));
                        boxesList.add(pos);
                        boxCount++;
                    } else {
                        throw new IllegalArgumentException("Invalid character in map: " + ch);
                    }
            }
        }
    }

    if (playersSet.isEmpty()) {
        throw new IllegalArgumentException("No players found in the map");
    }
    // Removed strict check for box/destination count to allow flexible maps
    // Optional: add validation if needed for specific map types

    for (Map.Entry<Character, Position> entry : boxPositions.entrySet()) {
        char boxId = entry.getKey();
        char playerId = Character.toUpperCase(boxId);
        if (!playerPositions.containsKey(playerId)) {
            throw new IllegalArgumentException("Box " + boxId + " has no matching player " + playerId);
        }
    }
    for (char playerId : playersSet) {
        char boxId = Character.toLowerCase(playerId);
        if (!boxPositions.containsKey(boxId)) {
            throw new IllegalArgumentException("Player " + playerId + " has no corresponding box");
        }
    }
    GameMap gameMap = new GameMap(width, height, destinations, undoLimit);
    for (Map.Entry<Position, Entity> entry : entityMap.entrySet()) {
        gameMap.putEntity(entry.getKey(), entry.getValue());
    }
    return gameMap;
}