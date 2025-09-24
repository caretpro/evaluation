
@Override
public Action fetchAction() {
    final String inputLine = terminalScanner.nextLine().trim();

    // 1) Exit command?
    if (EXIT_COMMAND_TEXT.equalsIgnoreCase(inputLine)) {
        return new Exit(0);
    }

    // 2) Undo command?
    if (inputLine.equalsIgnoreCase("U")) {
        return new Undo(0);
    }

    // 3) Move commands (W/A/S/D or H/J/K/L)
    final Matcher m = MOVE_REGEX.matcher(inputLine);
    if (m.matches()) {
        char c = Character.toUpperCase(m.group("action").charAt(0));
        return switch (c) {
            case 'W'       -> new Move.Up(0);
            case 'A'       -> new Move.Left(0);
            case 'S'       -> new Move.Down(0);
            case 'D'       -> new Move.Right(0);
            case 'H'       -> new Move.Left(0);
            case 'J'       -> new Move.Down(0);
            case 'K'       -> new Move.Up(0);
            case 'L'       -> new Move.Right(0);
            default        -> throw new ShouldNotReachException();
        };
    }

    // 4) invalid input → signal user and return InvalidInput
    System.out.println(INVALID_INPUT_MESSAGE);
    return new InvalidInput(-1, INVALID_INPUT_MESSAGE);
}