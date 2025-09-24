
/**
 * Creates a pipe queue with pipes already filled in the queue. Ultimately, it should contain {@link PipeQueue#MAX_GEN_LENGTH} number of pipes.
 * <p> This method should also populate the queue until it has {@link PipeQueue#MAX_GEN_LENGTH} number of pipes in it. </p>
 * @param pipes List of pipes to display before generated pipes.
 */
public PipeQueue(List<Pipe> pipes) {
    this.pipeQueue = new LinkedList<>(pipes);
    while (pipeQueue.size() < MAX_GEN_LENGTH) {
        pipeQueue.add(generateNewPipe());
    }
}