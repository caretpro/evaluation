
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

    // User player's pieces (placed on bottom-left corner and adjacent)
    Knight userKnight = new Knight(userPlayer);
    Archer userArcher = new Archer(userPlayer);
    configuration.addInitialPiece(userKnight, size - 1, 0);       // bottom-left corner
    configuration.addInitialPiece(userArcher, size - 1, 1);       // bottom row, next to knight

    // Computer player's pieces (placed on top-right corner and adjacent)
    Knight compKnight = new Knight(computerPlayer);
    Archer compArcher = new Archer(computerPlayer);
    configuration.addInitialPiece(compKnight, 0, size - 1);       // top-right corner
    configuration.addInitialPiece(compArcher, 0, size - 2);       // top row, next to knight

    return new JesonMor(configuration);
}