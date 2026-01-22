
public static Game createGame(int size, int numMovesProtection) {
    // Create players: user and computer
    Player userPlayer = new ConsolePlayer("User");
    Player computerPlayer = new RandomPlayer("Computer");
    
    // Initialize configuration with user as first player (moves first)
    Configuration configuration = new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);
    
    // Add initial pieces for user player at specific positions
    configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
    configuration.addInitialPiece(new Archer(userPlayer), 0, size - 1);
    
    // Add initial pieces for computer player at specific positions
    configuration.addInitialPiece(new Knight(computerPlayer), size - 1, 0);
    configuration.addInitialPiece(new Archer(computerPlayer), size - 1, size - 1);
    
    // Construct and return the game
    return new JesonMor(configuration);
}