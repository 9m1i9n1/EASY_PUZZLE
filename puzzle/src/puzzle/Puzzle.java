package puzzle;

import java.util.*;

public class Puzzle {

  // =======================
  public State initState; // 퍼즐의 초기상태
  public State state; // 퍼즐의 현재상태
  // =======================
  static final int CAPACITY = 1000; // 큐 초기값
  // =======================
  private static int num; // 행, 열
  // a* search 를 위한 우선순위큐
  public final PriorityQueue<State> queue = new PriorityQueue<State>(CAPACITY, new Comparator<State>() {
    public int compare(State o1, State o2) {
      return o1.f() - o2.f();
    }
  });
  // 방문 체크하는 hashset
  public final HashSet<State> visit = new HashSet<State>();
  // =======================
  public Puzzle(int[] puzzleInput, int n) {
	num = n;
    this.initState = new State(puzzleInput);
    this.state = this.initState;
  }

  // 현재 휴리스틱을 계산 함수 (각 조각의 맨하탄 거리 합계)
  public static int getHeuristic(int[] array) {
    int heuristic = 0;

    for(int i = 0; i < array.length; i++) {
      if (array[i] != 0)
        heuristic += getManhattanDistance(i, array[i]);
    }
    return heuristic;
  }

  // 목표까지의 맨하탄 거리 계산 함수 (index : 현재 인덱스, number : 퍼즐 실제 값)
  public static int getManhattanDistance(int index, int number) {
    return Math.abs((index / num) - ((number-1) / num)) + Math.abs((index % num) - ((number-1) % num));
  }

  // 다음상태 큐에 추가 함수
  private void addToQueue(State nextState) {
	// 유효한 이동, 방문되지 않았을 경우
    if(nextState != null && !this.visit.contains(nextState)) {
    	this.queue.add(nextState);
    }
  }

  // 퍼즐 푸는 함수
  public String solve() {
    queue.clear(); // 초기상태로 clear
    queue.add(this.initState);

    while(!queue.isEmpty()) {
      // 다음 경로 확인
      this.state = queue.poll();

      // 퍼즐이 풀렸는지 확인
      if (this.state.isSolved()) {
        System.out.println(this.state.allSteps());
        return this.state.allSteps();
      }

      // 방문한 hashset에 추가
      visit.add(state);

      // blank 이동
      this.addToQueue(Move.up(state, num));
      this.addToQueue(Move.down(state, num));
      this.addToQueue(Move.left(state, num));
      this.addToQueue(Move.right(state, num));
    }
	return null;
  }
}

