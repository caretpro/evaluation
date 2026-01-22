
public class GameState {
    @NotNull
    private final GameBoard gameBoard;
    private final int initialNumOfGems;
    private int numLives;
    private int numDeaths;
    private int numMoves;
    @NotNull
    private final MoveStack moveStack;

    /**
     * Constructor for unlimited lives.
     * @param gameBoard the game board
     */
    public GameState(@NotNull GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numLives = UNLIMITED_LIVES;
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
    }

    /**
     * Constructor for limited lives.
     * @param gameBoard the game board
     * @param numLives the number of lives
     */
    public GameState(@NotNull GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
        this.initialNumOfGems = gameBoard.getNumGems();
        if (numLives < 0) {
            this.numLives = UNLIMITED_LIVES;
        } else {
            this.numLives = numLives;
        }
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
    }
}