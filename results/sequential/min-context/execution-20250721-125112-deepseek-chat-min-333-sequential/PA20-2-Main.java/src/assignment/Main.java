
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    
    Configuration configuration = new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);
    
    // Add only the required pieces at specific positions
    configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
    configuration.addInitialPiece(new Knight(computerPlayer), size-1, size-1);
    
    return new JesonMor(configuration);
}