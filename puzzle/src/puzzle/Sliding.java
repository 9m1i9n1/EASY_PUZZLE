package puzzle;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class Sliding extends JPanel implements MouseListener {
	// =========================
	int count = 0; // count : 증가변수
	static int game[]; // game : 실제 저장된 값
	static int n; // 행, 열
	int level; // 난이도 설정
	// =========================
	Image original, blank; // original : 원본 이미지, blank : blank 이미지
	BufferedImage img[]; // 원본 이미지를 잘라 저장할 배열
	int width, height; // 잘라낸 그림 1개의 크기
	int clicknum; // 이전에 클릭한 위치
	long t_start, t_end; // 시간 측정
	// =========================
	static JButton btn_robot = new JButton("AI 도와줘!"); // 버튼
	// =========================

	public Sliding() {
		String s = Pop(); //초기 dialog
		
		StringTokenizer st = new StringTokenizer(s);
		n = Integer.parseInt(st.nextToken());
		level = Integer.parseInt(st.nextToken());

		// 원본 그림 읽기, 이미지 부분노출 방지로 mediatracker사용.
		MediaTracker tracker = new MediaTracker(this);

		original = Toolkit.getDefaultToolkit().getImage("default.jpg");
		original = original.getScaledInstance(500, 500, Image.SCALE_DEFAULT);
		
		blank = Toolkit.getDefaultToolkit().getImage("blank.jpg");
		
		tracker.addImage(original, 0);
		try {
			tracker.waitForAll();
		} catch (InterruptedException e) {}
		
		// 퍼즐 1개의 사이즈 설정
		width = original.getWidth(this) / n;
		height = original.getHeight(this) / n;
		setSize(new Dimension(width * n, height * n));

		img = new BufferedImage[n * n];
		
		// 사이즈에 맞게 그림 자르기
		int cnt = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {				
				if (i == n - 1 && j == n - 1) { //맨 마지막 블럭은 blank로 설정
					img[cnt] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					Graphics g = img[cnt].getGraphics();

					g.drawImage(blank, 0, 0, width, height,
							j * width, i * height, (j + 1) * width, (i + 1) * height, this);
					cnt++;
					break;
				}

				img[cnt] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = img[cnt].getGraphics();

				// 원본이미지에서 잘라서 그림
				g.drawImage(original, 0, 0, width, height, // 그려질 위치
						j * width, i * height, (j + 1) * width, (i + 1) * height, this); // 그림을 잘라낼부분
				cnt++;
			}
		}
		setVisible(true);
		shuffle(level); // 숫자 섞기
		addMouseListener(this); // 마우스 리스너 등록

		t_start = System.currentTimeMillis(); // 시작 시간
	}

	// game배열의 숫자 섞는 함수
	private void shuffle(int level) {
		char[] dir = { 'l', 'r', 'u', 'd' };
		int block = n * n - 1;

		game = new int[n * n];
		Random rand = new Random();

		for (int i = 0; i < n * n; i++)
			game[i] = i;

		do {
			for (int i = 0; i < Math.pow((n * n) * 5, level); i++) {
				block = move(dir[rand.nextInt(4)], block, 0);
			}
		} while (endGame());

	}

	// 그림 업데이트용
	public void paint(Graphics g) {
		int cnt = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				g.drawImage(img[game[cnt]], j * width, i * height, (j + 1) * width, (i + 1) * height, // 그려질위치
						0, 0, width, height, this); // 잘라낼부분
				cnt++;
			}
		}
	}

	public static void main(String[] args) {
		Sliding pane = new Sliding();
		JFrame frame = new JFrame("MIN'S SLIDE PUZZLE");
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(513, 580);
		frame.setResizable(false);
		frame.add(pane);

		// 버튼 리스너
		btn_robot.addActionListener(new ActionListener() {
			int block = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				int temp[] = new int[n * n];

				// game[]의 blank를 0으로 변경
				for (int i = 0; i < temp.length; i++) {
					if (game[i] == n * n - 1) {
						block = i;
						temp[i] = 0;
					} else {
						temp[i] = game[i] + 1;
					}
				}

				Puzzle puzzle = new Puzzle(temp, n);
				String str_dir = puzzle.solve();
				
				// 받아온 방향대로 자동 move
				for (int i = 0; i < str_dir.length(); i++) {
					block = pane.move(str_dir.charAt(i), block, 2);
				}
			}
		});
		btn_robot.setLocation(200, 505);
		btn_robot.setSize(100, 30);
		frame.add(btn_robot);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	// 방향에 맞게 퍼즐 위치 변경(blank 기준)
	public int move(char dir, int imgNo, int human) {
		int temp = 0;

		switch (dir) {
		case 'r':
			if (imgNo % n != n - 1) {
				temp = game[imgNo];
				game[imgNo] = game[imgNo + 1];
				game[imgNo + 1] = temp;
				imgNo += 1;
			}
			break;

		case 'l':
			if (imgNo % n != 0) {
				temp = game[imgNo];
				game[imgNo] = game[imgNo - 1];
				game[imgNo - 1] = temp;
				imgNo -= 1;
			}
			break;

		case 'd':
			if (imgNo + n < n * n) {
				temp = game[imgNo];
				game[imgNo] = game[imgNo + n];
				game[imgNo + n] = temp;
				imgNo += n;
			}
			break;

		case 'u':
			if (imgNo - n >= 0) {
				temp = game[imgNo];
				game[imgNo] = game[imgNo - n];
				game[imgNo - n] = temp;
				imgNo -= n;
			}
			break;
		}
		
		repaint();

		boolean tfend = endGame();

		// 0 = 사람아님, 1 = 사람, 2 = ai
		if (human == 1) {
			if (tfend) { // if(조건식)에서 조건식이 true면 if문실행
				t_end = System.currentTimeMillis();

				String name = JOptionPane.showInputDialog("성공하셨습니다! 이름을 입력해주세요!");
				if (name == null) {
					JOptionPane.showMessageDialog(null, "이름을 설정하지 않으셔서 기본값인 '플레이어'로 입력됩니다.");
					name = "플레이어";
				}
				String time = String.valueOf((t_end - t_start) / 1000);
				System.out.println("실행 시간 : " + time + "초");

				// 랭킹 창
				Filerank fr = new Filerank(name, time);
			}
		} else if (human == 2) {
			if (tfend) {
				JOptionPane.showMessageDialog(null, "컴퓨터가 풀었네요. 기록은 입력하지 않습니다.");
				System.exit(0);
			}
		}

		return imgNo;
	}

	// 마우스 리스너
	public void mouseClicked(MouseEvent e) {
		int x = e.getX(); // x좌표
		int y = e.getY(); // y좌표
		int r = y / height; // 작은 그림 한개 세로
		int c = x / width; // 작은 그림 한개 가로
		int imgNo = r * n + c; // 현재 이미지 위치
		// =======================================================

		clicknum = imgNo;
		int blank = n * n - 1;

		try {
			if (game[imgNo + 1] == blank) {
				move('r', imgNo, 1);
			}
		} catch (ArrayIndexOutOfBoundsException exception) {

		}
		try {
			if (game[imgNo - 1] == blank) {
				move('l', imgNo, 1);
			}
		} catch (ArrayIndexOutOfBoundsException exception) {

		}
		try {
			if (game[imgNo + n] == blank) {
				move('d', imgNo, 1);
			}
		} catch (ArrayIndexOutOfBoundsException exception) {

		}
		try {
			if (game[imgNo - n] == blank) {
				move('u', imgNo, 1);
			}
		} catch (ArrayIndexOutOfBoundsException exception) {

		}
	}

	// 퍼즐 완성 확인 함수
	private boolean endGame() {
		for (int i = 1; i < game.length; i++) {
		      if(game[i-1] > game[i]) {
		    	  return false;
		      }
		    }
		return true;
	}

	// 초기 난이도, n 세팅 함수
	private String Pop() {
		String n = JOptionPane.showInputDialog("가로 몇 줄의 퍼즐을 원하세요? (2이상 숫자입력)");
		if (n == null || !isDigit(n)) {
			JOptionPane.showMessageDialog(null, "2 이상의 숫자를 입력해주세요!");
			System.exit(0);
		}

		String[] dif = { "쉬움", "보통", "어려움" };
		Object level = JOptionPane.showInputDialog(null, "난이도를 선택해주세요!", "난이도 설정", JOptionPane.QUESTION_MESSAGE, null,
				dif, dif[0]);
		if (level == null) {
			JOptionPane.showMessageDialog(null, "난이도를 선택해야합니다!");
			System.exit(0);
		} else {
			if (level.equals("쉬움")) {
				level = "1";
			} else if (level.equals("보통")) {
				level = "2";
			} else {
				level = "3";
			}
		}

		return n + " " + level;
	}

	// 숫자 판별 함수
	private boolean isDigit(String value) {
		if ("".equals(value)) {
			return false;
		}

		if (value.length() == 1) {
			if (value.equals("0") || value.equals("1")) {
				return false;
			}
		}

		for (int i = 0; i < value.length(); i++) {
			char result = value.charAt(i);

			if (result < 48 || result > 57) { // 문자
				return false;
			}
		}

		return true;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}