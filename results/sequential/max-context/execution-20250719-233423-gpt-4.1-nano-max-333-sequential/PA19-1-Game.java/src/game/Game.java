
public class Game {
    // existing fields and methods...

    /**
     * Constructor for creating a Game instance with specific parameters.
     *
     * @param rows Number of rows in the map.
     * @param cols Number of columns in the map.
     * @param delay Delay in rounds before water flows.
     * @param cells 2D array of cells representing the map.
     * @param pipes List of pipes available in the game.
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.cellStack = new CellStack();
        this.numOfSteps = 0;
    }
}