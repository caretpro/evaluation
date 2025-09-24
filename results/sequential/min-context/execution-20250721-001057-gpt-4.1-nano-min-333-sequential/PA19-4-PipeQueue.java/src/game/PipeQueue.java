
public PipeQueue(@Nullable List<Pipe> pipes) {
    this.pipeQueue = new LinkedList<>();
    if (pipes != null) {
        this.pipeQueue.addAll(pipes);
    }
    while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
        this.pipeQueue.add(generateNewPipe());
    }
}