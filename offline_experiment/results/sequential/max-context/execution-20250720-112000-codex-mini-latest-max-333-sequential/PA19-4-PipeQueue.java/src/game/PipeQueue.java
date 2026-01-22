
package game;

import game.pipes.Pipe;
import org.jetbrains.annotations.NotNull;

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
     * Displays the current queue.
     */
    void display() {
        System.out.print("Next Pipes:  ");
        for (Pipe p : pipeQueue) {
            System.out.print(p.toSingleChar() + "    ");
        }
        System.out.println();
    }

    /**
     * Generates a new pipe.
     *
     * <p>
     * Hint: Use {@link java.util.Random#nextInt(int)} to generate random numbers.
     * </p>
     *
     * @return A new pipe.
     */
    private static Pipe generateNewPipe() {
        Random random = new Random();
        int idx = random.nextInt(Pipe.Shape.values().length);
        return new Pipe(Pipe.Shape.values()[idx]);
    }

    /**
     * Creates a pipe queue with {@link #MAX_GEN_LENGTH} number of pipes.
     * This constructor also populates the queue until it has {@link #MAX_GEN_LENGTH} pipes.
     */
    PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Creates a pipe queue with pipes already filled in the queue.
     * Ultimately, it will contain {@link #MAX_GEN_LENGTH} pipes.
     *
     * @param pipes List of pipes to display before generated pipes.
     */
    PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
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
            throw new IllegalStateException("No pipes in the queue.");
        }
        return pipeQueue.getFirst();
    }

    void consume() {
        if (pipeQueue.isEmpty()) {
            throw new IllegalStateException("No pipes to consume.");
        }
        pipeQueue.removeFirst();
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
        pipeQueue.addFirst(pipe);
        if (pipeQueue.size() > MAX_GEN_LENGTH) {
            pipeQueue.removeLast();
        }
    }
}