
PipeQueue() {
    pipeQueue = new LinkedList<>();
    while (pipeQueue.size() < MAX_GEN_LENGTH) {
        pipeQueue.add(generateNewPipe());
    }
}

PipeQueue(List<Pipe> pipes) {
    pipeQueue = new LinkedList<>(pipes);
    while (pipeQueue.size() < MAX_GEN_LENGTH) {
        pipeQueue.add(generateNewPipe());
    }
}