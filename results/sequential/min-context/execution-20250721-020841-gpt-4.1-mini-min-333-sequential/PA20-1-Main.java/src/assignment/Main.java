
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

    // Place user player's pieces at bottom row corners
    configuration.addInitialPiece(new Knight(userPlayer), size - 1, 0);         // bottom-left corner
    configuration.addInitialPiece(new Archer(userPlayer), size - 1, size - 1);  // bottom-right corner

    // Place computer player's pieces at top row corners
    configuration.addInitialPiece(new Knight(computerPlayer), 0, 0);           // top-left corner
    configuration.addInitialPiece(new Archer(computerPlayer), 0, size - 1);    // top-right corner

    // No other pieces are placed, so other positions remain empty (null)

    return new JesonMor(configuration);
}