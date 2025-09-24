
public static Game createGame(int size, int numMovesProtection) {
    // Create players: user player first, then computer
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    Player[] players = new Player[] { userPlayer, computerPlayer };
    
    // Initialize configuration with players
    Configuration configuration = new Configuration(size, players, numMovesProtection);
    
    // Place initial pieces for user and computer
    // For example, place user's Knight at (0,0)
    Knight userKnight = new Knight(userPlayer);
    configuration.addInitialPiece(userKnight, 0, 0);
    
    // Place computer's Knight at (size - 1, size - 1)
    Knight computerKnight = new Knight(computerPlayer);
    configuration.addInitialPiece(computerKnight, size - 1, size - 1);
    
    // Additional initial pieces can be added here if needed
    
    // Create and return the game instance
    return new JesonMor(configuration);
}