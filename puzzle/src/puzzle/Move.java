package puzzle;

// Move Å¬·¡½º
class Move {
  private Move() {}

  public static State up(State state, int n) {
    if (state.blank - n >= 0)
      return new State(state, state.blank - n, 'u');
    return null;
  }

  public static State down(State state, int n) {
    if (state.blank + n < n * n)
      return new State(state, state.blank + n, 'd');
    return null;
  }

  public static State left(State state, int n) {
    if (state.blank % n != 0)
      return new State(state, state.blank - 1, 'l');
    return null;
  }

  public static State right(State state, int n) {
    if (state.blank % n != n - 1)
      return new State(state, state.blank + 1, 'r');
    return null;
  }

}