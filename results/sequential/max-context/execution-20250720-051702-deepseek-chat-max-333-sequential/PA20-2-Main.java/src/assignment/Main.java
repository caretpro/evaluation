
public static Game createGame(int size, int numMovesProtection) {
    ConsolePlayer userPlayer = new ConsolePlayer("User");
    RandomPlayer computerPlayer = new RandomPlayer("Computer");
    Configuration config = new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);

    // User pieces (green)
    config.addInitialPiece(new Knight(userPlayer), 0, 0);
    config.addInitialPiece(new Archer(userPlayer), 0, 1);
    config.addInitialPiece(new Knight(userPlayer), 0, size-1);
    
    // Computer pieces (blue)
    config.addInitialPiece(new Knight(computerPlayer), size-1, 0);
    config.addInitialPiece(new Archer(computerPlayer), size-1, 1);
    config.addInitialPiece(new Knight(computerPlayer), size-1, size-1);

    return new JesonMor(config);
}