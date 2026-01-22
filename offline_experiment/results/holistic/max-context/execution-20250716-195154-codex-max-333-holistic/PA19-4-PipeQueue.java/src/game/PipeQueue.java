
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

    private static final Random RNG = new Random();

    @NotNull
    private final LinkedList<Pipe> pipeQueue;

    /**
     * Creates a pipe queue with {@link PipeQueue#MAX_GEN_LENGTH} number of pipes.
     *
     * <p>
     * This constructor also populates the queue until it has {@link PipeQueue#MAX_GEN_LENGTH} pipes in it.
     * </p>
     */
    PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        // fill up to MAX_GEN_LENGTH
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Creates a pipe queue with pipes already filled in the queue. Ultimately, it will contain
     * {@link PipeQueue#MAX_GEN_LENGTH} pipes.
     *
     * <p>
     * This constructor also populates the queue until it has {@link PipeQueue#MAX_GEN_LENGTH} pipes in it.
     * </p>
     *
     * @param pipes List of pipes to display before generated pipes.
     */
    PipeQueue(@Nullable List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
        // fill up to MAX_GEN_LENGTH
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Peeks the next pipe.
     *
     * @return The next pipe in the queue.
     * @throws IllegalStateException if there are no pipes in the queue.
     */
    Pipe peek() {
        if (pipeQueue.isEmpty()) {
            throw new IllegalStateException("No pipes available to peek");
        }
        return pipeQueue.getFirst();
    }

    /**
     * Consumes the next pipe.
     *
     * This method removes the pipe from the queue, and generates new ones if the queue has fewer elements than
     * {@link PipeQueue#MAX_GEN_LENGTH}.
     */
    void consume() {
        if (pipeQueue.isEmpty()) {
            throw new IllegalStateException("No pipes to consume");
        }
        pipeQueue.removeFirst();
        // replenish
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Undoes a step by inserting {@code pipe} into the front of the queue.
     *
     * @param pipe Pipe to insert to front of queue.
     */
    void undo(final Pipe pipe) {
        // add back to front
        pipeQueue.addFirst(pipe);
        // enforce max length
        if (pipeQueue.size() > MAX_GEN_LENGTH) {
            pipeQueue.removeLast();
        }
    }

    /**
     * Displays the current queue.
     */
    void display() {
        System.out.print("Next Pipes:  ");
        for (var p : pipeQueue) {
            System.out.print(p.toSingleChar() + "    ");
        }
        System.out.println();
    }

    /**
     * Generates a new pipe at random.
     *
     * <p>
     * Uses {@link java.util.Random#nextInt(int)} to choose among the available shapes.
     * </p>
     *
     * @return A new pipe.
     */
    private static Pipe generateNewPipe() {
        Pipe.Shape[] shapes = Pipe.Shape.values();
        int idx = RNG.nextInt(shapes.length);
        return new Pipe(shapes[idx]);
    }
}