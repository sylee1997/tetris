package tetris.client;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import tetris.bean.TetrisDTO;
import tetris.dao.TetrisDAO;

public class TetrisLogInManager extends JFrame implements ActionListener {
	private JLabel idL, pwdL;
	private JTextField idT;
	private JPasswordField pwdT;
	private JButton loginBtn, joinBtn;
	private ImageIcon icon;

//============================================================================이벤트
	public void loginEvent() {
		loginBtn.addActionListener(this); // 로그인 버튼
		joinBtn.addActionListener(this); // 회원가입 버튼
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == loginBtn) {
			loginArticle();
		} else if (e.getSource() == joinBtn) {
			new TetrisJoinManager().joinEvent();
		}
	}

//============================================================================로그인버튼
	public void loginArticle() {
		String id = idT.getText();
		String pw = pwdT.getText();

		TetrisDAO dao = TetrisDAO.getInstance();
		TetrisDTO dto = dao.loginArticle(id, pw);

		if (dto.getId() == null) {
			JOptionPane.showMessageDialog(this, "ID나 비밀번호가 일치하지 않습니다");

		} else {
			JOptionPane.showMessageDialog(this, "로그인 성공");
			TetrisClient ss = new TetrisClient(id, pw);
			ss.service();
			ss.event();
			dispose();
		}
	}

//==============================================================================생성자
	public TetrisLogInManager() {

		icon = new ImageIcon("Image/로그인배경.jpg");

		JPanel panel = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(icon.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};

		idL = new JLabel("아이디");
		pwdL = new JLabel("비밀번호");

		idT = new JTextField(15);

		pwdT = new JPasswordField(15);

		loginBtn = new JButton("로그인");
		joinBtn = new JButton("회원가입");

		JPanel idP = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		idP.add(idL);
		idP.add(idT);
		idP.setBounds(50, 60, 280, 30);
		idP.setBackground(new Color(255, 0, 0, 0));

		JPanel pwdP = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		pwdP.add(pwdL);
		pwdP.add(pwdT);
		pwdP.setBounds(38, 120, 280, 30);
		pwdP.setBackground(new Color(255, 0, 0, 0));

		JPanel buttonP = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 5));
		buttonP.add(loginBtn);
		buttonP.add(joinBtn);
		buttonP.setBounds(50, 180, 280, 40);
		buttonP.setBackground(new Color(255, 0, 0, 0));

		add(idP);
		add(pwdP);
		add(buttonP);
		add(panel);

		setTitle("로그인");
		setBounds(700, 300, 400, 300);
		setVisible(true);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

//---------------------------------------------------------------------------타이틀 왼쪽 아이콘 이미지 삽입
		try {
			setIconImage(ImageIO.read(new File("image/테트리스아이콘.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
