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
	PipeQueue() {
		this(java.util.Collections.emptyList());
	}

	/**
	 * Creates a pipe queue with pipes already filled in the queue. Ultimately, it should contain {@link PipeQueue#MAX_GEN_LENGTH}  number of pipes. <p> This method should also populate the queue until it has  {@link PipeQueue#MAX_GEN_LENGTH}  number of pipes in it. </p>
	 * @param pipes  List of pipes to display before generated pipes.
	 */
	void PipeQueue(List<Pipe> pipes) {
		this.pipeQueue = new LinkedList<>();
		for (int i = 0; i < pipes.size() && this.pipeQueue.size() < MAX_GEN_LENGTH; i++) {
			this.pipeQueue.addLast(pipes.get(i));
		}
		while (this.pipeQueue.size() < MAX_GEN_LENGTH) {
			this.pipeQueue.addLast(generateNewPipe());
		}
	}

	/**
	 * Peeks the next pipe.
	 * @return  The next pipe in the queue.
	 * @throws IllegalStateException  if there are no pipes in the queue.
	 */
	Pipe peek() {
		if (pipeQueue.isEmpty()) {
			throw new IllegalStateException("No pipes in the queue");
		}
		return pipeQueue.getFirst();
	}

	void consume() {
		if (pipeQueue.isEmpty()) {
			throw new IllegalStateException("No pipes in the queue");
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
	 * Generates a new pipe. <p> Hint: Use  {@link java.util.Random#nextInt(int)}  to generate random numbers. </p>
	 * @return  A new pipe.
	 */
	private static Pipe generateNewPipe() {
		Random rnd = new Random();
		return switch (rnd.nextInt(6)) {
		case 0:
			yield new game.pipes.StraightPipe();
		case 1:
			yield new game.pipes.TPipe();
		case 2:
			yield new game.pipes.CornerPipeNorthWest();
		case 3:
			yield new game.pipes.CornerPipeNorthEast();
		case 4:
			yield new game.pipes.CornerPipeSouthWest();
		case 5:
			yield new game.pipes.CornerPipeSouthEast();
		default:
			throw new IllegalStateException("Unexpected pipe index");
		};
	}
}
