
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

    private static final int MAX_GEN_LENGTH = 5;

    @NotNull
    private final LinkedList<Pipe> pipeQueue;

    PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        populateQueue();
    }

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
        if (pipeQueue.size() > MAX_GEN_LENGTH) {
            pipeQueue.removeLast();
        }
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