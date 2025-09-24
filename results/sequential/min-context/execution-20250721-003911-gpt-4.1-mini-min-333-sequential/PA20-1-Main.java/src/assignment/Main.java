
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);

    // Place pieces for computer player on top two rows
    for (int col = 0; col < size; col++) {
        if (col % 2 == 0) {
            configuration.addInitialPiece(new Knight(computerPlayer), 0, col);
            configuration.addInitialPiece(new Archer(computerPlayer), 1, col);
        } else {
            configuration.addInitialPiece(new Archer(computerPlayer), 0, col);
            configuration.addInitialPiece(new Knight(computerPlayer), 1, col);
        }
    }

    // Place pieces for user player on bottom two rows
    int lastRow = size - 1;
    int secondLastRow = size - 2;
    for (int col = 0; col < size; col++) {
        if (col % 2 == 0) {
            configuration.addInitialPiece(new Knight(userPlayer), lastRow, col);
            configuration.addInitialPiece(new Archer(userPlayer), secondLastRow, col);
        } else {
            configuration.addInitialPiece(new Archer(userPlayer), lastRow, col);
            configuration.addInitialPiece(new Knight(userPlayer), secondLastRow, col);
        }
    }

    return new JesonMor(configuration);
}