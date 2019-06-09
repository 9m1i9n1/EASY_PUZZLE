package puzzle;

import java.io.*;
import java.util.*;

/**
 * This class contains logic and properties that are related to
 * a sliding puzzle.
 *
 * @author Casey Scarborough
 */
public class Puzzle {

  /** The initial state of the puzzle. */
  public State initialState;

  /** The current state of the puzzle. */
  public State state;

  /** The initial capacity of the queue. */
  static final int CAPACITY = 100;

  /** The filename for the file to output to, if given. */
  private String outFile;
  
  private static int n;

  /** The A * Search priority queue used to solve the puzzle. */
  public final PriorityQueue<State> queue = new PriorityQueue<State>(CAPACITY, new Comparator<State>() {
    public int compare(State o1, State o2) {
      return o1.f() - o2.f();
    }
  });

  /** A Hash set containing the states that have been visited. */
  public final HashSet<State> visited = new HashSet<State>();

  /**
   * Constructor for puzzle class.
   * @param puzzleInput Valid sliding puzzle in 2D array format.
   */
  public Puzzle(int[] puzzleInput, int n) {
    this.initialState = new State(puzzleInput);
    this.state = this.initialState;
    this.n = n;
  }

  /**
   * Constructor for puzzle class.
   * @param puzzleInput Valid sliding puzzle in 2D array format.
   * @param outFile A filename to output solution to.
   */
  public Puzzle(int[] puzzleInput, String outFile) {
    this.initialState = new State(puzzleInput);
    this.state = this.initialState;
    this.outFile = outFile;
  }

  /**
   * This method checks whether or not the puzzle object it
   * is called on is a solvable puzzle or not.
   * @return True if it is solvable, false if it is not.
   */
  public boolean isSolvable() {
    int inversions = 0;
    int[] p = this.state.array;

    for(int i = 0; i < p.length - 1; i++) {
      for(int j = i + 1; j < p.length; j++)
        if(p[i] > p[j]) inversions++;
      if(p[i] == 0 && i % 2 == 1) inversions++;
    }
    return (inversions % 2 == 0);
  }

  /**
   * This method determines whether or not the data inputted by
   * the user is a valid puzzle format.
   * @param puzzleInput A string of the user's input.
   * @return True if it is valid, false if not.
   */
  public static boolean isValid(String puzzleInput) {
    if (puzzleInput.length() == 17) {
      // Check if duplicates exist in the input.
      HashSet<Integer> lump = new HashSet<Integer>();
      for(String s : puzzleInput.split(" ")) {
        int i = Integer.parseInt(s);
        if (lump.contains(i) || i > 8) return false;
        lump.add(i);
      }
      return true;
    }
    return false;
  }
  
  /**
   * This method retrieves a user's input from the console
   * and returns the input as an integer array.
   * @return An array of integers.
   */
  /*public static int[] getConsoleInput() {
    System.out.println("\nEnter a valid 8-puzzle below:");
    Scanner scanner = new Scanner(System.in);

    String str_n = scanner.nextLine();
    n = Integer.valueOf(str_n);

    return convertToArray(n);
  }
  
  /**
   * This method replaces blanks in the user's input
   * with 0s for easier solving of the puzzle.
   * @param row The row input by the user.
   * @return String the row with blanks replaced with 0s.
   */
  /*public static String handleBlankSpaces(String row) {
    row = row.replaceAll("\\s+$", "");

    if (row.length() == 3) row += " 0";
    row = row.replace("   ", " 0 ").replace("  ", "0 ");
    return row.trim();
  }*/

  /**
   * This method outputs a passed in string into a filename.
   */
  public void writeToFile(String content) throws IOException {
    File f = new File(this.outFile);
    if (!f.exists()) f.createNewFile();

    FileWriter fw = new FileWriter(f.getAbsoluteFile());
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(content);
    bw.close();
  }

  /**
   * This method converts a string of user's input into
   * an integer array to be used by the puzzle class.
   * @param s A string of 9 integers separated by spaces.
   * @return The converted integer array.
   */
  /*public static int[] convertToArray(int[] origin, int n) {
    
    int[] p = new int[n * n];
    
    for(int i = 0; i < origin.length; i++) {
    	p[i] = origin[i];
    }
    
    //p[0] = 1; p[1] = 2; p[2] = 3; p[3] = 4; p[4] = 5; p[5] = 6; p[6] = 0; p[7] = 8; 
    //p[8] = 9; p[9] = 10; p[10] = 7; p[11] = 11; p[12] = 13; p[13] =14; p[14] = 15; p[15] = 12;
    //p[0] = 1; p[1] = 3; p[2] = 0; p[3] = 4; p[4] = 2; p[5] = 5; p[6] = 7; p[7] = 8; p[8] = 6;
    //p[0] = 0; p[1] = 1; p[2] = 3; p[3] = 2;
    
    return p;
  }*/

  /**
   * This method calculates the current heuristic for a puzzle's
   * state. The heuristic it uses is the sum of the Manhattan Distance
   * of each tile from where it is located to where is should be.
   * @param array A puzzle state array.
   * @return int - The heuristic for the current puzzle.
   */
  public static int getHeuristic(int[] array) {
    int heuristic = 0;

    for(int i = 0; i < array.length; i++) {
      if (array[i] != 0)
        heuristic += getManhattanDistance(i, array[i]);
    }
    return heuristic;
  }

  /**
   * This method calculates the Manhattan Distance between a tile's
   * location and it's goal location.
   * @param index The tile's current index.
   * @param number The value of the tile.
   * @return int - The distance between the tile and it's goal state.
   */
  public static int getManhattanDistance(int index, int number) {
    return Math.abs((index / n) - ((number-1) / n)) + Math.abs((index % n) - ((number-1) % n));
  }

  /**
   * This method handles adding the next state to the queue. It
   * will only add the next state to the queue if it is a valid move
   * and the state has not been visited previously.
   * @param nextState
   */
  private void addToQueue(State nextState) {
    if(nextState != null && !this.visited.contains(nextState)) this.queue.add(nextState);
  }

  /**
   * This method handles the solving of the puzzle.
   */
  public void solve() {
    // Clear the queue and add the initial state.
    queue.clear();
    queue.add(this.initialState);
    long startTime = System.currentTimeMillis();

    while(!queue.isEmpty()) {
      // Get the best next state.
      this.state = queue.poll();

      // Check if the state is a solution.
      if (this.state.isSolved()) {
        System.out.println(this.state.solutionMessage(startTime));
        return;
      }

      // Add this state to the visited HashSet so we don't revisit it.
      visited.add(state);

      // Add valid moves to the queue.
      this.addToQueue(Move.up(state, n));
      this.addToQueue(Move.down(state, n));
      this.addToQueue(Move.left(state, n));
      this.addToQueue(Move.right(state, n));
    }
  }

  /*public static void main(String[] args) {
    Puzzle puzzle = new Puzzle(convertToArray(n));

    // Solve the puzzle.
    puzzle.solve();
  }
  */
}

