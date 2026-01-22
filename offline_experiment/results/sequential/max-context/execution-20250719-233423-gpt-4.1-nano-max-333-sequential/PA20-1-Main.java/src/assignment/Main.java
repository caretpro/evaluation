
public static Game createGame(int size, int numMovesProtection) {
    // Create the user player and computer player
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    
    // Initialize configuration with the user player first to ensure they move first
    Player[] players = new Player[] { userPlayer, computerPlayer };
    Configuration configuration = new Configuration(size, players, numMovesProtection);
    
    // Add initial pieces to the gameboard at valid positions
    Knight knight1 = new Knight(userPlayer);
    Knight knight2 = new Knight(computerPlayer);
    // Place knights away from the central square to avoid conflicts
    configuration.addInitialPiece(knight1, 0, 0); // top-left corner
    configuration.addInitialPiece(knight2, size - 1, size - 1); // bottom-right corner
    
    // Return the JesonMor game constructed with this configuration
    return new JesonMor(configuration);
}