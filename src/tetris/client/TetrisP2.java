package tetris.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;

import tetris.bean.Info;
import tetris.bean.TetrisDTO;

public class TetrisP2 extends JFrame implements Runnable, ActionListener {

	private ArrayList<Color> colorList;
	private LineBorder border;
	private int x = 10;
	private int cell = 30;
	private int y = 18;
	private JPanel[][] background;
	private JPanel center;
	private JPanel[][] nextP;
	private int[][] block = new int[10][18];
	private boolean spacecheck;
	private int blockType;
	private int count;
	private Random r = new Random(); // 블럭랜덤(7개)
	private boolean keyboardcheck = true;
	private int angle;
	public static final int BLOCK_NONE = 0; // 아무것도 없음
	public static final int BLOCK_STOP = 1; // 멈춰있는블럭
	public static final int BLOCK_MOVE = 2; // 움직이는블럭
	private JPanel g = new JPanel(new GridLayout(1,2,5,5));
	private JButton startB;
	private String nickname;
	JTextArea pro;

	public TetrisP2(String nickname) {
		
		this.nickname = nickname;
		
		colorList = new ArrayList<Color>();

		colorList.add(new Color(255, 255, 0)); // 노랑
		colorList.add(new Color(95, 0, 255)); // 보라
		colorList.add(new Color(0, 215, 255)); // 하늘
		colorList.add(new Color(255, 95, 0)); // 주황
		colorList.add(new Color(30, 220, 20)); // 연두
		colorList.add(new Color(255, 0, 125));// 분홍
		colorList.add(new Color(0, 0, 255));// 빨강
		border = new LineBorder(Color.BLACK, 2);// 모서리 직선부분을 검은색으로 바꿔줌~

		center = new JPanel(); // 중앙패널만들기
		center.setSize(x * cell, y * cell); // 300,540
		center.setLayout(null);// 레이아웃 빼기
//		center.setBorder(new LineBorder(Color.RED));
		
		g.add(center);
		
		// 다음 게임에서 보여줘야 할 패널.
		JPanel next = new JPanel();
		next.setSize(120, 90);
		next.setLayout(null);
		nextP = new JPanel[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nextP[i][j] = new JPanel();
				nextP[i][j].setBounds(i * cell, j * cell, cell, cell);
				nextP[i][j].setBorder(border);
				nextP[i][j].setBackground(new Color(50, 50, 50));
				next.add(nextP[i][j]);
			}
		}

		next.setBackground(Color.WHITE);
		

		background = new JPanel[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				background[i][j] = new JPanel(); // 하나하나 패널 생성.
				background[i][j].setBounds(i * cell, j * cell, cell, cell); // cell사이즈로 모든 패널에 깔아주기
				background[i][j].setBorder(border); // 라인보더로 보더처리(검정)
				background[i][j].setBackground(new Color(50, 50, 50));// 배경색 검정으로 덮어버리기.
				center.add(background[i][j]);
			}
		}

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (keyboardcheck) {
					int keycode = e.getKeyCode();
					switch (keycode) {
					case KeyEvent.VK_RIGHT:
						goRight();
						break;
					case KeyEvent.VK_LEFT:
						goLeft();
						break;
					case KeyEvent.VK_DOWN:
						goDown();
						break;
					case KeyEvent.VK_CONTROL:
						blockup();
						break;
					case KeyEvent.VK_SPACE: // 수동으로 내려주는 건가봄.
						while (spacecheck) {
							goDown(); // spacecheck가 false, 이동할 수 없을 때까지 무한반복으로 내려가게 하는 것.
						}
						spacecheck = true; // 다 끝난시점에서 스페이스 사용 계속 가능하게 다시 값 변경
						break;// 멈춤

					}
				}
			}
		});
		
		
		startB = new JButton("start!");
		startB.setFont(new Font("HY울릉도M",Font.BOLD,30));
		startB.setBackground(new Color(255,250,205));
		startB.addActionListener(this);
		startB.setFocusable(false);
		
		pro = new JTextArea();
		pro.setFont(new Font("HY울릉도M",Font.BOLD,30));
		pro.setText("닉네임 : " + this.nickname +"\n점수 : " + count +  "                 \n");
		pro.setFocusable(false);
		
		
		JPanel e = new JPanel(new GridLayout(3,1));
		e.add(next);
		e.add(pro);
		e.add(startB);
		
		
		
		g.add(e);
		
		
		
		Container c = this.getContentPane();
		
		c.add(g);
		
		
	
		Thread t = new Thread(this);
		t.start();
		
		ImageIcon img = new ImageIcon("image/테트리스아이콘.png");
		this.setIconImage(img.getImage());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(400, 500, 700, 600);
//		setLayout(null);
		setVisible(true);

	}// 생성자
	
//	public static void main(String[] args) {
//		new TetrisP2("아");
//	}

	// 다음 나올 모양 보여주기
	public int nextShape() {
		blockType = r.nextInt(7);
		switch (blockType) {
		case 0:
			nextSquare(); // 0 => 네모
			break;

		case 1:
			nextTriangle(); // 1 => ㅗ
			break;

		case 2:
			nextStick(); // 2 => -
			break;

		case 3:
			nextRldur(); // 3 => ㄴ
			break;
		case 4:
			nextRrldur(); // 4 => _|
			break;
		case 5:
			nextFldmf(); // 5 => s
			break;
		case 6:
			nextRfldmf(); // 6 => s반대
			break;
		}

		return blockType;
	}

	// 모양생성
	public void newShape() {
		angle = 0;
		if (blockType == -1) {
			blockType = r.nextInt(7);
		}

		if (isGameOver(blockType)) {
			JOptionPane.showMessageDialog(this, "[GameOver] 점수는 " + count + "입니다.");
			count = 0;
			pro.setText("닉네임 : " + this.nickname +"\n점수 : " + count +  "                 \n");
			
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					block[i][j] = BLOCK_NONE;
					this.background[i][j].setBackground(new Color(50, 50, 50));
				}
			}
			
			
			int result = JOptionPane.showConfirmDialog(this, "다시 하시겠습니까?", "replay?" , JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) {
				startB.setEnabled(true);
				startB.setFocusable(false);
			}
			else {
				dispose();
				
			}
		

			center.setFocusable(true);

		}

		else

		{
			switch (blockType) {
			case 0:
				square(); // 0 => 네모
				break;

			case 1:
				triangle(); // 1 => ㅗ
				break;

			case 2:
				stick(); // 2 => -
				break;

			case 3:
				rldur(); // 3 => ㄴ
				break;
			case 4:
				rrldur(); // 4 => _|
				break;
			case 5:
				fldmf(); // 5 => s
				break;
			case 6:
				rfldmf(); // 6 => s반대
				break;
			}
		}
	}

	public void run() {
		try {
			while (true) {
				// 도형내려오는거.
				Thread.sleep(900);
				goDown();

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 도형들을 아래로 내려주는 메소드
	private void goDown() {
		// 이동 가능여부 체크. => isMove()
		if (isMove(1)) {
			for (int i = 9; i >= 0; i--) {
				for (int j = 17; j >= 0; j--) {
					if (block[i][j] == BLOCK_MOVE) {
						background[i][j + 1].setBackground(background[i][j].getBackground()); // 해당 블럭보다 한칸 아래로 내려가는 블럭을
																								// 위 블럭색으로 변경.
						background[i][j].setBackground(new Color(50, 50, 50)); // 현재 위치 블랙을 덮기.

						block[i][j + 1] = BLOCK_MOVE;// 움직일 수 있는 블럭
						block[i][j] = BLOCK_NONE;// 블럭이 비어있다.
					}
				}
			}
		} // if문

		else { // 이동 불가능
			for (int i = 9; i >= 0; i--) {
				for (int j = 17; j >= 0; j--) {
					if (block[i][j] == BLOCK_MOVE) { // 움직일 수 있는 블럭이면
						block[i][j] = BLOCK_STOP; // 블럭 멈추기.
					}
				}
			}

			// 한줄씩 검사
			for (int i = 0; i < 18; i++) { // y=18; 세로줄

				int cnt = 0; // 다음줄 카운트 초기화

				for (int j = 0; j < 10; j++) { // x=10; 가로줄
					if (block[j][i] == BLOCK_STOP) { // 한줄씩 검토
						cnt++;
					}
				}

				if (cnt == 10) { // 블럭 한줄이 다 멈춰있음 => 깨기
					for (int k = 0; k < 10; k++) {
						block[k][i] = BLOCK_NONE; // 깨진 공간은 사라지게 만들기(없는 공간 만들기.)
						background[k][i].setBackground(new Color(50, 50, 50));
						count += 1;

					}
					
					pro.setText("닉네임 : " + this.nickname +"\n점수 : " + count +  "                 \n");

					// 깨진 줄의 윗줄 내려주기
					for (int m = i; m > 0; m--) { // 깨진 줄이 i번째 줄이기 때문에 그 줄을 초기값으로 잡는다.
						for (int n = 0; n < 10; n++) {
							block[n][m] = block[n][m - 1]; // 깨진줄의 윗줄이 깨진줄로 내려가게끔 잡는다.
							background[n][m].setBackground(background[n][m - 1].getBackground()); // 윗블럭색깔 맞춰줌.
						}
					}

					// 맨 윗줄을 채움.
					for (int n = 0; n < 10; n++) {
						block[n][0] = BLOCK_NONE;
						background[n][0].setBackground(new Color(50, 50, 50));
					}

				}
			} // for(i)

			spacecheck = false; // 움직일 수 없을 때

			newShape();// 새로운 블럭 불러오기
			blockType = nextShape();
		} // else문

	}// godown

	// 도형들을 왼쪽으로 옮겨주는 메소드
	public void goLeft() {
		// 이동가능여부
		if (isMove(2)) {
			for (int i = 1; i < 10; i++) {
				for (int j = 17; j >= 0; j--) {
					if (block[i][j] == BLOCK_MOVE) {
						background[i - 1][j].setBackground(background[i][j].getBackground());
						background[i][j].setBackground(new Color(50, 50, 50));
						// 이동한 블럭 상태 변경
						block[i - 1][j] = BLOCK_MOVE;
						block[i][j] = BLOCK_NONE;
					}
				}
			}
		}

	}// goLeft()

	// 도형들을 오른쪽으로 옮겨주는 메소드
	public void goRight() {
		// 이동가능여부

		if (isMove(3)) {
			for (int i = 9; i >= 0; i--) {
				for (int j = 17; j >= 0; j--) {
					if (block[i][j] == BLOCK_MOVE) {
						background[i + 1][j].setBackground(background[i][j].getBackground());
						background[i][j].setBackground(new Color(50, 50, 50));
						// 이동한 블럭 상태 변경
						block[i + 1][j] = BLOCK_MOVE;
						block[i][j] = BLOCK_NONE;
					}
				}
			}
		}
	}// goRight()

	// 이동 가능한지 여부
	public boolean isMove(int number) { // number => 아래 1, 왼쪽 2, 오른쪽3,
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 18; j++) {
				if (block[i][j] == BLOCK_MOVE) {
					// godown(1)
					if (number == 1) {
						if ((j + 1) > 17) {
							return false; // center끝으로 나가지 못하게
						}
						if (block[i][j + 1] == BLOCK_STOP) {
							return false;
						} // 멈춰있는 블럭 위에 쌓아지도록
					}

					// goLeft(2)
					else if (number == 2) {
						if ((i - 1) < 0)
							return false; // 왼쪽끝으로 나가지 못하게
						if (block[i - 1][j] == BLOCK_STOP)
							return false; // 옆에 블럭이 있으니 못움직이게.

					}

					// goRight(3)
					else if (number == 3) {
						if ((i + 1) > 9)
							return false;
						if (block[i + 1][j] == BLOCK_STOP)
							return false;
					}

				}
			}
		}

		return true; // 모두 아니면 움직일 수 있게!
	} // isMove()

	// 7가지의 블럭 모양
	// 정보=====================================================================================================
	// 1. 네모모양(노랑)
	public void nextSquare() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nextP[i][j].setBackground(new Color(50, 50, 50));
			}
		}
		nextP[1][1].setBackground(this.colorList.get(0));
		nextP[1][2].setBackground(this.colorList.get(0));
		nextP[2][1].setBackground(this.colorList.get(0));
		nextP[2][2].setBackground(this.colorList.get(0));

	}

	public void square() {

		block[4][0] = BLOCK_MOVE; // 처음 나올땐 중간으로 맞춤.
		block[4][1] = BLOCK_MOVE;
		block[5][0] = BLOCK_MOVE;
		block[5][1] = BLOCK_MOVE;
		// 블럭의 색을 설정
		background[4][0].setBackground(this.colorList.get(0)); // 노란색
		background[4][1].setBackground(this.colorList.get(0));
		background[5][0].setBackground(this.colorList.get(0));
		background[5][1].setBackground(this.colorList.get(0));
	}

	// 2. ㅗ모양 - 보라
	public void nextTriangle() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nextP[i][j].setBackground(new Color(50, 50, 50));
			}
		}
		nextP[2][1].setBackground(this.colorList.get(1));
		nextP[1][2].setBackground(this.colorList.get(1));
		nextP[2][2].setBackground(this.colorList.get(1));
		nextP[3][2].setBackground(this.colorList.get(1));
	}

	public void triangle() {
		block[5][0] = BLOCK_MOVE;
		block[4][1] = BLOCK_MOVE;
		block[5][1] = BLOCK_MOVE;
		block[6][1] = BLOCK_MOVE;

		background[5][0].setBackground(this.colorList.get(1)); // 보라색
		background[4][1].setBackground(this.colorList.get(1));
		background[5][1].setBackground(this.colorList.get(1));
		background[6][1].setBackground(this.colorList.get(1));

	}

	// 3. 막대기'-' 모양 - 하늘
	public void nextStick() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nextP[i][j].setBackground(new Color(50, 50, 50));
			}
		}
		nextP[0][2].setBackground(this.colorList.get(2));
		nextP[1][2].setBackground(this.colorList.get(2));
		nextP[2][2].setBackground(this.colorList.get(2));
		nextP[3][2].setBackground(this.colorList.get(2));
	}

	public void stick() {
		block[3][0] = BLOCK_MOVE;
		block[4][0] = BLOCK_MOVE;
		block[5][0] = BLOCK_MOVE;
		block[6][0] = BLOCK_MOVE;

		background[3][0].setBackground(this.colorList.get(2)); // 하늘색
		background[4][0].setBackground(this.colorList.get(2));
		background[5][0].setBackground(this.colorList.get(2));
		background[6][0].setBackground(this.colorList.get(2));
	}

	// 4. 'ㄴ'자 모양 - 주황
	public void nextRldur() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nextP[i][j].setBackground(new Color(50, 50, 50));
			}
		}
		nextP[1][1].setBackground(this.colorList.get(3));
		nextP[1][2].setBackground(this.colorList.get(3));
		nextP[2][2].setBackground(this.colorList.get(3));
		nextP[3][2].setBackground(this.colorList.get(3));
	}

	public void rldur() {
		block[4][0] = BLOCK_MOVE;
		block[4][1] = BLOCK_MOVE;
		block[5][1] = BLOCK_MOVE;
		block[6][1] = BLOCK_MOVE;

		background[4][0].setBackground(this.colorList.get(3)); // 주황색
		background[4][1].setBackground(this.colorList.get(3));
		background[5][1].setBackground(this.colorList.get(3));
		background[6][1].setBackground(this.colorList.get(3));
	}

	// 5. '_|'자 모양 - 연두
	public void nextRrldur() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nextP[i][j].setBackground(new Color(50, 50, 50));
			}
		}
		nextP[1][2].setBackground(this.colorList.get(4));
		nextP[2][2].setBackground(this.colorList.get(4));
		nextP[3][1].setBackground(this.colorList.get(4));
		nextP[3][2].setBackground(this.colorList.get(4));
	}

	public void rrldur() {

		block[6][0] = BLOCK_MOVE;
		block[6][1] = BLOCK_MOVE;
		block[5][1] = BLOCK_MOVE;
		block[4][1] = BLOCK_MOVE;

		background[6][0].setBackground(this.colorList.get(4)); // 연두색
		background[6][1].setBackground(this.colorList.get(4));
		background[5][1].setBackground(this.colorList.get(4));
		background[4][1].setBackground(this.colorList.get(4));
	}

	// 6. 's'자모양 - 분홍
	public void nextFldmf() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nextP[i][j].setBackground(new Color(50, 50, 50));
			}
		}
		nextP[1][1].setBackground(this.colorList.get(5));
		nextP[1][2].setBackground(this.colorList.get(5));
		nextP[2][2].setBackground(this.colorList.get(5));
		nextP[2][3].setBackground(this.colorList.get(5));

	}

	public void fldmf() {
		block[4][0] = BLOCK_MOVE;
		block[4][1] = BLOCK_MOVE;
		block[5][1] = BLOCK_MOVE;
		block[5][2] = BLOCK_MOVE;

		background[4][0].setBackground(this.colorList.get(5)); // 분홍색
		background[4][1].setBackground(this.colorList.get(5));
		background[5][1].setBackground(this.colorList.get(5));
		background[5][2].setBackground(this.colorList.get(5));

	}

	// 7. 's'자 반대모양 - 빨강
	public void nextRfldmf() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				nextP[i][j].setBackground(new Color(50, 50, 50));
			}
		}
		nextP[1][2].setBackground(this.colorList.get(6));
		nextP[1][3].setBackground(this.colorList.get(6));
		nextP[2][1].setBackground(this.colorList.get(6));
		nextP[2][2].setBackground(this.colorList.get(6));

	}

	public void rfldmf() {
		block[4][1] = BLOCK_MOVE;
		block[4][2] = BLOCK_MOVE;
		block[5][0] = BLOCK_MOVE;
		block[5][1] = BLOCK_MOVE;

		background[4][1].setBackground(this.colorList.get(6)); // 빨강색
		background[4][2].setBackground(this.colorList.get(6));
		background[5][0].setBackground(this.colorList.get(6));
		background[5][1].setBackground(this.colorList.get(6));
	}

	// 랜덤으로 나온 값으로 7개의 모양 중 하나가 나왔을 때 움직일 수 없으면
	// 게임오버=========================================================================================
	public boolean isGameOver(int number) {
		switch (number)
		// 랜덤 돌려서 나온값 중복처리 안함
		{
		case 0:
			if (block[4][0] == BLOCK_STOP)
				return true;
			if (block[4][1] == BLOCK_STOP)
				return true;
			if (block[5][0] == BLOCK_STOP)
				return true;
			if (block[5][1] == BLOCK_STOP)
				return true;
			break;
		case 1:
			if (block[5][0] == BLOCK_STOP)
				return true;
			if (block[4][1] == BLOCK_STOP)
				return true;
			if (block[5][1] == BLOCK_STOP)
				return true;
			if (block[6][1] == BLOCK_STOP)
				return true;
			break;
		case 2:
			if (block[3][0] == BLOCK_STOP)
				return true;
			if (block[4][0] == BLOCK_STOP)
				return true;
			if (block[5][0] == BLOCK_STOP)
				return true;
			if (block[6][0] == BLOCK_STOP)
				return true;
			break;
		case 3:
			if (block[4][0] == BLOCK_STOP)
				return true;
			if (block[4][1] == BLOCK_STOP)
				return true;
			if (block[5][1] == BLOCK_STOP)
				return true;
			if (block[6][1] == BLOCK_STOP)
				return true;
			break;
		case 4:
			if (block[4][0] == BLOCK_STOP)
				return true;
			if (block[4][1] == BLOCK_STOP)
				return true;
			if (block[5][1] == BLOCK_STOP)
				return true;
			if (block[5][2] == BLOCK_STOP)
				return true;
			break;
		case 5:
			if (block[6][0] == BLOCK_STOP)
				return true;
			if (block[6][1] == BLOCK_STOP)
				return true;
			if (block[5][1] == BLOCK_STOP)
				return true;
			if (block[4][1] == BLOCK_STOP)
				return true;
			break;
		case 6:
			if (block[5][0] == BLOCK_STOP)
				return true;
			if (block[5][1] == BLOCK_STOP)
				return true;
			if (block[4][1] == BLOCK_STOP)
				return true;
			if (block[4][2] == BLOCK_STOP)
				return true;
			break;
		}

		return false;

	}
	
	// ========================================================[회전]===============================================================
	   public void blockup() {
	      int blockcolor = 8;
	      // if (isMove(2) && isMove(3))
	      {
	         System.out.println("Move");
	         if (angle > 3) {
	            angle = 0;
	         }
	         aa: for (int j = 0; j < 18; j++) {
	            for (int i = 0; i < 10; i++) {
	               if (block[i][j] == BLOCK_MOVE) {
	                  if (background[i][j].getBackground() == colorList.get(0)) {
	                     blockcolor = 0;
	                  } else if (background[i][j].getBackground() == colorList.get(1)) {
	                     blockcolor = 1;
	                  } else if (background[i][j].getBackground() == colorList.get(2)) {
	                     blockcolor = 2;
	                  } else if (background[i][j].getBackground() == colorList.get(3)) {
	                     blockcolor = 3;
	                  } else if (background[i][j].getBackground() == colorList.get(4)) {
	                     blockcolor = 4;
	                  } else if (background[i][j].getBackground() == colorList.get(5)) {
	                     blockcolor = 5;
	                  } else if (background[i][j].getBackground() == colorList.get(6)) {
	                     blockcolor = 6;
	                  }
	                  System.out.println(i + "   " + j);

	                  // 주황색 ㄴ자 돌리기
	                  if (blockcolor == 3 && angle == 0 && (j + 2) < 18 && (i + 2) < 10)// 유효한 위치인지 검사
	                  {
	                     background[i][j + 2].setBackground(colorList.get(3));
	                     background[i][j].setBackground(new Color(50, 50, 50));
	                     block[i][j + 2] = BLOCK_MOVE;
	                     block[i][j] = BLOCK_NONE;
	                     background[i + 1][j + 2].setBackground(colorList.get(3));
	                     background[i][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i + 1][j + 2] = BLOCK_MOVE;
	                     block[i][j + 1] = BLOCK_NONE;
	                     background[i + 1][j].setBackground(colorList.get(3));
	                     background[i + 2][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i + 1][j] = BLOCK_MOVE;
	                     block[i + 2][j + 1] = BLOCK_NONE;
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 3 && angle == 1 && (j + 2) < 18) {// 완료
	                     if (i == 1) {
	                        background[i - 1][j].setBackground(colorList.get(3));
	                        background[i + 1][j].setBackground(colorList.get(3));
	                        background[i + 1][j + 1].setBackground(colorList.get(3));
	                        block[i - 1][j] = BLOCK_MOVE;
	                        block[i + 1][j] = BLOCK_MOVE;
	                        block[i + 1][j + 1] = BLOCK_MOVE;

	                        background[i][j + 1].setBackground(new Color(50, 50, 50));
	                        background[i][j + 2].setBackground(new Color(50, 50, 50));
	                        background[i - 1][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i][j + 1] = BLOCK_NONE;
	                        block[i][j + 2] = BLOCK_NONE;
	                        block[i - 1][j + 2] = BLOCK_NONE;
	                     } else {
	                        background[i - 2][j].setBackground(colorList.get(3));
	                        background[i][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i - 2][j] = BLOCK_MOVE;
	                        block[i][j + 2] = BLOCK_NONE;
	                        background[i - 1][j].setBackground(colorList.get(3));
	                        background[i - 1][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i - 1][j] = BLOCK_MOVE;
	                        block[i - 1][j + 2] = BLOCK_NONE;
	                     }
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 3 && angle == 2 && (j + 2) < 18 && (i + 2) < 10) {
	                     background[i + 1][j + 1].setBackground(colorList.get(3));
	                     background[i + 2][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i + 1][j + 1] = BLOCK_MOVE;
	                     block[i + 2][j + 1] = BLOCK_NONE;
	                     background[i + 1][j + 2].setBackground(colorList.get(3));
	                     background[i][j].setBackground(new Color(50, 50, 50));
	                     block[i + 1][j + 2] = BLOCK_MOVE;
	                     block[i][j] = BLOCK_NONE;
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 3 && angle == 3 && (j + 2) < 18) {// 완료
	                     if (i == 8) {
	                        background[i + 1][j + 1].setBackground(colorList.get(3));
	                        background[i - 1][j].setBackground(colorList.get(3));
	                        background[i - 1][j + 1].setBackground(colorList.get(3));
	                        block[i + 1][j + 1] = BLOCK_MOVE;
	                        block[i - 1][j] = BLOCK_MOVE;
	                        block[i - 1][j + 1] = BLOCK_MOVE;
	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        background[i + 1][j].setBackground(new Color(50, 50, 50));
	                        background[i][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i + 1][j] = BLOCK_NONE;
	                        block[i + 1][j] = BLOCK_NONE;
	                        block[i][j + 2] = BLOCK_NONE;
	                     } else {
	                        background[i + 1][j + 1].setBackground(colorList.get(3));
	                        background[i + 1][j].setBackground(new Color(50, 50, 50));
	                        block[i + 1][j + 1] = BLOCK_MOVE;
	                        block[i + 1][j] = BLOCK_NONE;
	                        background[i + 2][j + 1].setBackground(colorList.get(3));
	                        background[i][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i + 2][j + 1] = BLOCK_MOVE;
	                        block[i][j + 2] = BLOCK_NONE;
	                     }
	                     angle = 0;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  System.out.println(blockcolor);

	                  // 분홍색 5번 s자 돌리기
	                  if (blockcolor == 5 && angle == 0 && (j + 2) < 18 && (i + 1) < 10) {
	                     if (i == 0) {
	                        background[i][j + 2].setBackground(colorList.get(5));
	                        block[i][j + 2] = BLOCK_MOVE;
	                        background[i + 2][j + 1].setBackground(colorList.get(5));
	                        block[i + 2][j + 1] = BLOCK_MOVE;
	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        block[i][j] = BLOCK_NONE;
	                        background[i][j + 1].setBackground(new Color(50, 50, 50));
	                        block[i][j + 1] = BLOCK_NONE;
	                     } else {
	                        background[i][j + 2].setBackground(colorList.get(5));
	                        background[i + 1][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i][j + 2] = BLOCK_MOVE;
	                        block[i + 1][j + 2] = BLOCK_NONE;
	                        background[i - 1][j + 2].setBackground(colorList.get(5));
	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        block[i - 1][j + 2] = BLOCK_MOVE;
	                        block[i][j] = BLOCK_NONE;
	                     }
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 5 && angle == 1 && (j + 1) < 18 && (i + 1) < 10 && (i - 1) >= 0) {
	                     background[i + 1][j + 1].setBackground(colorList.get(5));
	                     background[i][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i + 1][j + 1] = BLOCK_MOVE;
	                     block[i][j + 1] = BLOCK_NONE;
	                     background[i][j - 1].setBackground(colorList.get(5));
	                     background[i - 1][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i][j - 1] = BLOCK_MOVE;
	                     block[i - 1][j + 1] = BLOCK_NONE;
	                     angle = 0;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }

	                  // 역 ㄴ 돌리기 완료
	                  if (blockcolor == 4 && angle == 0 && (j + 2) < 18 && (i - 2) >= 0) {
	                     background[i - 1][j + 2].setBackground(colorList.get(4));
	                     background[i][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i - 1][j + 2] = BLOCK_MOVE;
	                     block[i][j + 1] = BLOCK_NONE;
	                     background[i - 2][j].setBackground(colorList.get(4));
	                     background[i - 2][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i - 2][j] = BLOCK_MOVE;
	                     block[i - 2][j + 1] = BLOCK_NONE;
	                     background[i - 1][j].setBackground(colorList.get(4));
	                     background[i][j].setBackground(new Color(50, 50, 50));
	                     block[i - 1][j] = BLOCK_MOVE;
	                     block[i][j] = BLOCK_NONE;
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 4 && angle == 1 && (j + 2) < 18 && (i + 1) < 10) {
	                     if (i == 0) {
	                        background[i + 2][j].setBackground(colorList.get(4));
	                        block[i + 2][j] = BLOCK_MOVE;
	                        background[i][j + 1].setBackground(colorList.get(4));
	                        block[i][j + 1] = BLOCK_MOVE;

	                        background[i + 1][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i + 1][j + 2] = BLOCK_NONE;
	                        background[i + 1][j + 1].setBackground(new Color(50, 50, 50));
	                        block[i + 1][j + 1] = BLOCK_NONE;
	                     } else {
	                        background[i - 1][j].setBackground(colorList.get(4));
	                        background[i + 1][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i - 1][j] = BLOCK_MOVE;
	                        block[i + 1][j + 2] = BLOCK_NONE;
	                        background[i - 1][j + 1].setBackground(colorList.get(4));
	                        background[i + 1][j + 1].setBackground(new Color(50, 50, 50));
	                        block[i - 1][j + 1] = BLOCK_MOVE;
	                        block[i + 1][j + 1] = BLOCK_NONE;
	                     }

	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 4 && angle == 2 && (j + 2) < 18 && (i + 2) < 10) {
	                     background[i + 1][j + 1].setBackground(colorList.get(4));
	                     background[i + 2][j].setBackground(new Color(50, 50, 50));
	                     block[i + 1][j + 1] = BLOCK_MOVE;
	                     block[i + 2][j] = BLOCK_NONE;
	                     background[i + 1][j + 2].setBackground(colorList.get(4));
	                     background[i][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i + 1][j + 2] = BLOCK_MOVE;
	                     block[i][j + 1] = BLOCK_NONE;
	                     background[i + 2][j + 2].setBackground(colorList.get(4));
	                     background[i][j].setBackground(new Color(50, 50, 50));
	                     block[i + 2][j + 2] = BLOCK_MOVE;
	                     block[i][j] = BLOCK_NONE;
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 4 && angle == 3 && (j + 2) < 18 && (i + 1) < 10) {
	                     if (i == 8) {
	                        background[i + 1][j].setBackground(colorList.get(4));
	                        block[i + 1][j] = BLOCK_MOVE;
	                        background[i - 1][j + 1].setBackground(colorList.get(4));
	                        block[i - 1][j + 1] = BLOCK_MOVE;
	                        background[i + 1][j + 1].setBackground(colorList.get(4));
	                        block[i + 1][j + 1] = BLOCK_MOVE;

	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        block[i][j] = BLOCK_NONE;
	                        background[i][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i][j + 2] = BLOCK_NONE;
	                        background[i + 1][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i + 1][j + 2] = BLOCK_NONE;
	                     } else {

	                        background[i + 1][j + 1].setBackground(colorList.get(4));
	                        background[i][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i + 1][j + 1] = BLOCK_MOVE;
	                        block[i][j + 2] = BLOCK_NONE;

	                        background[i + 2][j + 1].setBackground(colorList.get(4));
	                        background[i + 1][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i + 2][j + 1] = BLOCK_MOVE;
	                        block[i + 1][j + 2] = BLOCK_NONE;

	                        background[i + 2][j].setBackground(colorList.get(4));
	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        block[i + 2][j] = BLOCK_MOVE;
	                        block[i][j] = BLOCK_NONE;
	                     }

	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }

	                  // 스틱 돌리기 완료
	                  if (blockcolor == 2 && angle == 0 && (j + 3) < 18 && (i + 3) < 10) {
	                     background[i + 2][j + 1].setBackground(colorList.get(2));
	                     background[i + 3][j].setBackground(new Color(50, 50, 50));
	                     block[i + 2][j + 1] = BLOCK_MOVE;
	                     block[i + 3][j] = BLOCK_NONE;
	                     background[i + 2][j + 2].setBackground(colorList.get(2));
	                     background[i + 1][j].setBackground(new Color(50, 50, 50));
	                     block[i + 2][j + 2] = BLOCK_MOVE;
	                     block[i + 1][j] = BLOCK_NONE;
	                     background[i + 2][j + 3].setBackground(colorList.get(2));
	                     background[i][j].setBackground(new Color(50, 50, 50));
	                     block[i + 2][j + 3] = BLOCK_MOVE;
	                     block[i][j] = BLOCK_NONE;
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 2 && angle == 1 && (j + 3) < 18) {
	                     if(i ==0) {
	                        background[i + 1][j].setBackground(colorList.get(2));
	                        block[i + 1][j] = BLOCK_MOVE;
	                        background[i + 2][j].setBackground(colorList.get(2));
	                        block[i + 2][j] = BLOCK_MOVE;
	                        background[i + 3][j].setBackground(colorList.get(2));
	                        block[i + 3][j] = BLOCK_MOVE;
	                     }else if(i==1) {
	                        background[i - 1][j].setBackground(colorList.get(2));
	                        block[i - 1][j] = BLOCK_MOVE;
	                        background[i + 1][j].setBackground(colorList.get(2));
	                        block[i + 1][j] = BLOCK_MOVE;
	                        background[i + 2][j].setBackground(colorList.get(2));
	                        block[i + 2][j] = BLOCK_MOVE;
	                        
	                     }else if(i==9) {
	                        background[i - 1][j].setBackground(colorList.get(2));
	                        block[i - 1][j] = BLOCK_MOVE;
	                        background[i - 2][j].setBackground(colorList.get(2));
	                        block[i - 2][j] = BLOCK_MOVE;
	                        background[i -3][j].setBackground(colorList.get(2));
	                        block[i -3][j] = BLOCK_MOVE;
	                     }else {
	                        background[i - 1][j].setBackground(colorList.get(2));
	                        block[i - 1][j] = BLOCK_MOVE;
	                        background[i - 2][j].setBackground(colorList.get(2));
	                        block[i - 2][j] = BLOCK_MOVE;
	                        background[i + 1][j].setBackground(colorList.get(2));
	                        block[i + 1][j] = BLOCK_MOVE;
	                     }
	               
	                     background[i][j + 3].setBackground(new Color(50, 50, 50));
	                     block[i][j + 3] = BLOCK_NONE;
	                     background[i][j + 2].setBackground(new Color(50, 50, 50));                     
	                     block[i][j + 2] = BLOCK_NONE;         
	                     background[i][j + 1].setBackground(new Color(50, 50, 50));                     
	                     block[i][j + 1] = BLOCK_NONE;
	                     angle = 0;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }

	                  // 파란색 역 s자 완료
	                  if (blockcolor == 6 && angle == 0 && (j + 2) < 18) {
	                     if (i < 2) {
	                        background[i][j + 2].setBackground(colorList.get(6));
	                        block[i][j + 2] = BLOCK_MOVE;
	                        background[i+1][j + 2].setBackground(colorList.get(6));
	                        block[i+1][j + 2] = BLOCK_MOVE;

	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        block[i][j] = BLOCK_NONE;
	                        background[i-1][j+2].setBackground(new Color(50, 50, 50));
	                        block[i-1][j+2] = BLOCK_NONE;
	                     }else {
	                        background[i][j + 2].setBackground(colorList.get(6));
	                        background[i][j + 1].setBackground(new Color(50, 50, 50));
	                        block[i][j + 2] = BLOCK_MOVE;
	                        block[i][j + 1] = BLOCK_NONE;
	                        background[i - 2][j + 1].setBackground(colorList.get(6));
	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        block[i - 2][j + 1] = BLOCK_MOVE;
	                        block[i][j] = BLOCK_NONE;
	                     }
	                     
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 6 && angle == 1 && (j + 1) < 18 && (j - 1) >= 0 && (i + 2) < 10) {

	                     background[i + 2][j].setBackground(colorList.get(6));
	                     background[i + 2][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i + 2][j] = BLOCK_MOVE;
	                     block[i + 2][j + 1] = BLOCK_NONE;
	                     background[i + 2][j - 1].setBackground(colorList.get(6));
	                     background[i][j].setBackground(new Color(50, 50, 50));
	                     block[i + 2][j - 1] = BLOCK_MOVE;
	                     block[i][j] = BLOCK_NONE;
	                     angle = 0;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }

	                  // ㅗ모양 돌리기 완료
	                  if (blockcolor == 1 && angle == 0 && (j + 2) < 18) {
	                     if (i > 8)
	                        i = 8;
	                     background[i][j + 2].setBackground(colorList.get(1));
	                     background[i + 1][j + 1].setBackground(new Color(50, 50, 50));
	                     block[i][j + 2] = BLOCK_MOVE;
	                     block[i + 1][j + 1] = BLOCK_NONE;
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 1 && angle == 1 && (j + 1) < 18) {
	                     if (i == 9) {
	                        background[i - 2][j + 1].setBackground(colorList.get(1));
	                        block[i - 2][j + 1] = BLOCK_MOVE;
	                        background[i - 1][j + 2].setBackground(colorList.get(1));
	                        block[i - 1][j + 2] = BLOCK_MOVE;

	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        block[i][j] = BLOCK_NONE;
	                        background[i][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i][j + 2] = BLOCK_NONE;
	                     } else {
	                        background[i + 1][j + 1].setBackground(colorList.get(1));
	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        block[i + 1][j + 1] = BLOCK_MOVE;
	                        block[i][j] = BLOCK_NONE;
	                     }

	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 1 && angle == 2 && (j - 1) >= 0 && (i + 1) < 10) {

	                     background[i + 1][j - 1].setBackground(colorList.get(1));
	                     background[i][j].setBackground(new Color(50, 50, 50));
	                     block[i + 1][j - 1] = BLOCK_MOVE;
	                     block[i][j] = BLOCK_NONE;
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  if (blockcolor == 1 && angle == 3 && (j + 2) < 18) {
	                     if (i == 0) {
	                        background[i + 1][j].setBackground(colorList.get(1));
	                        block[i + 1][j] = BLOCK_MOVE;
	                        background[i + 2][j + 1].setBackground(colorList.get(1));
	                        block[i + 2][j + 1] = BLOCK_MOVE;
	                        background[i][j].setBackground(new Color(50, 50, 50));
	                        block[i][j] = BLOCK_NONE;
	                        background[i][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i][j + 2] = BLOCK_NONE;
	                     } else {
	                        background[i - 1][j + 1].setBackground(colorList.get(1));
	                        background[i][j + 2].setBackground(new Color(50, 50, 50));
	                        block[i - 1][j + 1] = BLOCK_MOVE;
	                        block[i][j + 2] = BLOCK_NONE;
	                     }
	                     angle++;
	                     System.out.println("angle값이" + angle + "로증가");
	                     break aa;
	                  }
	                  break aa;
	               }
	            }
	         }
	      }
	   }// blockup()

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == startB) {
			newShape(); // 첫 랜덤모양 발생시키기
			nextShape();// 다음 모양 알려주기
			startB.setEnabled(false);
			startB.setFocusable(false);
		}
	}

}
