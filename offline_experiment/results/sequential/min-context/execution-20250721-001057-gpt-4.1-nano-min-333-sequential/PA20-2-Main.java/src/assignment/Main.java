
public static Game createGame(int size, int numMovesProtection) {
    // Create the user player and the computer player
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    
    // Initialize configuration with user player first to ensure they move first
    Player[] players = new Player[] { userPlayer, computerPlayer };
    Configuration configuration = new Configuration(size, players, numMovesProtection);
    
    // Add initial pieces to the gameboard with proper player association
    // Place a Knight for the user at (0,0)
    Knight userKnight = new Knight(userPlayer);
    configuration.addInitialPiece(userKnight, 0, 0);
    
    // Place a Knight for the computer at (size - 1, size - 1)
    Knight computerKnight = new Knight(computerPlayer);
    configuration.addInitialPiece(computerKnight, size - 1, size - 1);
    
    // Optionally, add more initial pieces with correct player associations here
    
    // Create and return the game object
    return new JesonMor(configuration);
}