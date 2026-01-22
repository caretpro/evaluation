
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer", assignment.protocol.Color.GREEN);
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer", assignment.protocol.Color.BLUE);

    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

    var center = configuration.getCentralPlace();

    // User player pieces on top row (y=0)
    configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
    configuration.addInitialPiece(new Knight(userPlayer), size - 1, 0);

    // Place one archer for user player on second row (y=1), avoid center
    int archerXUser = size / 2;
    int archerYUser = 1;
    if (archerXUser == center.x() && archerYUser == center.y()) {
        archerYUser++; // move down if conflicts with center
    }
    configuration.addInitialPiece(new Archer(userPlayer), archerXUser, archerYUser);

    // Computer player pieces on bottom row (y=size-1)
    configuration.addInitialPiece(new Knight(computerPlayer), 0, size - 1);
    configuration.addInitialPiece(new Knight(computerPlayer), size - 1, size - 1);

    // Place one archer for computer player on second last row (y=size-2), avoid center
    int archerXComp = size / 2;
    int archerYComp = size - 2;
    if (archerXComp == center.x() && archerYComp == center.y()) {
        archerYComp--; // move up if conflicts with center
    }
    configuration.addInitialPiece(new Archer(computerPlayer), archerXComp, archerYComp);

    return new JesonMor(configuration);
}