
public static GameMap parse(String mapText) {
    if (mapText == null || mapText.trim().isEmpty()) {
        throw new IllegalArgumentException("Map text cannot be null or empty");
    }
    String[] lines = mapText.split("\\r?\\n");
    if (lines.length < 2) {
        throw new IllegalArgumentException("Map must contain at least two lines (undo limit + map)");
    }
    int undoLimit;
    try {
        undoLimit = Integer.parseInt(lines[0].trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid undo limit");
    }
    if (undoLimit < -1 || undoLimit == 0) {
        throw new IllegalArgumentException("Undo limit must be >= -1 and != 0");
    }
    List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
    int height = mapLines.size();
    int width = mapLines.stream().mapToInt(String::length).max().orElse(0);
    Set<Position> destinations = new HashSet<>();
    Map<Position, Entity> entityMap = new HashMap<>();
    Map<Character, Position> playersPositions = new HashMap<>();
    int playerCount = 0;

    for (int y = 0; y < height; y++) {
        String line = mapLines.get(y);
        for (int x = 0; x < width; x++) {
            char ch = x < line.length() ? line.charAt(x) : ' ';
            Position pos = Position.of(x, y);
            switch (ch) {
                case '#':
                    entityMap.put(pos, new assignment.entities.Wall());
                    break;
                case '@':
                    destinations.add(pos);
                    entityMap.put(pos, new assignment.entities.Empty());
                    break;
                case '.':
                case ' ':
                    entityMap.put(pos, new assignment.entities.Empty());
                    break;
                default:
                    if (Character.isUpperCase(ch)) {
                        if (playersPositions.containsKey(ch)) {
                            throw new IllegalArgumentException("Multiple players with same ID: " + ch);
                        }
                        playersPositions.put(ch, pos);
                        entityMap.put(pos, new assignment.entities.Player(ch - 'A'));
                        playerCount++;
                    } else if (Character.isLowerCase(ch)) {
                        char ownerChar = Character.toUpperCase(ch);
                        if (!playersPositions.containsKey(ownerChar)) {
                            throw new IllegalArgumentException("Box with owner " + ownerChar + " but no such player");
                        }
                        int ownerId = ownerChar - 'A';
                        entityMap.put(pos, new assignment.entities.Box(ownerId));
                    } else {
                        entityMap.put(pos, new assignment.entities.Empty());
                    }
            }
        }
    }
    if (playerCount == 0) {
        throw new IllegalArgumentException("No players found in the map");
    }
    return new GameMap(width, height, destinations, undoLimit).withEntities(entityMap);
}