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
	 * Creates a pipe queue with pipes already filled in the queue. Ultimately, it should contain
	 * {@link PipeQueue#MAX_GEN_LENGTH} number of pipes.
	 *
	 * <p>
	 * This method should also populate the queue until it has {@link PipeQueue#MAX_GEN_LENGTH} number of pipes in it.
	 * </p>
	 *
	 * @param pipes List of pipes to display before generated pipes.
	 */
	PipeQueue(List<Pipe> pipes) {
		// TODO
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
	 * Creates a pipe queue with  {@link PipeQueue#MAX_GEN_LENGTH}  number of pipes. <p> This method should also populate the queue until it has  {@link PipeQueue#MAX_GEN_LENGTH}  number of pipes in it. </p>
	 */
	void PipeQueue() {
		this.pipeQueue = new LinkedList<>();
		while (pipeQueue.size() < MAX_GEN_LENGTH) {
			pipeQueue.add(generateNewPipe());
		}
	}

	/**
	 * Peeks the next pipe.
	 * @return  The next pipe in the queue.
	 * @throws IllegalStateException  if there are no pipes in the queue.
	 */
	Pipe peek() {
		if (pipeQueue.isEmpty()) {
			throw new IllegalStateException("No pipes in the queue.");
		}
		return pipeQueue.getFirst();
	}

	/**
	 * Consumes the next pipe. This method removes the pipe from the queue, and generate new ones if the queue has less elements than {@link PipeQueue#MAX_GEN_LENGTH} .
	 */
	void consume() {
		if (pipeQueue.isEmpty()) {
			throw new IllegalStateException("No pipes to consume.");
		}
		pipeQueue.removeFirst();
		while (pipeQueue.size() < MAX_GEN_LENGTH) {
			pipeQueue.addLast(generateNewPipe());
		}
	}

	/**
	 * Undoes a step by inserting  {@code  pipe}  into the front of the queue.
	 * @param pipe  Pipe to insert to front of queue.
	 */
	void undo(final Pipe pipe) {
		pipeQueue.addFirst(pipe);
		if (pipeQueue.size() > MAX_GEN_LENGTH) {
			pipeQueue.removeLast();
		}
	}

	/**
	 * Returns a randomly chosen pipe. 
	 */
	public static Pipe randomPipe() {
	}
}
