
package game;

import game.pipes.Pipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class encapsulating the pipe queue.
 */
class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    @NotNull private final LinkedList<Pipe> pipeQueue;

    /**
     * Creates a pipe queue with initial pipes.
     */
    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        for (int i = 0; i < MAX_GEN_LENGTH; i++) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Creates a pipe queue with initial pipes.
     * @param pipes List of pipes to initialize the queue
     */
    public PipeQueue(@NotNull List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    // ... [rest of the existing methods remain unchanged] ...
}