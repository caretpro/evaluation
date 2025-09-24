
package game;

import game.pipes.Pipe;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
        }
        populateQueue();
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
        // Basic implementation that creates a new Pipe with default constructor
        // This assumes Pipe has a no-arg constructor that creates a random pipe
        return new Pipe();
    }

    private void populateQueue() {
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }
}