
public static GameMap parse(String mapText) {
    if (mapText == null || mapText.trim().isEmpty()) {
        throw new IllegalArgumentException("Map text cannot be null or empty");
    }
    String[] lines = mapText.lines().toArray(String[]::new);
    if (lines.length < 2) {
        throw new IllegalArgumentException("Map text must contain at least two lines (undo limit + map)");
    }
    int undoLimit;
    try {
        undoLimit = Integer.parseInt(lines[0].trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid undo limit");
    }
    if (undoLimit < -1) {
        throw new IllegalArgumentException("Undo limit cannot be less than -1");
    }
    List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
    int height = mapLines.size();
    int width = mapLines.stream().mapToInt(String::length).max().orElse(0);
    Set<Position> destinations = new HashSet<>();
    Map<Position, Entity> entities = new HashMap<>();
    Map<Character, Position> playersPositions = new HashMap<>();
    Map<Character, Box> boxes = new HashMap<>();
    Set<Character> playerIds = new HashSet<>();
    Set<Character> boxIds = new HashSet<>();
    for (int y = 0; y < height; y++) {
        String line = mapLines.get(y);
        for (int x = 0; x < line.length(); x++) {
            char ch = line.charAt(x);
            Position pos = new Position(x, y);
            switch (ch) {
                case '#':
                    entities.put(pos, new Wall());
                    break;
                case '@':
                    destinations.add(pos);
                    entities.put(pos, new Empty());
                    break;
                case '.':
                    entities.put(pos, new Empty());
                    break;
                default:
                    if (Character.isUpperCase(ch)) {
                        if (playerIds.contains(ch)) {
                            throw new IllegalArgumentException("Multiple players with same ID: " + ch);
                        }
                        playerIds.add(ch);
                        if (playersPositions.containsKey(ch)) {
                            throw new IllegalArgumentException("Duplicate player ID: " + ch);
                        }
                        playersPositions.put(ch, pos);
                        entities.put(pos, new Player(ch));
                    } else if (Character.isLowerCase(ch)) {
                        if (boxIds.contains(ch)) {
                            throw new IllegalArgumentException("Duplicate box ID: " + ch);
                        }
                        Box box = new Box(Character.toUpperCase(ch));
                        boxes.put(ch, box);
                        entities.put(pos, box);
                        boxIds.add(ch);
                    } else {
                        throw new IllegalArgumentException("Invalid map character: " + ch);
                    }
            }
        }
    }
    if (playerIds.isEmpty()) {
        throw new IllegalArgumentException("No players found in the map");
    }
    // Validate that each box's player ID corresponds to an existing Player entity
    for (Map.Entry<Character, Box> entry : boxes.entrySet()) {
        char boxChar = entry.getKey();
        char boxIdUpper = Character.toUpperCase(boxChar);
        boolean playerExists = entities.values().stream()
            .anyMatch(entity -> entity instanceof Player && ((Player) entity).getId() == boxIdUpper);
        if (!playerExists) {
            throw new IllegalArgumentException(
                "Box with ID " + boxChar + " has invalid player ID " + boxIdUpper);
        }
    }
    long boxCount = boxes.size();
    long destinationCount = destinations.size();
    if (boxCount != destinationCount) {
        throw new IllegalArgumentException("Number of boxes does not match number of destinations");
    }
    return new GameMap(width, height, destinations, undoLimit);
}