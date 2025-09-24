
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    
    Configuration configuration = new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);
    
    // Place user pieces (1 knight and 1 archer)
    configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
    configuration.addInitialPiece(new Archer(userPlayer), 0, 1);
    
    // Place computer pieces (1 knight and 1 archer)
    configuration.addInitialPiece(new Knight(computerPlayer), size-1, size-1);
    configuration.addInitialPiece(new Archer(computerPlayer), size-1, size-2);
    
    return new JesonMor(configuration);
}