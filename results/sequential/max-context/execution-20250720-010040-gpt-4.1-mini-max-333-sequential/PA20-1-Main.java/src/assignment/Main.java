
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

    var central = configuration.getCentralPlace();

    int bottomY = size - 1;
    int topY = 0;

    // User player's pieces (bottom row)
    // Knights at corners
    configuration.addInitialPiece(new Knight(userPlayer), 0, bottomY);
    configuration.addInitialPiece(new Knight(userPlayer), size - 1, bottomY);
    // One Archer next to left corner (1, bottomY), avoid central place
    if (!(1 == central.x() && bottomY == central.y())) {
        configuration.addInitialPiece(new Archer(userPlayer), 1, bottomY);
    } else {
        // If central is at (1,bottomY), place Archer at (2,bottomY)
        configuration.addInitialPiece(new Archer(userPlayer), 2, bottomY);
    }

    // Computer player's pieces (top row)
    // Knights at corners
    configuration.addInitialPiece(new Knight(computerPlayer), 0, topY);
    configuration.addInitialPiece(new Knight(computerPlayer), size - 1, topY);
    // One Archer next to left corner (1, topY), avoid central place
    if (!(1 == central.x() && topY == central.y())) {
        configuration.addInitialPiece(new Archer(computerPlayer), 1, topY);
    } else {
        // If central is at (1,topY), place Archer at (2,topY)
        configuration.addInitialPiece(new Archer(computerPlayer), 2, topY);
    }

    return new JesonMor(configuration);
}