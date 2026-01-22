
public static Game createGame(int size, int numMovesProtection) {
    // Create players: user and computer
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    
    // Construct configuration with user as first player (moves first)
    Player[] players = new Player[] { userPlayer, computerPlayer };
    Configuration configuration = new Configuration(size, players, numMovesProtection);
    
    // Add initial pieces to the gameboard with proper player association
    Knight userKnight = new Knight(userPlayer);
    configuration.addInitialPiece(userKnight, 0, 0);
    
    Knight computerKnight = new Knight(computerPlayer);
    configuration.addInitialPiece(computerKnight, size - 1, size - 1);
    
    // Return the game instance
    return new JesonMor(configuration);
}