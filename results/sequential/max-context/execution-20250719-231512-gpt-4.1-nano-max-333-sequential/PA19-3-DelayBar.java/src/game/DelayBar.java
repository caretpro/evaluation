package game;

import util.StringUtils;

/**
 * Class representing the delay before the water starts to flow in the map.
 */
class DelayBar {

	private final int initialValue;
	private int currentValue;

	/**
	 * Constructs a {@link DelayBar}.
	 *
	 * @param initialValue Number of rounds to wait before the water starts to flow.
	 */
	DelayBar(int initialValue) {
		this.initialValue = initialValue;
		this.currentValue = initialValue;
	}

	void display() {
		if (currentValue > 0) {
			System.out.print("Rounds Countdown: ");
			System.out.print(StringUtils.createPadding(currentValue, '='));
			System.out.print(StringUtils.createPadding(initialValue - currentValue, ' '));
			System.out.print(" " + currentValue);
			System.out.println();
		} else {
			System.out.println();
		}
	}

	void countdown() {
		currentValue--;
	}

	int distance() {
		return currentValue < 0 ? Math.abs(currentValue) : 0;
	}
}
