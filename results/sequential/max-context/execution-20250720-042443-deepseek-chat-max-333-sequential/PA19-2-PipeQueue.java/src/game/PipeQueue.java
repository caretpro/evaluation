
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

    /**
     * Maximum number of pipes to display in the queue.
     */
    private static final int MAX_GEN_LENGTH = 5;

    @NotNull
    private final LinkedList<Pipe> pipeQueue;

    /**
     * Creates a pipe queue with {@link PipeQueue#MAX_GEN_LENGTH} number of pipes.
     * This method should also populate the queue until it has {@link PipeQueue#MAX_GEN_LENGTH} number of pipes in it.
     */
    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Creates a pipe queue with pipes already filled in the queue. Ultimately, it should contain
     * {@link PipeQueue#MAX_GEN_LENGTH} number of pipes.
     * This method should also populate the queue until it has {@link PipeQueue#MAX_GEN_LENGTH} number of pipes in it.
     * @param pipes List of pipes to display before generated pipes.
     */
    public PipeQueue(@NotNull List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    // [Rest of the class methods remain unchanged...]
}