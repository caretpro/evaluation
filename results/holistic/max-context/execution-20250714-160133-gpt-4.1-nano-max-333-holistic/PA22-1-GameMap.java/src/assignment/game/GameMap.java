
public static GameMap parse(String mapText) {
    String[] lines = mapText.lines().toArray(String[]::new);
    if (lines.length == 0) {
        throw new IllegalArgumentException("Map text is empty");
    }
    // Parse undo limit from first line
    int undoLimit;
    try {
        undoLimit = Integer.parseInt(lines[0].trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid undo limit");
    }
    if (undoLimit < -1) {
        throw new IllegalArgumentException("Undo limit cannot be less than -1");
    }

    Set<Position> destinations = new HashSet<>();
    Map<Position, Entity> entities = new HashMap<>();
    Map<Character, Player> playersMap = new HashMap<>();
    int boxCount = 0;

    List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
    int height = mapLines.size();
    int width = mapLines.stream().mapToInt(String::length).max().orElse(0);

    for (int y = 0; y < height; y++) {
        String line = mapLines.get(y);
        for (int x = 0; x < line.length(); x++) {
            char ch = line.charAt(x);
            Position pos = Position.of(x, y);
            switch (ch) {
                case '#':
                    entities.put(pos, new Wall());
                    break;
                case '@':
                    destinations.add(pos);
                    break;
                case '.':
                    entities.put(pos, new Empty());
                    break;
                default:
                    if (Character.isUpperCase(ch)) {
                        if (playersMap.containsKey(ch)) {
                            throw new IllegalArgumentException("Multiple players with same ID: " + ch);
                        }
                        Player player = new Player(ch - 'A' + 1);
                        playersMap.put(ch, player);
                        entities.put(pos, player);
                    } else if (Character.isLowerCase(ch)) {
                        char upper = Character.toUpperCase(ch);
                        if (!playersMap.containsKey(upper)) {
                            throw new IllegalArgumentException("Box " + ch + " has no matching player");
                        }
                        Box box = new Box(upper - 'A' + 1);
                        entities.put(pos, box);
                        boxCount++;
                    } else {
                        entities.put(pos, new Empty());
                    }
                    break;
            }
        }
    }

    if (playersMap.isEmpty()) {
        throw new IllegalArgumentException("No players found in the map");
    }

    int destinationCount = destinations.size();
    if (boxCount != destinationCount) {
        throw new IllegalArgumentException("Number of boxes (" + boxCount + ") does not match number of destinations (" + destinationCount + ")");
    }

    for (Box box : entities.values().stream().filter(e -> e instanceof Box).map(e -> (Box) e).toList()) {
        int playerId = box.getPlayerId();
        boolean playerExists = playersMap.values().stream().anyMatch(p -> p.getId() == playerId);
        if (!playerExists) {
            throw new IllegalArgumentException("Box with playerId " + playerId + " has no matching player");
        }
    }

    return new GameMap(width, height, destinations, undoLimit).withEntities(entities);
}