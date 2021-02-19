package tetris.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class TetrisP extends JPanel {
	
	private ArrayList<Color> colorList;
	private LineBorder border;
	private int x = 10;
	private int cell = 30;
	private int y = 18;
	private JPanel[][] background;
	private int[][] block;
	private int stop;

	
	public int[][] getBlock() {
		return block;
	}


	public void setBlock(int[][] block) {
		this.block = block;
	}


	public int getStop() {
		return stop;
	}


	public void setStop(int stop) {
		this.stop = stop;
	}


	public TetrisP() {
		colorList = new ArrayList<Color>();

		colorList.add(new Color(255, 255, 0)); // 노랑
		colorList.add(new Color(95, 0, 255)); // 보라
		colorList.add(new Color(0, 215, 255)); // 하늘
		colorList.add(new Color(255, 95, 0)); // 주황
		colorList.add(new Color(30, 220, 20)); // 연두
		colorList.add(new Color(255, 0, 125));// 분홍
		colorList.add(new Color(0, 0, 255));// 빨강
		border = new LineBorder(Color.BLACK,2);// 모서리 직선부분을 검은색으로 바꿔줌~

		background = new JPanel[x][y];
//		block = new int[10][18];

		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				background[i][j] = new JPanel(); // 하나하나 패널 생성.
				background[i][j].setBounds(i * cell, j * cell, cell, cell); // cell사이즈로 모든 패널에 깔아주기
				background[i][j].setBorder(border); // 라인보더로 보더처리(검정)
				background[i][j].setBackground(new Color(50, 50, 50));// 배경색 검정으로 덮어버리기.
				add(background[i][j]);
			}
		}
		
		setSize(x * cell, y * cell);
		setLayout(null);
		setVisible(true);
		
		
		
		
	}// 생성자


	public void move() {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 18; j++) {
				if(block[i][j] == stop) {
					background[i][j].setBackground(Color.LIGHT_GRAY);
				}
			}
		}
	}


	


	

}
