
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
     */
    PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        populateQueue();
    }

    /**
     * Creates a pipe queue with pipes already filled in the queue.
     */
    PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        populateQueue();
    }

    Pipe peek() {
        if (pipeQueue.isEmpty()) {
            throw new IllegalStateException("No pipes in the queue");
        }
        return pipeQueue.peek();
    }

    void consume() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.removeFirst();
            populateQueue();
        }
    }

    void undo(final Pipe pipe) {
        pipeQueue.addFirst(pipe);
    }

    void display() {
        System.out.print("Next Pipes:  ");
        for (var p : pipeQueue) {
            System.out.print(p.toSingleChar() + "    ");
        }
        System.out.println();
    }

    private static Pipe generateNewPipe() {
        Random random = new Random();
        Pipe.Shape[] shapes = Pipe.Shape.values();
        return new Pipe(shapes[random.nextInt(shapes.length)]);
    }

    private void populateQueue() {
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }
}