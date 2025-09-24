
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

    var center = configuration.getCentralPlace();

    // Create pieces: one Knight and one Archer per player
    Knight userKnight = new Knight(userPlayer);
    Archer userArcher = new Archer(userPlayer);

    Knight compKnight = new Knight(computerPlayer);
    Archer compArcher = new Archer(computerPlayer);

    // Positions for user player (bottom-left corner)
    int bottomRow = size - 1;
    int leftCol = 0;

    // Place user Knight at bottom-left corner if not center
    if (!center.equals(new assignment.protocol.Place(leftCol, bottomRow))) {
        configuration.addInitialPiece(userKnight, leftCol, bottomRow);
    } else {
        // If center is bottom-left, place Knight one square above
        configuration.addInitialPiece(userKnight, leftCol, bottomRow - 1);
    }

    // Place user Archer next to Knight horizontally if not center
    if (!center.equals(new assignment.protocol.Place(leftCol + 1, bottomRow))) {
        configuration.addInitialPiece(userArcher, leftCol + 1, bottomRow);
    } else {
        // If center is here, place Archer one row above
        configuration.addInitialPiece(userArcher, leftCol + 1, bottomRow - 1);
    }

    // Positions for computer player (top-right corner)
    int topRow = 0;
    int rightCol = size - 1;

    // Place computer Knight at top-right corner if not center
    if (!center.equals(new assignment.protocol.Place(rightCol, topRow))) {
        configuration.addInitialPiece(compKnight, rightCol, topRow);
    } else {
        // If center is top-right, place Knight one square below
        configuration.addInitialPiece(compKnight, rightCol, topRow + 1);
    }

    // Place computer Archer next to Knight horizontally if not center
    if (!center.equals(new assignment.protocol.Place(rightCol - 1, topRow))) {
        configuration.addInitialPiece(compArcher, rightCol - 1, topRow);
    } else {
        // If center is here, place Archer one row below
        configuration.addInitialPiece(compArcher, rightCol - 1, topRow + 1);
    }

    return new JesonMor(configuration);
}