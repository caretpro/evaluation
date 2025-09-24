
package game;

import game.pipes.Pipe;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class encapsulating the pipe queue.
 */
public class PipeQueue {

    /**
     * Maximum number of pipes to display in the queue.
     */
    private static final int MAX_GEN_LENGTH = 5;

    @NotNull
    private final LinkedList<Pipe> pipeQueue;

    private static final Random random = new Random();

    /**
     * Creates a pipe queue with {@link PipeQueue#MAX_GEN_LENGTH} number of pipes.
     *
     * <p>
     * This method should also populate the queue until it has {@link PipeQueue#MAX_GEN_LENGTH} number of pipes in it.
     * </p>
     */
    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        // Populate the queue with initial pipes
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Creates a pipe queue with pipes already filled in the queue. Ultimately, it should contain
     * {@link PipeQueue#MAX_GEN_LENGTH} number of pipes.
     *
     * <p>
     * This method should also populate the queue until it has {@link PipeQueue#MAX_GEN_LENGTH} number of pipes in it.
     * </p>
     *
     * @param pipes List of pipes to display before generated pipes.
     */
    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        if (pipes != null) {
            this.pipeQueue.addAll(pipes);
        }
        // Fill the queue until it reaches MAX_GEN_LENGTH
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
    public Pipe peek() {
        if (pipeQueue.isEmpty()) {
            throw new IllegalStateException("No pipes in the queue");
        }
        return pipeQueue.peekFirst();
    }

    /**
     * Consumes the next pipe.
     *
     * This method removes the pipe from the queue, and generate new ones if the queue has less elements than
     * {@link PipeQueue#MAX_GEN_LENGTH}.
     */
    public void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.removeFirst();
        }
        // Generate new pipes if needed to maintain size
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    /**
     * Undoes a step by inserting {@code pipe} into the front of the queue.
     *
     * @param pipe Pipe to insert to front of queue.
     */
    public void undo(final Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    /**
     * Displays the current queue.
     */
    public void display() {
        System.out.print("Next Pipes:  ");
        for (var p : pipeQueue) {
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
        int randInt = random.nextInt(3); // assuming 3 types of pipes
        switch (randInt) {
            case 0:
                return Pipe.createStraightPipe();
            case 1:
                return Pipe.createCurvedPipe();
            case 2:
                return Pipe.createSpecialPipe();
            default:
                return Pipe.createStraightPipe(); // fallback
        }
    }
}