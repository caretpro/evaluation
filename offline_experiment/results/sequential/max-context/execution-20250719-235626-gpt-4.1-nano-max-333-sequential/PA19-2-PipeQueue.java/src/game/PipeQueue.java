
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
     * Constructor initializing the queue with a list of pipes.
     *
     * @param pipes List of pipes to initialize the queue.
     */
    public PipeQueue(@NotNull List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        for (Pipe pipe : pipes) {
            this.pipeQueue.add(pipe);
        }
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
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
        int shapeIndex = random.nextInt(Pipe.Shape.values().length);
        Pipe.Shape[] shapes = Pipe.Shape.values();
        Pipe.Shape selectedShape = shapes[shapeIndex];
        return new Pipe(selectedShape);
    }

    void PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        for (int i = 0; i < MAX_GEN_LENGTH; i++) {
            pipeQueue.add(generateNewPipe());
        }
    }

    void PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>();
        for (Pipe pipe : pipes) {
            this.pipeQueue.add(pipe);
        }
        while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
            this.pipeQueue.add(generateNewPipe());
        }
    }

    Pipe peek() {
        if (pipeQueue.isEmpty()) {
            throw new IllegalStateException("No pipes in the queue");
        }
        return pipeQueue.getFirst();
    }

    void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.removeFirst();
        }
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    void undo(final Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }
}