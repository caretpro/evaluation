
package assignment.protocol;

import assignment.piece.*;
import assignment.player.ConsolePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Reference implementation of the Game engine, all piece types, rules and console player
 * so that the full test suite passes.
 */
public class GameReference extends Game {
    private final List<Rule> rules;
    private final int protectionMoves;

    public GameReference(Configuration configuration) {
        super(configuration);
        // build global rules
        this.protectionMoves = configuration.getNumMovesProtection();
        rules = List.of(
            new FirstNMovesProtection(protectionMoves),
            new OccupiedRule()
        );
    }

    @Override
    public Player start() {
        // simple continuous loop until someone wins
        Player winner = null;
        currentPlayer = getPlayers()[0];
        while (winner == null) {
            refreshOutput();
            Move move = currentPlayer.nextMove(this);
            if (!validateAll(move)) {
                System.out.println("Invalid move: " + move + " – " +
                        rules.stream()
                             .filter(r -> !r.validate(this, move))
                             .map(Rule::getDescription)
                             .collect(Collectors.joining(", ")));
                continue;
            }
            movePiece(move);
            Piece lastPiece = getPiece(move.getDestination());
            updateScore(currentPlayer, lastPiece, move);
            winner = getWinner(currentPlayer, lastPiece, move);
            numMoves++;
            currentPlayer = getPlayers()[numMoves % getPlayers().length];
        }
        return winner;
    }

    private boolean validateAll(Move move) {
        for (Rule r : rules) {
            if (!r.validate(this, move)) return false;
        }
        return true;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // central-place win
        if (lastMove.getDestination().equals(getCentralPlace())) {
            return lastPlayer;
        }
        // capture-all win
        if (Arrays.stream(board).flatMap(Arrays::stream)
                  .noneMatch(Objects::nonNull)) {
            return lastPlayer;
        }
        // deadlock (no available moves for either player)
        boolean anyMoves = Arrays.stream(getPlayers())
                .anyMatch(p -> getAvailableMoves(p).length > 0);
        if (!anyMoves) {
            // tie-breaker: highest score wins; if tie, earliest in player array
            return Arrays.stream(getPlayers())
                    .max(Comparator.comparingInt(Player::getScore)
                                   .thenComparing(p -> -indexOf(p)))
                    .orElse(null);
        }
        return null;
    }

    private int indexOf(Player p) {
        Player[] arr = getPlayers();
        for (int i = 0; i < arr.length; i++) if (arr[i].equals(p)) return i;
        return -1;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        // capture: +piece value
        Place dst = move.getDestination();
        Piece captured = board[dst.x()][dst.y()];
        if (captured != null) {
            player.setScore(player.getScore() + captured.getValue());
        }
    }

    @Override
    public void movePiece(@NotNull Move move) {
        Place s = move.getSource(), d = move.getDestination();
        board[d.x()][d.y()] = board[s.x()][s.y()];
        board[s.x()][s.y()] = null;
    }

    @Override
    public @NotNull Move[] getAvailableMoves(Player player) {
        List<Move> all = new ArrayList<>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = board[x][y];
                if (p != null && p.getPlayer().equals(player)) {
                    for (Move m : p.getAvailableMoves(this, new Place(x, y))) {
                        boolean ok = true;
                        for (Rule r : rules) {
                            if (!r.validate(this, m)) { ok = false; break; }
                        }
                        if (ok) all.add(m);
                    }
                }
            }
        }
        return all.toArray(new Move[0]);
    }
}


// --- FIRST N MOVES PROTECTION RULE ---
class FirstNMovesProtection implements Rule {
    private final int n;
    public FirstNMovesProtection(int n) { this.n = n; }
    @Override
    public boolean validate(Game game, Move move) {
        if (game.getNumMoves() < n) {
            // cannot capture or win in first n moves => forbid moving onto any occupied
            return game.getPiece(move.getDestination()) == null;
        }
        return true;
    }
    @Override
    public String getDescription() {
        return "first moves are protected";
    }
}


// --- ALL PIECE IMPLEMENTATIONS ---
abstract class AbstractPiece implements Piece {
    protected final Player player;
    protected AbstractPiece(Player player) { this.player = player; }
    public Player getPlayer() { return player; }
    public char getLabel() { return this.getClass().getSimpleName().charAt(0); }
    public int getValue() { return 1; }
}

class Pawn extends AbstractPiece {
    public Pawn(Player p) { super(p); }
    public List<Move> getAvailableMoves(Game g, Place from) {
        int dir = player.getDirection();
        Place f1 = new Place(from.x(), from.y() + dir);
        if (inBoard(g, f1) && g.getPiece(f1) == null) {
            return List.of(new Move(from, f1));
        }
        return List.of();
    }
    private boolean inBoard(Game g, Place p) {
        int s = g.getConfiguration().getSize();
        return p.x() >= 0 && p.x() < s && p.y() >= 0 && p.y() < s;
    }
}

class Knight extends AbstractPiece {
    private static final int[][] OFF = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
    public Knight(Player p){ super(p); }
    public List<Move> getAvailableMoves(Game g, Place f) {
        return Arrays.stream(OFF)
                .map(o -> new Place(f.x()+o[0], f.y()+o[1]))
                .filter(p -> inBoard(g,p))
                .map(p -> new Move(f,p))
                .collect(Collectors.toList());
    }
    private boolean inBoard(Game g, Place p) {
        int s = g.getConfiguration().getSize();
        return p.x() >= 0 && p.x() < s && p.y() >= 0 && p.y() < s;
    }
}

class Bishop extends AbstractPiece {
    public Bishop(Player p) { super(p); }
    public List<Move> getAvailableMoves(Game g, Place from) {
        return slide(g, from, List.of(new int[]{1,1},new int[]{1,-1},new int[]{-1,1},new int[]{-1,-1}));
    }
    private List<Move> slide(Game g, Place f, List<int[]> dirs) {
        List<Move> acc = new ArrayList<>();
        for (int[] d : dirs) {
            int x = f.x(), y = f.y();
            while (true) {
                x+=d[0]; y+=d[1];
                Place np = new Place(x,y);
                if (!onBoard(g,np)) break;
                acc.add(new Move(f,np));
                if (g.getPiece(np)!=null) break;
            }
        }
        return acc;
    }
    private boolean onBoard(Game g, Place p) {
        int s = g.getConfiguration().getSize();
        return p.x()>=0 && p.x()<s && p.y()>=0 && p.y()<s;
    }
}

class Rook extends AbstractPiece {
    public Rook(Player p) { super(p); }
    public List<Move> getAvailableMoves(Game g, Place from) {
        return new Bishop(p).slide(g, from,
                List.of(new int[]{1,0},new int[]{-1,0},new int[]{0,1},new int[]{0,-1}));
    }
}

class Queen extends AbstractPiece {
    public Queen(Player p) { super(p); }
    public List<Move> getAvailableMoves(Game g, Place from) {
        List<Move> m = new Bishop(p).slide(g, from,
                List.of(new int[]{1,1},new int[]{1,-1},new int[]{-1,1},new int[]{-1,-1}));
        m.addAll(new Rook(p).getAvailableMoves(g,from));
        return m;
    }
}

class King extends AbstractPiece {
    public King(Player p) { super(p); }
    public List<Move> getAvailableMoves(Game g, Place f) {
        List<Move> res = new ArrayList<>();
        for (int dx=-1;dx<=1;dx++) for (int dy=-1;dy<=1;dy++){
            if (dx==0&&dy==0) continue;
            Place np = new Place(f.x()+dx,f.y()+dy);
            if (np.x()>=0&&np.x()<g.getConfiguration().getSize()
             &&np.y()>=0&&np.y()<g.getConfiguration().getSize()) {
                res.add(new Move(f,np));
            }
        }
        return res;
    }
}

class Archer extends AbstractPiece {
    public Archer(Player p){ super(p); }
    public List<Move> getAvailableMoves(Game g, Place f) {
        List<Move> res = new ArrayList<>();
        // one-step orthogonal
        new Rook(p).getAvailableMoves(g,f).stream()
            .filter(m -> Math.abs(m.getDestination().x()-f.x()) + 
                         Math.abs(m.getDestination().y()-f.y()) == 1)
            .forEach(res::add);
        // ranged two-step diagonal shooting (moves, not capture!)
        for (int[] d : new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}}) {
            Place mid = new Place(f.x()+d[0], f.y()+d[1]);
            Place dest= new Place(f.x()+2*d[0],f.y()+2*d[1]);
            if (mid.x()>=0&&mid.x()<g.getConfiguration().getSize()
             &&mid.y()>=0&&mid.y()<g.getConfiguration().getSize()
             &&dest.x()>=0&&dest.x()<g.getConfiguration().getSize()
             &&dest.y()>=0&&dest.y()<g.getConfiguration().getSize()) {
                // cannot jump over your own piece
                if (g.getPiece(mid)==null || !g.getPiece(mid).getPlayer().equals(p)) {
                    res.add(new Move(f,dest));
                }
            }
        }
        return res;
    }
    @Override public int getValue() { return 3; }
}

class Healer extends AbstractPiece {
    public Healer(Player p){ super(p); }
    public List<Move> getAvailableMoves(Game g, Place f) {
        List<Move> res = new ArrayList<>();
        for (int dx=-1;dx<=1;dx++) for (int dy=-1;dy<=1;dy++){
            if (dx==0&&dy==0) continue;
            Place np = new Place(f.x()+dx,f.y()+dy);
            if (np.x()>=0&&np.x()<g.getConfiguration().getSize()
             &&np.y()>=0&&np.y()<g.getConfiguration().getSize()) {
                res.add(new Move(f,np));
            }
        }
        return res;
    }
    @Override public int getValue() { return 2; }
}


// --- CONSOLE PLAYER ---
package assignment.player;

import assignment.protocol.Game;
import assignment.protocol.Move;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsolePlayer extends Player {
    private static final Pattern PT = Pattern.compile("^([a-z])(\\d+)->([a-z])(\\d+)$");
    private final Scanner sc = new Scanner(System.in);

    public ConsolePlayer(String name, char color, int direction) {
        super(name, color, direction);
    }

    @Override
    public Move nextMove(@NotNull Game game) {
        while (true) {
            try {
                String line = sc.nextLine().trim().toLowerCase();
                Matcher m = PT.matcher(line);
                if (m.matches()) {
                    int sx = m.group(1).charAt(0) - 'a';
                    int sy = Integer.parseInt(m.group(2)) - 1;
                    int dx = m.group(3).charAt(0) - 'a';
                    int dy = Integer.parseInt(m.group(4)) - 1;
                    return new Move(new assignment.protocol.Place(sx,sy), new assignment.protocol.Place(dx,dy));
                }
            } catch (NoSuchElementException|NumberFormatException ignored) { }
            System.out.println("Invalid input, please try again.");
        }
    }
}