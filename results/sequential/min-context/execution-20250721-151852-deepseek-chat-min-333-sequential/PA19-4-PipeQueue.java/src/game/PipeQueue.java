
/**
 * Creates a pipe queue with pipes already filled in the queue.
 * @param pipes List of pipes to display before generated pipes.
 */
public PipeQueue(@NotNull List<Pipe> pipes) {
    this.pipeQueue = new LinkedList<>(pipes);
    while (pipeQueue.size() < MAX_GEN_LENGTH) {
        pipeQueue.add(generateNewPipe());
    }
}