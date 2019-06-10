package puzzle;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class Filerank {
	final static String FILE_NAME = "rank.txt";
	// =====================
	JFrame rankframe = new JFrame();
	JPanel rankpanel = new JPanel();
	// ======================
	Rankdata rd = new Rankdata(); // 각 플레이어 정보 클래스
	List<Rankdata> player = new ArrayList<Rankdata>(); // 플레이어 모음 List
	// 파일 입출력용으로 사용
	BufferedReader br = null;
	BufferedWriter bw = null;
	// ======================
	JTable userTable;
	JScrollPane listJs;
	DefaultTableModel model;
	// ======================
	JPanel listPanel;
	// ======================
	Vector<String> userColumn = new Vector<String>(); // Jtable에 넣기 위한 배열
	Vector<String> userRow;
	// ======================
	
	Filerank() {}

	Filerank(String name, String time) {
		JFrame frame = new JFrame();

		// Jtable Column 추가
		userColumn.addElement("순위");
		userColumn.addElement("이름");
		userColumn.addElement("소요 시간(초)");

		model = new DefaultTableModel(userColumn, 0){ public boolean isCellEditable(int i, int c){ return false; } }; // 테이블 모델 생성
		userTable = new JTable(model); // 테이블 생성

		listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		listJs = new JScrollPane(userTable);
		listPanel.add(listJs, BorderLayout.CENTER);

		frame.setTitle("순위표");
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null); // 화면 중앙 배치
		frame.add(listPanel);
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// 기존 랭킹 불러오기
		filereader(name, time);
		
		// 신규 추가하여 랭킹 내기
		ranking();
		
		tablesetting(userTable);
	}
	
	// Jtable 세팅
	public void tablesetting(JTable t){
        t.getTableHeader().setReorderingAllowed(false); //테이블 컬럼 이동 방지
 
        // 컬럼 크기 세팅
        t.getColumnModel().getColumn(0).setPreferredWidth(40);
        t.getColumnModel().getColumn(0).setResizable(false);
        t.getColumnModel().getColumn(1).setPreferredWidth(100);
        t.getColumnModel().getColumn(3).setPreferredWidth(40);
       
        // 셀 정렬 조절 객체 생성
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER); // 데이터 가운데 정렬

		TableColumnModel tcm = t.getColumnModel(); // 테이블 columnmodel 가져옴

		// 전체 열에 지정
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setCellRenderer(dtcr);
		}
    }

	// 순위별로 정렬하여 Jtable에 데이터 쏴주는 함수
	void ranking() {
		Collections.sort(player, new Comparator<Rankdata>() {
			// 해결 시간순으로 정렬
			@Override
			public int compare(Rankdata d1, Rankdata d2) {
				// TODO Auto-generated method stub
				return Integer.parseInt(d1.gettime()) - Integer.parseInt(d2.gettime());
			}
		});

		int cnt = 1;
		for (Rankdata r : player) {
			// 행마다 데이터 설정
			userRow = new Vector<String>();
			userRow.addElement(Integer.toString(cnt));
			userRow.addElement(r.getname());
			userRow.addElement(r.gettime());
			model.addRow(userRow);
			cnt++;
		}

		// 파일 출력
		filewriter("", "");
	}

	// Jtable의 정렬된 데이터 바탕으로 신규 플레이어 추가하여 파일 입력
	void filewriter(String name, String time) {
		// rank.txt 파일이 기존에 있을 경우의 파일 출력
		if (name.isEmpty()) {
			try {
				bw = new BufferedWriter(new FileWriter(FILE_NAME));

				for (int row = 0; row < userTable.getRowCount(); row++) {
					bw.write(userTable.getValueAt(row, 1).toString());
					bw.write(",");
					bw.write(userTable.getValueAt(row, 2).toString());
					bw.newLine();
				}
			} catch (Exception e) {
				System.out.println("파일저장 중에 오류가 발생했습니다!");
			} finally {
				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
		// rank.txt 파일이 기존에 없을 경우의 파일 출력
		else {
			try {
				bw = new BufferedWriter(new FileWriter(FILE_NAME));
				bw.write(name); bw.write(","); bw.write(time);
				bw.newLine();
			} catch (Exception e) {
				System.out.println("파일저장 중에 오류가 발생했습니다!");
			} finally {
				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 기존 rank.txt 파일 불러오기
	void filereader(String name, String time) {
		// 방금 입력한 플레이어 정보 저장
		rd = new Rankdata(name, time);
		player.add(rd);

		// 기존 rank.txt 파일 있을 경우
		try {
			br = new BufferedReader(new FileReader(FILE_NAME));

			int cnt = 0;
			while (true) {
				String str = br.readLine();
				if (str == null)
					break;
				String arr[] = str.split(",");
				rd = new Rankdata(arr[0], arr[1]);
				player.add(rd);
				cnt++;
			}

		} catch (FileNotFoundException fnfe) {
			// 기존 rank.txt 파일 없을 경우
			filewriter(name, time);

		} catch (IOException ioe) {
			System.out.println("파일을 읽을 수 없습니다.");
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
		}
	}
}
