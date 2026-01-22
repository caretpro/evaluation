
public Game(int rows, int cols) {
    this.map = new Map(rows + 2, cols + 2);
    this.pipeQueue = new PipeQueue();
    this.delayBar = new DelayBar(10);
    this.numOfSteps = 0;
}

public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
    this.map = new Map(rows + 2, cols + 2, cells);
    this.pipeQueue = new PipeQueue(pipes);
    this.delayBar = new DelayBar(delay);
    this.numOfSteps = 0;
    this.cellStack.clear();
}