
public static Game createGame(int size, int numMovesProtection) {
    if (size < 4) {
        throw new IllegalArgumentException("Board size must be at least 4");
    }
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    Player[] players = new Player[] { userPlayer, computerPlayer };
    Configuration configuration = new Configuration(size, players, numMovesProtection);

    // Place computer player's pieces on the top two rows
    for (int col = 0; col < size; col++) {
        configuration.addInitialPiece(new Knight(computerPlayer), 0, col);
        configuration.addInitialPiece(new Archer(computerPlayer), 1, col);
    }

    // Place user player's pieces on the bottom two rows
    for (int col = 0; col < size; col++) {
        configuration.addInitialPiece(new Archer(userPlayer), size - 2, col);
        configuration.addInitialPiece(new Knight(userPlayer), size - 1, col);
    }

    return new JesonMor(configuration);
}