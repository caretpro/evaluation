
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer", assignment.protocol.Color.GREEN);
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer", assignment.protocol.Color.BLUE);

    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

    var central = configuration.getCentralPlace();

    // User player's pieces (bottom side)
    // Knights at bottom-left and bottom-right corners
    configuration.addInitialPiece(new Knight(userPlayer), 0, size - 1);
    configuration.addInitialPiece(new Knight(userPlayer), size - 1, size - 1);

    // One Archer placed one row above bottom row, centered but not on central place
    int archerXUser = central.x();
    int archerYUser = size - 2;
    if (archerXUser == central.x() && archerYUser == central.y()) {
        // If central place conflicts, shift archer one to the right
        archerXUser = Math.min(size - 1, archerXUser + 1);
    }
    configuration.addInitialPiece(new Archer(userPlayer), archerXUser, archerYUser);

    // Computer player's pieces (top side)
    // Knights at top-left and top-right corners
    configuration.addInitialPiece(new Knight(computerPlayer), 0, 0);
    configuration.addInitialPiece(new Knight(computerPlayer), size - 1, 0);

    // One Archer placed one row below top row, centered but not on central place
    int archerXComp = central.x();
    int archerYComp = 1;
    if (archerXComp == central.x() && archerYComp == central.y()) {
        // If central place conflicts, shift archer one to the right
        archerXComp = Math.min(size - 1, archerXComp + 1);
    }
    configuration.addInitialPiece(new Archer(computerPlayer), archerXComp, archerYComp);

    return new JesonMor(configuration);
}