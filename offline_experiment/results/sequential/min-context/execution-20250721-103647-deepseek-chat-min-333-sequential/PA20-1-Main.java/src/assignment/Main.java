
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    
    Configuration configuration = new Configuration(size, 
        new Player[]{userPlayer, computerPlayer}, numMovesProtection);
    
    // Place user pieces in first row
    for (int col = 0; col < size; col++) {
        configuration.addInitialPiece(new Knight(userPlayer), 0, col);
    }
    
    // Place computer pieces in last row
    for (int col = 0; col < size; col++) {
        configuration.addInitialPiece(new Knight(computerPlayer), size-1, col);
    }
    
    return new JesonMor(configuration);
}