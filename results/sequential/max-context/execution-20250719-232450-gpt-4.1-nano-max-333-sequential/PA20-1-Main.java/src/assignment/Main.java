
public static Game createGame(int size, int numMovesProtection) {
    // Create players: user and computer
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    
    // Initialize configuration with players and protection moves
    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);
    
    // Create initial pieces for each player
    Knight userKnight = new Knight(userPlayer);
    Knight computerKnight = new Knight(computerPlayer);
    
    // Place user's knight at (0,0)
    configuration.addInitialPiece(userKnight, 0, 0);
    // Place computer's knight at (size - 1, size - 1)
    configuration.addInitialPiece(computerKnight, size - 1, size - 1);
    
    // Return the game instance
    return new JesonMor(configuration);
}