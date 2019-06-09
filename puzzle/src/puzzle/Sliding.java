package puzzle;

import java.awt.Container;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

//패널을 이용한 그림퍼즐게임

public class Sliding extends JPanel implements MouseListener{

	int count = 0; // count : 증가변수, game : 실제 저장된 값
	static int game[];
	static int n; // 행, 열
	int level;
	Image original, blank; // 원본 이미지
	BufferedImage img[]; // 원본 이미지를 잘라 저장할 배열
	int width, height; // 잘라낸 그림 1개의 크기
	int clickCount, clickNum; // 클릭수 카운트, 이전에 클릭한 위치
	long t_start, t_end;
	// =========================
	static JButton btn_robot = new JButton("AI 도와줘!");

	public Sliding() {

		String s = Pop();
		StringTokenizer st = new StringTokenizer(s);
		n = Integer.parseInt(st.nextToken());
		level = Integer.parseInt(st.nextToken());

		// 원본 그림 읽기
		MediaTracker tracker = new MediaTracker(this); //// 이미지에만 적용되는 쓰레드역할 단독사용해야함
		/*
		 * 이미지가 올려지는동안 부분적으로 보여지는것을 볼수있다. 이런현상을 방지하기위해 MediaTracker를 사용한다. MediaTracker는
		 * 그림이 완전히 올려지면 그이미지를 보여준다.
		 */

		original = Toolkit.getDefaultToolkit().getImage("default.jpg"); // 1.jpg라는 그림파일을 프로젝트 안에 넣어야 함
		original = original.getScaledInstance(500, 500, Image.SCALE_DEFAULT);
		blank = Toolkit.getDefaultToolkit().getImage("blank.jpg");
		tracker.addImage(original, 0);
		try {
			tracker.waitForAll(); // waitForAll(); : Starts loading all images tracked by this media tracker.
		} catch (InterruptedException e) {
		}
		width = original.getWidth(this) / n;
		height = original.getHeight(this) / n;
		setSize(new Dimension(width * n, height * n));

		// 이미지를 잘라 넣자
		img = new BufferedImage[n * n];
		int cnt = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == n - 1 && j == n - 1) {
					img[cnt] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					Graphics g = img[cnt].getGraphics();

					// 원본이미지에서 필요한 부분만 잘라서 그리기
					g.drawImage(blank, 0, 0, width, height, // 그려질 위치
							j * width, i * height, (j + 1) * width, (i + 1) * height, this); // 그림을 잘라낼부분
					cnt++;
					break;
				}

				img[cnt] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = img[cnt].getGraphics();

				// 원본이미지에서 필요한 부분만 잘라서 그리기
				g.drawImage(original, 0, 0, width, height, // 그려질 위치
						j * width, i * height, (j + 1) * width, (i + 1) * height, this); // 그림을 잘라낼부분
				cnt++;
			}
		}
		setVisible(true);
		shuffle(level); // 숫자 섞기
		addMouseListener(this); // 마우스 리스너 등록

		t_start = System.currentTimeMillis();
	}

	// game배열의 숫자 섞기
	private void shuffle(int level) {
		char[] dir = { 'l', 'r', 'u', 'd' };
		int block = n * n - 1;

		game = new int[n * n];
		Random rand = new Random();

		for (int i = 0; i < n * n; i++)
			game[i] = i;

		do {
			for (int i = 0; i < Math.pow((n * n) * 5, level); i++) {
				block = move(dir[rand.nextInt(4)], block, false);
			}
		} while (endGame());

	}

	// 그리기 코드에 paint에 몰아준다.
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
		frame.setResizable(false); // 프레임의 크기를 변경할수 없다(false)
		Container panel = frame.getContentPane();
		panel.add(pane);

		btn_robot.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Puzzle puzzle = new Puzzle(game, n);

                // Solve the puzzle.
                puzzle.solve();
            }
        });
		btn_robot.setLocation(200, 505);
		btn_robot.setSize(100, 30);
		frame.add(btn_robot);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public int move(char dir, int imgNo, Boolean human) {
		int temp = 0;

		switch (dir) {
		case 'l':
			if (imgNo % n != n - 1) {
				temp = game[imgNo];
				game[imgNo] = game[imgNo + 1];
				game[imgNo + 1] = temp;
				imgNo += 1;
			}
			break;

		case 'r':
			if (imgNo % n != 0) {
				temp = game[imgNo];
				game[imgNo] = game[imgNo - 1];
				game[imgNo - 1] = temp;
				imgNo -= 1;
			}
			break;

		case 'u':
			if (imgNo + n < n * n) {
				temp = game[imgNo];
				game[imgNo] = game[imgNo + n];
				game[imgNo + n] = temp;
				imgNo += n;
			}
			break;

		case 'd':
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

		if (human) {
			if (tfend) { // if(조건식)에서 조건식이 true면 if문실행
				t_end = System.currentTimeMillis();

				String name = JOptionPane.showInputDialog("성공하셨습니다! 이름을 입력해주세요!");
				if (name == null) {
					JOptionPane.showMessageDialog(null, "이름을 설정하지 않으셔서 기본값인 '플레이어'로 입력됩니다.");
					name = "플레이어";
				}
				String time = String.valueOf((t_end - t_start) / 1000);
				System.out.println("실행 시간 : " + time + "초");

				Filerank fr = new Filerank(name, time);
			}
		}

		return imgNo;
	}

	// 마우스 리스너 구현
	public void mouseClicked(MouseEvent e) {
		int x = e.getX(); // x좌표
		int y = e.getY(); // y좌표
		int r = y / height; // 그림 한개의 높이로 나눈다
		int c = x / width; // 그림 한개의 폭으로 나눈다
		int imgNo = r * n + c; // 배열 첨자
		String coord = "(" + x + ", " + y + ") - (" + r + ", " + c + ") - " + imgNo + " : " + game[imgNo];
		// imgNo : 현재 그림 위치 / game[imgNo] : 정답위치 / clickNum = imgNo
		System.out.println(coord);

		// =======================================================

		clickNum = imgNo;
		int blank = n * n - 1;

		try {
			if (game[imgNo + 1] == blank) {
				move('l', imgNo, true);
			}
		} catch (ArrayIndexOutOfBoundsException exception) {

		}
		try {
			if (game[imgNo - 1] == blank) {
				move('r', imgNo, true);
			}
		} catch (ArrayIndexOutOfBoundsException exception) {

		}
		try {
			if (game[imgNo + n] == blank) {
				move('u', imgNo, true);
			}
		} catch (ArrayIndexOutOfBoundsException exception) {

		}
		try {
			if (game[imgNo - n] == blank) {
				move('d', imgNo, true);
			}
		} catch (ArrayIndexOutOfBoundsException exception) {

		}
	}

	// 게임 종료를 확인하는 메소드
	private boolean endGame() {
		boolean endGame = true;
		for (int i = 0; i < game.length; i++) {
			if (i != game[i]) {
				endGame = false;
			}
		}

		return endGame;
	}

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