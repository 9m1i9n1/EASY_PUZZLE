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
	BufferedReader br = null;
	// ===================== 프레임
	JFrame rankframe = new JFrame();
	JPanel rankpanel = new JPanel();
	// ======================
	List<Rankdata> member = new ArrayList<Rankdata>();
	BufferedWriter bw = null;
	// =============================
	Rankdata rd = new Rankdata();
	DefaultTableModel model;
	// =========================
	JTable userTable;
	JScrollPane listJs;
	JPanel listPanel;

	Vector<String> userColumn = new Vector<String>();
	Vector<String> userRow;
	
	Filerank() {
		
	}

	Filerank(String name, String time) { // 생성자
		JFrame frame = new JFrame();

		userColumn.addElement("순위");
		userColumn.addElement("이름"); // 열 추가
		userColumn.addElement("소요 시간(초)");

		model = new DefaultTableModel(userColumn, 0){ public boolean isCellEditable(int i, int c){ return false; } }; // 테이블 모델 생성
		userTable = new JTable(model); // 테이블 생성

		listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		listJs = new JScrollPane(userTable);
		listPanel.add(listJs, BorderLayout.CENTER);

		frame.setTitle("순위표");
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.add(listPanel);
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		filereader(name, time);
		ranking(); // 개인석차
		
		tablesetting(userTable);
	}
	
	public void tablesetting(JTable t){
        t.getTableHeader().setReorderingAllowed(false);      
        //테이블 컬럼의 이동을 방지한다. 이거 안쓰면 마우스로 드로그 앤 드롭으로 엉망진창이 될수 있다.
 
        t.getColumnModel().getColumn(0).setPreferredWidth(40);
        t.getColumnModel().getColumn(0).setResizable(false);
        t.getColumnModel().getColumn(1).setPreferredWidth(100);
        t.getColumnModel().getColumn(3).setPreferredWidth(40);
       
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); // 디폴트테이블셀렌더러를 생성
		dtcr.setHorizontalAlignment(SwingConstants.CENTER); // 렌더러의 가로정렬을 CENTER로

		TableColumnModel tcm = t.getColumnModel(); // 정렬할 테이블의 컬럼모델을 가져옴

		// 전체 열에 지정
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setCellRenderer(dtcr);
			// 컬럼모델에서 컬럼의 갯수만큼 컬럼을 가져와 for문을 이용하여
			// 각각의 셀렌더러를 아까 생성한 dtcr에 set해줌
			//
		}
    }

	void ranking() {
		Collections.sort(member, new Comparator<Rankdata>() {

			@Override
			public int compare(Rankdata d1, Rankdata d2) {
				// TODO Auto-generated method stub
				return Integer.parseInt(d1.gettime()) - Integer.parseInt(d2.gettime());
			}
		});

		int cnt = 1;
		for (Rankdata r : member) {
			System.out.println(r.getname() + ", " + r.gettime());
			userRow = new Vector<String>(); // 내용추가 (행추가)
			userRow.addElement(Integer.toString(cnt));
			userRow.addElement(r.getname());
			userRow.addElement(r.gettime());
			model.addRow(userRow);
			cnt++;
		}

		filewriter("", "");
	}

	void filewriter(String name, String time) {
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			try {
				bw = new BufferedWriter(new FileWriter(FILE_NAME));
				bw.write(name);
				bw.write(",");
				bw.write(time);
				bw.newLine();
			} catch (Exception e) {
				System.out.println("파일저장 중에 오류가 발생했습니다!");
			} finally {
				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	void filereader(String name, String time) {
		rd = new Rankdata(name, time);
		member.add(rd);

		try {
			br = new BufferedReader(new FileReader(FILE_NAME));

			int cnt = 0; // 이름을 저장하는데 사용될 카운터
			while (true) {
				String str = br.readLine();
				if (str == null)
					break;
				String arr[] = str.split(",");
				rd = new Rankdata(arr[0], arr[1]);
				member.add(rd);
				cnt++;
			}

		} catch (FileNotFoundException fnfe) {
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
