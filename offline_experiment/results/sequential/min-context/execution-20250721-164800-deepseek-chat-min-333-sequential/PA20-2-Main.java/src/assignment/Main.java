
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    
    Configuration configuration = new Configuration(size, 
        new Player[]{userPlayer, computerPlayer}, numMovesProtection);
    
    // Place one knight and one archer for each player at valid positions
    configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
    configuration.addInitialPiece(new Archer(userPlayer), 0, 1);
    configuration.addInitialPiece(new Knight(computerPlayer), size-1, size-1);
    configuration.addInitialPiece(new Archer(computerPlayer), size-1, size-2);
    
    return new JesonMor(configuration);
}