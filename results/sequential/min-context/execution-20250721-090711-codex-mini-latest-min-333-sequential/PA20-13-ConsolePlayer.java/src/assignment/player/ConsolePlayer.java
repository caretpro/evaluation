package assignment.player;

import assignment.protocol.Game;
import assignment.piece.*;
import assignment.protocol.Color;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * The player that makes move according to user input from console.
 */
public class ConsolePlayer extends Player {
	public ConsolePlayer(String name, Color color) {
		super(name, color);
	}

	public ConsolePlayer(String name) {
		this(name, Color.GREEN);
	}

	private String validateMove(Game game, Move move) {
		var rules = new Rule[] { new OutOfBoundaryRule(), new OccupiedRule(), new VacantRule(), new NilMoveRule(),
				new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), new ArcherMoveRule(),
				new KnightMoveRule(), new KnightBlockRule(), };
		for (var rule : rules) {
			if (!rule.validate(game, move)) {
				return rule.getDescription();
			}

			var piece = game.getPiece(move.getSource());
			if (piece == null) {
				return "No piece at " + move.getSource().toString();
			}
			if (!this.equals(piece.getPlayer())) {
				return "Cannot move a piece not belonging to you";
			}
		}
		return null;
	}

	private static Place parsePlace(String str) {
		if (str.length() < 2) {
			return null;
		}
		try {
			var x = str.charAt(0) - 'a';
			var y = Integer.parseInt(str.substring(1)) - 1;
			return new Place(x, y);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Move parseMove(String str) {
		var segments = str.split("->");
		if (segments.length < 2) {
			return null;
		}
		var source = parsePlace(segments[0].strip());
		if (source == null) {
			return null;
		}
		var destination = parsePlace(segments[1].strip());
		if (destination == null) {
			return null;
		}
		return new Move(source, destination);
	}

	@Override
	public Move nextMove(Game game, Move[] availableMoves) {
		Scanner scanner = new Scanner(System.in);
		Move selectedMove;
		String error;
		while (true) {
			System.out.print("Enter your move (e.g. a1->b3): ");
			String line = scanner.nextLine().trim();
			selectedMove = parseMove(line);
			if (selectedMove == null) {
				System.out.println("Invalid format.  Please use x#->y# (e.g. a1->b3).");
				continue;
			}
			boolean inList = false;
			for (Move m : availableMoves) {
				if (m.equals(selectedMove)) {
					inList = true;
					break;
				}
			}
			if (!inList) {
				System.out.println("That move is not one of the available moves.");
				continue;
			}
			error = validateMove(game, selectedMove);
			if (error != null) {
				System.out.println("Illegal move: " + error);
				continue;
			}
			break;
		}
		return selectedMove;
	}
}
