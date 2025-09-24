
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer", assignment.protocol.Color.GREEN);
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer", assignment.protocol.Color.BLUE);

    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

    var center = configuration.getCentralPlace();

    // Create pieces
    Knight userKnight = new Knight(userPlayer);
    Archer userArcher = new Archer(userPlayer);

    Knight compKnight = new Knight(computerPlayer);
    Archer compArcher = new Archer(computerPlayer);

    // Positions for user player (bottom row)
    int userY = size - 1;
    int userKnightX = 0;
    int userArcherX = size - 1;

    // Avoid central place for user pieces
    if (new Place(userKnightX, userY).equals(center)) {
        userKnightX = 1;
    }
    if (new Place(userArcherX, userY).equals(center)) {
        userArcherX = size - 2;
    }

    // Positions for computer player (top row)
    int compY = 0;
    int compKnightX = 0;
    int compArcherX = size - 1;

    // Avoid central place for computer pieces
    if (new Place(compKnightX, compY).equals(center)) {
        compKnightX = 1;
    }
    if (new Place(compArcherX, compY).equals(center)) {
        compArcherX = size - 2;
    }

    // Add pieces to configuration
    configuration.addInitialPiece(userKnight, userKnightX, userY);
    configuration.addInitialPiece(userArcher, userArcherX, userY);

    configuration.addInitialPiece(compKnight, compKnightX, compY);
    configuration.addInitialPiece(compArcher, compArcherX, compY);

    return new JesonMor(configuration);
}