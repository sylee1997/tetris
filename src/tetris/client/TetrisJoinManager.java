package tetris.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import tetris.bean.TetrisDTO;
import tetris.dao.TetrisDAO;

public class TetrisJoinManager extends JFrame implements ActionListener {
	private JLabel idL, pwdL, repwdL, nickL, emailL, atL, genderL, birthL, birthexL;
	private JTextField idT, nickT, email1T, birthT, emailAuthT;
	private JPasswordField pwdT, repwdT;
	private JComboBox<String> email2;
	private JRadioButton maleBtn, femaleBtn, unselectBtn;
	private JButton idoverBtn, nickoverBtn, mailoverBtn, emailAuthBtn, joinBtn, cancelBtn;

	private JLabel idoverL, pwoverL, nickoverL, emailAuthL;

	private String authCode;
	
	private ImageIcon icon;

	public void joinEvent() {
		idoverBtn.addActionListener(this);
		mailoverBtn.addActionListener(this);
		emailAuthBtn.addActionListener(this);
		nickoverBtn.addActionListener(this);
		joinBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == idoverBtn) {
			idCheck();
		} else if (e.getSource() == mailoverBtn) {
			mailCheck();
		} else if (e.getSource() == emailAuthBtn) {
			authCheck();
		} else if (e.getSource() == nickoverBtn) {
			nickCheck();
		} else if (e.getSource() == joinBtn) {
			blankCheck();
		} else if (e.getSource() == cancelBtn) {
			dispose();
		}
	}

//    아이디 중복체크 메소드
	public void idCheck() {
		String id = idT.getText();
		TetrisDAO dao = TetrisDAO.getInstance();
		if (idT == null || idT.getText().length() == 0) {
			idoverL.setText("아이디를 입력해주세요.");
			return;
		}
		int result = dao.idCheck(id);
		if (result == 0) {
			idoverL.setText("사용가능한 아이디입니다.");
			idT.setEnabled(false);
			idoverBtn.setEnabled(false);
		} else {
			idoverL.setText("이미 존재하는 아이디입니다.");
			idT.setText("");
			return;
		}
	}

//    이메일 인증번호 발송버튼 메소드 이메일 중복체크 후 이메일발송
	public void mailCheck() {
		String email1 = email1T.getText();
		String email2 = this.email2.getSelectedItem().toString();
		if (email1 == null || email1.length() == 0) {
			JOptionPane.showMessageDialog(this, "이메일을 확인해주세요.");
			return;
		}
		if (email2.equals("선택")) {
			JOptionPane.showMessageDialog(this, "이메일을 확인해주세요.");
			return;
		}

		TetrisDAO dao = TetrisDAO.getInstance();
		int result = dao.mailCheck(email1, email2);

		if (result == 0) {
			mailoverBtn.setEnabled(false);
			sendEmail();
		} else {
			JOptionPane.showMessageDialog(this, "이미 가입 된 이메일입니다.");
			email1T.setText("");
			return;
		}

	}

//   이메일인증번호 확인버튼
	public void authCheck() {
		if (mailoverBtn.isSelected()) {
			JOptionPane.showMessageDialog(this, "이메일 인증버튼을 눌러주세요.");
			return;
		} else {
			if (emailAuthT.getText().equals(authCode)) {
				emailAuthT.setText("인증완료");
				emailAuthBtn.setEnabled(false);
				emailAuthT.setEnabled(false);
			} else if (emailAuthT.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "인증번호를 입력해주세요");
				return;
			} else {
				JOptionPane.showMessageDialog(this, "인증번호가 일치하지 않습니다. 다시입력해주세요.");
				return;
			}
		}

	}

//    닉네임 중복체크 메소드
	public void nickCheck() {
		String nick = nickT.getText();
		if (nickT == null || nickT.getText().length() == 0) {
			nickoverL.setText("이메일을 입력해주세요.");
			return;
		}

		TetrisDAO dao = TetrisDAO.getInstance();
		int result = dao.nickCheck(nick);

//      idoverL, pwoverL, nickoverL, emailAuthL
		if (result == 0) {
			nickoverL.setText("사용가능한 아이디입니다.");
			nickT.setEnabled(false);
			nickoverBtn.setEnabled(false);
		} else {
			nickoverL.setText("이미 존재하는 아이디입니다.");
			nickT.setText("");
			return;
		}

	}

	// DB에 새 유저 정보를 기록하는 메소드
	public void joinArticle() {
		String id = idT.getText();
		String pw = pwdT.getText();
		String email1 = email1T.getText();
		String email2 = this.email2.getSelectedItem().toString();
		String nick = nickT.getText();
		int gender = 0;
		if (maleBtn.isSelected())
			gender = 0;
		else if (femaleBtn.isSelected())
			gender = 1;
		String birth = birthT.getText();

		// TetrisDTO
		TetrisDTO dto = new TetrisDTO();
		dto.setId(id);
		dto.setPw(pw);
		dto.setEmail1(email1);
		dto.setEmail2(email2);
		dto.setNick(nick);
		dto.setGender(gender);
		dto.setBirth(birth);

		// DB
		TetrisDAO dao = TetrisDAO.getInstance();

		dao.joinArticle(dto);

		JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.");
		dispose();

	}

	// 필수요소를 다 채웠는지 검사하는 메소드
	public void blankCheck() {
		if (idT == null || idT.getText().length() == 0) {
			idCheck();
		} else if (!pwoverL.getText().equals("일치합니다.")) {
			JOptionPane.showMessageDialog(this, "비밀번호를 확인해주세요!");
		} else if (email1T == null || email1T.getText().length() == 0) {
			mailCheck();
		} else if (!emailAuthT.getText().equals("인증완료")) {
			authCheck();
		} else if (nickT == null || nickT.getText().length() == 0) {
			nickCheck();
		} else {
			joinArticle();
		}
	}

	// 입력창을 초기화하는 메소드
	public void clear() {
		idT.setText("");
		pwdT.setText("");
		repwdT.setText("");
		email1T.setText("");
		nickT.setText("");
		maleBtn.setSelected(true);
		birthT.setText("");
	}

//   난수발생메소드
	public String randomCode() {
		int random = (int) (Math.random() * 10000) + 1000;
		authCode = String.valueOf(random);
		return authCode;
	}

//   이메일보내는 메소드
	public void sendEmail() {
		randomCode();
		String toEmail = email1T.getText() + "@" + email2.getSelectedItem().toString();
		String fromEmail = "totototoga3333@gmail.com";
		String password = "cldl4120";
		String content = "안녕하세요. 인증번호는" + authCode + "입니다";
		try {
			Properties pro = new Properties();
			pro.put("mail.smtp.host", "smtp.gmail.com");
			pro.put("mail.smtp.port", "465");
			pro.put("mail.smtp.auth", "true");
			pro.put("mail.smtp.starttls", "true");
			pro.put("mail.transport.protocol", "smtp");
			pro.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

			Session sess = Session.getDefaultInstance(pro, new javax.mail.Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromEmail, password);
				}
			});

//         메세지내용
			MimeMessage msg = new MimeMessage(sess);
			msg.setHeader("content-type", "text/plain;charset=utf-8");

//         보낼 사람
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

//         보낼제목
			msg.setSubject("환영합니다! 회원가입 인증코드 메일입니다.");

//         보낼내용
			msg.setContent(content, "text/plain;charset=utf-8");
			;

//         전송
			Transport.send(msg);
			System.out.println("전송 성공...");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

//   생성자----------------------------------------------------------------------------------------------------
	public TetrisJoinManager() {
		
		icon = new ImageIcon("Image/회원가입배경.jpg");

		JPanel panel = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(icon.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
//      회원가입-----------------------------------------------------------------------------------------------
		idL = new JLabel("아이디*");
		idoverL = new JLabel("");
		pwdL = new JLabel("비밀번호*");
		repwdL = new JLabel("비밀번호 확인*");
		pwoverL = new JLabel("");
		nickL = new JLabel("닉네임*");
		nickoverL = new JLabel("");
		emailL = new JLabel("   이메일*      ");
		atL = new JLabel("@");
		emailAuthL = new JLabel("이메일 인증*");
		genderL = new JLabel("성별");
		birthL = new JLabel("생년월일 ");
		birthexL = new JLabel("예시: 19910101");

//		idL.setForeground(Color.white);
//		pwdL.setForeground(Color.white);
//		repwdL.setForeground(Color.white);
//		nickL.setForeground(Color.white);
//		emailL.setForeground(Color.white);
//		emailAuthL.setForeground(Color.white);
//		atL.setForeground(Color.white);
//		genderL.setForeground(Color.white);
//		birthL.setForeground(Color.white);
//		birthexL.setForeground(Color.white);

//      텍스트필드
		idT = new JTextField(15);
		pwdT = new JPasswordField(15);
		repwdT = new JPasswordField(15);
//      비밀번호 확인---------------------------------------------------------------------
		pwdT.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (repwdT.getText().length() > pwdT.getText().length()) {
					pwoverL.setText("일치하지 않습니다.");
				} else if (repwdT.getText().length() == pwdT.getText().length()) {
					if (repwdT.getText().length() == 0)
						pwoverL.setText("");
					else {
						int pw = 0;
						for (int i = 0; i < pwdT.getText().length(); i++) {
							if (pwdT.getText().charAt(i) == repwdT.getText().charAt(i))
								pw++;
						}
						if (pw == pwdT.getText().length())
							pwoverL.setText("일치합니다.");
						else
							pwoverL.setText("일치하지 않습니다.");
					}
				} else if (repwdT.getText().length() < pwdT.getText().length()) {
					pwoverL.setText("");
				}
			}
		});
		repwdT.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (repwdT.getText().length() != pwdT.getText().length()) {
					pwoverL.setText("일치하지 않습니다.");
				} else {
					if (repwdT.getText().length() == 0)
						pwoverL.setText("");
					else {
						int pw = 0;
						for (int i = 0; i < pwdT.getText().length(); i++) {
							if (pwdT.getText().charAt(i) == repwdT.getText().charAt(i))
								pw++;
						}
						if (pw == pwdT.getText().length())
							pwoverL.setText("일치합니다.");
						else
							pwoverL.setText("일치하지 않습니다.");
					}
				}
			}
		});
//---------------------------------------------------------------------------비밀번호 확인끝
		nickT = new JTextField(15);
		email1T = new JTextField(7);
		emailAuthT = new JTextField(15);
		emailAuthT.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (emailAuthT.getText().length() > 3) {
					e.consume();
				}
			}
		});
		birthT = new JTextField(15);

//      메일콤보박스
		String[] mail = { "선택", "gmail.com", "naver.com", "daum.net", "nate.com" };
		email2 = new JComboBox<String>(mail);

//      성별선택 라디오버튼
		maleBtn = new JRadioButton("남성");
		femaleBtn = new JRadioButton("여성");
		unselectBtn = new JRadioButton("선택안함");
//      성별그룹
		ButtonGroup bg = new ButtonGroup();
		bg.add(maleBtn);
		bg.add(femaleBtn);
		bg.add(unselectBtn);
		unselectBtn.setSelected(true);// 선택안함 기본으로
		maleBtn.setBackground(Color.white);//배경색 투명으로 지정하면 라디오버튼 뒤로 인증하기 버튼겹침
		femaleBtn.setBackground(Color.white);
		unselectBtn.setBackground(Color.white);
//		maleBtn.setForeground(Color.WHITE);
//		femaleBtn.setForeground(Color.WHITE);
//		unselectBtn.setForeground(Color.WHITE);

//      버튼
		idoverBtn = new JButton("중복"); // 아이디 중복검사
		nickoverBtn = new JButton("중복"); // 닉네임 중복검사
		mailoverBtn = new JButton("인증하기");
		emailAuthBtn = new JButton("확인"); // 이메일 인증번호받기, 중복검사 후 중복x 인증
		joinBtn = new JButton("가입");
		cancelBtn = new JButton("취소");

//      id패널
		JPanel idP = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		idP.add(idL);
		idP.add(idT);
		idP.add(idoverBtn);
		idP.setBounds(62, 40, 400, 35);
//		idP.setBackground(Color.WHITE);
		idP.setBackground(new Color(255,0,0,0));//투명으로 배경 색상 지정

		idoverL.setBounds(150, 70, 400, 20);
		idoverL.setForeground(Color.RED);

//      pw패널
		JPanel pwP = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		pwP.add(pwdL);
		pwP.add(pwdT);
		pwP.setBounds(49, 90, 400, 30);
		pwP.setBackground(new Color(255,0,0,0));

//      pw확인패널
		JPanel repwP = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		repwP.add(repwdL);
		repwP.add(repwdT);
		repwP.setBounds(20, 130, 400, 30);
		repwP.setBackground(new Color(255,0,0,0));

		pwoverL.setBounds(150, 160, 400, 20);
		pwoverL.setForeground(Color.RED);

//      email패널
		JPanel emailP = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		emailP.add(emailL);
		emailP.add(email1T);
		emailP.add(atL);
		emailP.add(email2);
		emailP.add(mailoverBtn);
		emailP.setBounds(62, 180, 400, 40);
		emailP.setBackground(new Color(255,0,0,0));

//      email인증패널
		JPanel emailAuthP = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 3));
		emailAuthP.add(emailAuthL);
		emailAuthP.add(emailAuthT);
		emailAuthP.add(emailAuthBtn);
		emailAuthP.setBounds(32, 220, 400, 40);
		emailAuthP.setBackground(new Color(255,0,0,0));

//      닉네임패널
		JPanel nickP = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		nickP.add(nickL);
		nickP.add(nickT);
		nickP.add(nickoverBtn);
		nickP.setBounds(62, 260, 400, 35);
		nickP.setBackground(new Color(255,0,0,0));

		nickoverL.setBounds(150, 290, 400, 20);
		nickoverL.setForeground(Color.RED);

//      성별 패널
		JPanel genderP = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		genderP.add(genderL);
		genderP.add(maleBtn);
		genderP.add(femaleBtn);
		genderP.add(unselectBtn);
		genderP.setBounds(75, 315, 400, 40);
		genderP.setBackground(new Color(255,0,0,0));

//      생년월일 패널
		JPanel birthP = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		birthP.add(birthL);
		birthP.add(birthT);
		birthP.add(birthexL);
		birthP.setBounds(50, 350, 400, 40);
		birthP.setBackground(new Color(255,0,0,0));

//      가입취소 버튼 패널
		JPanel buttonP = new JPanel(new FlowLayout(FlowLayout.LEFT, 70, 5));
		buttonP.add(joinBtn);
		buttonP.add(cancelBtn);
		buttonP.setBounds(80, 400, 280, 40);
		buttonP.setBackground(new Color(255,0,0,0));
		
//      회원가입 패널
//		JPanel joinP = new JPanel();
//		joinP.setLayout(null);
//		joinP.setBackground(Color.black);
//		joinP.add(idP);
//		joinP.add(idoverL);
//		joinP.add(pwP);
//		joinP.add(repwP);
//		joinP.add(pwoverL);
//		joinP.add(emailP);
//		joinP.add(emailAuthP);
//		joinP.add(nickP);
//		joinP.add(nickoverL);
//		joinP.add(genderP);
//		joinP.add(birthP);
//		joinP.add(buttonP);
//      회원가입패널--------------------------------------------

//      컨테이너
//		Container c = this.getContentPane();
//		c.add(joinP);
		
		add(idP);
		add(idoverL);
		add(pwP);
		add(repwP);
		add(pwoverL);
		add(emailP);
		add(emailAuthP);
		add(nickP);
		add(nickoverL);
		add(genderP);
		add(birthP);
		add(buttonP);
		add(panel);

		setTitle("회원가입");
		setBounds(700, 300, 500, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

//---------------------------------------------------------------------------타이틀 왼쪽 아이콘 이미지 삽입		
		try {
			setIconImage(ImageIO.read(new File("image/테트리스아이콘.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}