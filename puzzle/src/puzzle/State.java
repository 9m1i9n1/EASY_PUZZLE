package puzzle;

import java.util.Arrays;

class State {
  public int[] array; // 전체적인 퍼즐 위치
  public int blank; // blank 퍼즐 위치
  // =============================
  private int g; //시작부터의 경로
  private int h; //목표까지의 경로
  private State previous; // 이전 State
  // =============================
  private String dir = ""; //총 방향 저장할 String
  // =============================
  
  // 새로운 State 만들때의 생성자
  public State(int[] input) {
	int n = (int) Math.sqrt(input.length); // 퍼즐 행, 열
	
	array = new int[n];
    this.array = input;
    this.blank = getblank(input, 0);
    this.previous = null;
    this.g = 0;
    this.h = Puzzle.getHeuristic(this.array);
  }

  // 진행중인 State를 만들때의 생성자 (previous : 이전 State, c : 이전 State dir)
  public State(State previous, int blankid, char c) {
    this.array = Arrays.copyOf(previous.array, previous.array.length);
    this.array[previous.blank] = this.array[blankid];
    this.array[blankid] = 0;
    this.blank = blankid;
    // ===========================
    this.g = previous.g + 1;
    this.h = Puzzle.getHeuristic(this.array);
    this.previous = previous;
    this.dir += c;
  }

  // blank 위치 찾는 함수
  public static int getblank(int[] array, int val) {
    for (int i = 0; i < array.length; i++)
      if (array[i] == val) return i;
    return -1;
  }

  // 퍼즐 완성 확인 함수
  public boolean isSolved() {
    int[] p = this.array;
    
    for (int i = 1; i < p.length - 1; i++) {
      if(p[i-1] > p[i]) {
    	  return false;
      }
    }
    return (p[0] == 1);
  }

  // 경로 뿌려주는 함수
  public String allSteps() {
	String s = new String();
    if (this.previous != null) s += previous.allSteps();
    s += dir;
    return s;
  }

  // g(n) 리턴 (현재 단계 수)
  public int g() {
    return this.g;
  }

  // h(n) 리턴 (현재 휴리스틱)
  public int h() {
    return this.h;
  }

  // f(n) 리턴 (현재 총 비용 <g + h>)
  public int f() {
    return g() + h();
  }

  // 이전 State 리턴
  public State getPrevious() {
    return this.previous;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    State state = (State) o;
    return Arrays.equals(array, state.array);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(array);
  }
}
