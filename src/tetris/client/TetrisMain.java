package tetris.client;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import tetris.client.TetrisLogInManager;


public class TetrisMain extends JFrame implements ActionListener {

	private JPanel connP;
	private JButton startB;
	
	public TetrisMain() {
		
		connP = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				Image image = new ImageIcon("image/테트리스타이틀배경.jpg").getImage();
				g.drawImage(image, 0, 0, TetrisMain.this.getWidth(), TetrisMain.this.getHeight(), null);

			}

		};

		connP.setLayout(null);

		ImageIcon button = new ImageIcon("image/startButton.png");
		startB = new JButton(button);
//		start.setBorderPainted(false);
//		start.setContentAreaFilled(false);
		startB.setBounds(400, 570, 200, 69);
		startB.setFocusPainted(false); // 포커스 없애기
		startB.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 커서가 바뀜.
		connP.add(startB);
		
		Container c = this.getContentPane();
			
		c.add(connP);
		
		startB.addActionListener(this);
		
		setBounds(400, 100, 1000, 700);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("TETRIS");
		ImageIcon img = new ImageIcon("image/테트리스아이콘.png");
		this.setIconImage(img.getImage());
		setVisible(true);
	}
	

	public static void main(String[] args) {
		new TetrisMain();
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		new TetrisLogInManager().loginEvent();
		dispose();
		
	}


}
