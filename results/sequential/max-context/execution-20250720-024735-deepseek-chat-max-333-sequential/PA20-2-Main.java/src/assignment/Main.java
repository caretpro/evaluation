
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("User");
    RandomPlayer computerPlayer = new RandomPlayer("Computer");
    Configuration configuration = new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);

    // User pieces (green)
    configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
    configuration.addInitialPiece(new Archer(userPlayer), 0, 1);
    
    // Computer pieces (blue)
    configuration.addInitialPiece(new Knight(computerPlayer), size-1, size-1);
    configuration.addInitialPiece(new Archer(computerPlayer), size-1, size-2);

    return new JesonMor(configuration);
}