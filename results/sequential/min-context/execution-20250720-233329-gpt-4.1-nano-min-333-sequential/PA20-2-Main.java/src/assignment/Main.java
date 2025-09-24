
public static Game createGame(int size, int numMovesProtection) {
    // Create the user player and the computer opponent
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    
    // Construct the configuration with the user player first to ensure they move first
    Player[] players = new Player[] { userPlayer, computerPlayer };
    Configuration configuration = new Configuration(size, players, numMovesProtection);
    
    // Add initial pieces for the user player at (0,0)
    Knight userKnight = new Knight(userPlayer);
    configuration.addInitialPiece(userKnight, 0, 0);
    
    // Add initial pieces for the computer player at (size-1, size-1)
    Knight computerKnight = new Knight(computerPlayer);
    configuration.addInitialPiece(computerKnight, size - 1, size - 1);
    
    // Ensure the configuration properly initializes the gameboard with these pieces
    // and that the pieces are associated with their players
    
    // Create and return the game instance
    return new JesonMor(configuration);
}