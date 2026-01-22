
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

    // User player's pieces on the top row (row 0)
    configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
    configuration.addInitialPiece(new Archer(userPlayer), 1, 0);

    // Computer player's pieces on the bottom row (row size - 1)
    configuration.addInitialPiece(new Knight(computerPlayer), 0, size - 1);
    configuration.addInitialPiece(new Archer(computerPlayer), 1, size - 1);

    return new JesonMor(configuration);
}