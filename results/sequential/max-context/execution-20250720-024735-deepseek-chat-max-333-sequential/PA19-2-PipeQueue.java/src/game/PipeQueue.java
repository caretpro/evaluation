
package game;

import game.pipes.Pipe;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class PipeQueue {
    private static final int MAX_GEN_LENGTH = 5;
    @NotNull private final LinkedList<Pipe> pipeQueue;

    public PipeQueue() {
        this.pipeQueue = new LinkedList<>();
        for (int i = 0; i < MAX_GEN_LENGTH; i++) {
            pipeQueue.add(generateNewPipe());
        }
    }

    public PipeQueue(List<Pipe> pipes) {
        this.pipeQueue = new LinkedList<>(pipes);
        while (pipeQueue.size() < MAX_GEN_LENGTH) {
            pipeQueue.add(generateNewPipe());
        }
    }

    // ... [rest of the class methods remain unchanged] ...
}