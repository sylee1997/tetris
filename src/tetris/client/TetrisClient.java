package tetris.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

import tetris.bean.Info;
import tetris.bean.TetrisDTO;
import tetris.dao.TetrisDAO;

public class TetrisClient extends JFrame implements ActionListener, Runnable {
//[컨테이너, 컨포넌트]============================================================================================
	private JButton loadsendB, makeRoomB, enterRoomB, backB, exitB;
	private JPanel loadP, loadSouthP, loadEastP, loadCenterP, loadChatP, loadSendP;
	DefaultListModel<TetrisDTO> model = new DefaultListModel<TetrisDTO>();// 대기유저리스트 관리 모델
	private JList<TetrisDTO> loadList;// 대기하는 유저리스트
	private JTextField loadT; // 로비채팅입력창
	private JTextArea loadTA; // 로비채팅창
	private JTable loadRoom;
	private Font font = new Font("HY울릉도M", Font.PLAIN, 18); // 폰트
	private DefaultTableModel RoomModel; // 대기방 테이블
	private Vector<String> vector;

//[게임방]========================================================================================================
	private String roomName; // 방 이름
	private Boolean pc = false; // 패널 체인지 유무
	private Boolean master = false; // 방장
	private TetrisGameRoom gameRoom;

//[서버]========================================================================================================
	private ObjectOutputStream oos; // 넣어주는거!
	private ObjectInputStream ois; // 가져오는거!
	private Socket socket; // 클라이언트 연결소켓
	private String serverIP = "192.168.0.103"; // 서버아이피

//[로그인 정보, dao]===================================================================================================
	private String id;
	private String pw;
	private TetrisDAO dao = TetrisDAO.getInstance();
	private String nickname;

	public TetrisClient(String id, String pw) {
		// 로그인 화면에서 넘어온 아이디,패스워드
		this.id = id;
		this.pw = pw;

		setFont(new FontUIResource(new Font("HY울릉도M", 0, 20)));

		gameRoom = new TetrisGameRoom(socket, this);

		// 대기방loadP
		loadP = new JPanel(new BorderLayout(3, 3));
		loadP.setBounds(0, 0, this.getWidth(), this.getHeight());
		loadP.setBackground(Color.BLACK);

		// 밑창 loadSouthP
		loadSouthP = new JPanel(new BorderLayout());
		backB = new JButton("뒤로가기");
		backB.setFont(font);
		backB.setBackground(Color.DARK_GRAY);
		backB.setForeground(Color.LIGHT_GRAY);
		backB.setBorderPainted(false);
		backB.setFocusPainted(false);
		backB.setCursor(new Cursor(Cursor.HAND_CURSOR));

		exitB = new JButton("나가기");
		exitB.setBackground(Color.DARK_GRAY);
		exitB.setFont(font);
		exitB.setForeground(Color.LIGHT_GRAY);
		exitB.setBorderPainted(false);
		exitB.setFocusPainted(false); // 포커스 없애기
		exitB.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loadSouthP.setBackground(Color.DARK_GRAY);

		loadSouthP.add("West", backB);
		loadSouthP.add("East", exitB);
		loadP.add("South", loadSouthP);

		// 대기하는 유저리스트
		loadEastP = new JPanel(new BorderLayout());

		loadList = new JList<TetrisDTO>(model);
//		loadList.setBackground(Color.CYAN);
//		loadList.setForeground(Color.WHITE);

//		loadList.setSelectionForeground(Color.RED);
		loadList.setFont(new Font("HY울릉도M", Font.BOLD, 14));
		loadList.setSelectionBackground(Color.PINK);
		JScrollPane scroll = new JScrollPane(loadList);

		// 방만들기, 방 들어가기, 방 나가기
		JPanel roomBP = new JPanel(new GridLayout(2, 1));
		makeRoomB = new JButton("방만들기");
		makeRoomB.setFont(new Font("HY울릉도M", Font.BOLD, 20));
		makeRoomB.setForeground(Color.BLACK);
		makeRoomB.setBackground(Color.WHITE);
		makeRoomB.setFocusPainted(false); // 포커스 없애기
		makeRoomB.setCursor(new Cursor(Cursor.HAND_CURSOR));

		enterRoomB = new JButton("방들어가기");
		enterRoomB.setFont(new Font("HY울릉도M", Font.BOLD, 20));
		makeRoomB.setForeground(Color.BLACK);
		enterRoomB.setBackground(Color.WHITE);
		enterRoomB.setFocusPainted(false); // 포커스 없애기
		enterRoomB.setCursor(new Cursor(Cursor.HAND_CURSOR));
		enterRoomB.setEnabled(false);

		roomBP.add(makeRoomB);
		roomBP.add(enterRoomB);

		loadEastP.add(scroll);
		loadEastP.add("South", roomBP);
		loadP.add("East", loadEastP);

		// 방 테이블(canter)+로비채팅방(north) 패널
		loadCenterP = new JPanel(new GridLayout(2, 1, 3, 3));
		loadCenterP.setBackground(Color.BLACK);
		// 방테이블 타이틀
		vector = new Vector<String>();
		vector.addElement("방 번호");
		vector.addElement("방 제목");
		vector.add("방 인원 수");
		vector.add("게임상태");

		RoomModel = new DefaultTableModel(vector, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}

		};

		List<TetrisDTO> list = loginData();
		for (TetrisDTO a : dao.roomList()) {
			Vector<String> v = new Vector<String>();
			v.add("" + a.getSeq());
			v.add(a.getRoomName());
			v.add(a.getRoomPeople());
			v.add(a.getRoomState());

			RoomModel.addRow(v);// 행 단위로 붙이겠다.

		}

		loadRoom = new JTable(RoomModel);
		loadRoom.setBackground(Color.LIGHT_GRAY);
		loadRoom.setFont(new Font("HY울릉도M", Font.PLAIN, 18));
		loadRoom.setRowHeight(50);
		loadRoom.getColumnModel().getColumn(0).setPreferredWidth(50);
		loadRoom.getColumnModel().getColumn(1).setPreferredWidth(400);
		loadRoom.getColumnModel().getColumn(2).setPreferredWidth(100);
		loadRoom.getTableHeader().setReorderingAllowed(false);

		JScrollPane scroll2 = new JScrollPane(loadRoom);
//		scroll2.setBackground(Color.CYAN);

		// 로비채팅패널
		loadChatP = new JPanel(new BorderLayout());
		loadTA = new JTextArea();
		loadTA.setBackground(new Color(176, 226, 255));
		loadTA.setForeground(new Color(50, 50, 50));
		loadTA.setFont(new Font("HY울릉도M", Font.BOLD, 20));
		loadTA.setEditable(false);

		loadSendP = new JPanel(new BorderLayout());

		loadsendB = new JButton("보내기");
		loadsendB.setFont(font);
		loadsendB.setBackground(Color.DARK_GRAY);
		loadsendB.setForeground(Color.WHITE);
		loadSendP.add("East", loadsendB);
		loadsendB.setFocusPainted(false);
		loadT = new JTextField();
		loadSendP.add(loadT);

		loadChatP.add("South", loadSendP);
		loadChatP.add(loadTA);

		loadCenterP.add("Center", scroll2);
		loadCenterP.add("South", loadChatP);

		loadP.add(loadCenterP);

		Container c = this.getContentPane();
		c.add(loadP);

		setBounds(400, 100, 1000, 700);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle("TETRIS");
		ImageIcon img = new ImageIcon("image/테트리스아이콘.png");
		this.setIconImage(img.getImage());
		setVisible(true);

	}// 생성자

	// [유저
	// 리스트]==============================================================================
	public List<TetrisDTO> loginData() {
		List<TetrisDTO> dtoList = dao.loginList();
		return dtoList;
	}

	// [이벤트
	// 메소드]==============================================================================
	public void event() {
		loadsendB.addActionListener(this);
		loadT.addActionListener(this);
		backB.addActionListener(this);
		exitB.addActionListener(this);
		makeRoomB.addActionListener(this);
		enterRoomB.addActionListener(this);

		// X버튼 누를때
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				TetrisDTO dto = null;
				if (pc == false) {
					dao.TetrisLogoutUpdate(TetrisClient.this.id);
					List<TetrisDTO> ss = loginData(); // 로그인목록 다시 불러오기

					dto = new TetrisDTO(); // 서버로 보낼 dto
					dto.setDtoList(ss); // 로그인 목록 보내기
					dto.setCommand(Info.EXIT);
				}

				else {
					if (master) {// 방장이 방을 나갔다면, 방 삭제
						dto = new TetrisDTO();
						dto.setRoomName(roomName);
						dao.roomoutDelete(roomName);
						List<TetrisDTO> ss = dao.roomList(); // 룸 레코드 삭제 하고 다시 리스트 불러오기.
						dto.setDtoList(ss);
						dto.setCommand(Info.MASTEROUTROOM);
						master = false;
					}

					else {// 그게 아니면 방은 그대로.
							// 나만 나가기
						getContentPane().removeAll();
						getContentPane().add(loadP, BorderLayout.CENTER);
						TetrisClient.this.setContentPane(getContentPane());

						dao.useroutUpdate();
						List<TetrisDTO> ss = dao.roomList();

						dto = new TetrisDTO();
						dto.setCommand(Info.OUTROOM);
						dto.setDtoList(ss);

						pc = false;

					}

				}
				// 서버로 보내기.
				try {
					oos.writeObject(dto);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		});

		loadRoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = loadRoom.getSelectedRow(); // 선택한 행의 번호
				int col = loadRoom.getSelectedColumn();// 선택한 열의 번호

				// 잘되는지 확인
				roomName = (String) loadRoom.getModel().getValueAt(row, 1);
				for (int i = 0; i < loadRoom.getColumnCount(); i++) {
					System.out.print(loadRoom.getModel().getValueAt(row, 1) + "\t");
				}

				// 클릭했을 때 버튼을 누를 수 있게끔.
				enterRoomB.setEnabled(true);

			}
		});

	}

	// [이벤트
	// 걸어주기]==============================================================================
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == backB) {
			new TetrisMain();
			dispose();
		}

		else if (e.getSource() == exitB) {
			dao.TetrisLogoutUpdate(this.id); // login => 0
			List<TetrisDTO> ss = loginData(); // 로그인목록 다시 불러오기

			TetrisDTO dto = new TetrisDTO(); // 서버로 보낼 dto
			dto.setDtoList(ss); // 로그인 목록 보내기
			dto.setCommand(Info.EXIT);
			// 서버로 보내기.
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

		// 보내기 버튼을 누르면 채팅 멘트와 commend 서버로 전달
		else if (e.getSource() == loadsendB || e.getSource() == loadT) {
			TetrisDTO data = new TetrisDTO();
			String chat = loadT.getText();
			data.setMessage(chat);
			data.setCommand(Info.LOADSEND);

			try {
				oos.writeObject(data);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			loadT.setText("");

		}

		// 방만들기 버튼 눌렀을 때
		else if (e.getSource() == makeRoomB) {
			String[] gamemode = { "1인용", "2인용" };
			String result = (String) JOptionPane.showInputDialog(this, "게임모드를 선택해주세요", "게임모드",
					JOptionPane.QUESTION_MESSAGE, new ImageIcon("image/테트리스아이콘.png"), gamemode, gamemode[0]);
			if (result.equals("1인용")) {
				new TetrisP2(nickname);
				dispose();
			} else {
				String roomName = JOptionPane.showInputDialog(this, "방 제목을 입력해주세요", "방 제목").trim();
				if (roomName.length() == 0) {
					JOptionPane.showMessageDialog(this, "방 제목을 입력하지 않으셨습니다", "에러", JOptionPane.ERROR_MESSAGE);
				}

				// 방 만들기~
				int seq = dao.getSeq();
				dao.roomMakeArticle(seq, roomName);
				List<TetrisDTO> list = dao.roomList();

				// 방정보 서버로 보내기~
				TetrisDTO data = new TetrisDTO();
				data.setCommand(Info.MAKEROOM);
				data.setNick(nickname); // 방장 닉네임 보내기
				data.setDtoList(list);

				// 패널 변환, 방장
				pc = true;
				master = true;

				// 방패널로 바뀌는 것.
				getContentPane().removeAll();
				gameRoom.setNick(nickname); // 닉네임 나오게끔
				gameRoom.setRoomName(roomName);
				getContentPane().add(gameRoom, BorderLayout.CENTER);
				this.setContentPane(getContentPane());
				try {
					oos.writeObject(data);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}

		// 방들어가기 버튼 눌렀을 때
		else if (e.getSource() == enterRoomB) {
			int result = JOptionPane.showConfirmDialog(this, "[" + roomName + "]" + "으로 들어가시겠습니까?", "TETRIS",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			TetrisDTO data = null;
			if (result == JOptionPane.YES_OPTION) {
				// 방 리스트 목록 보내주고,
				dao.tetrisroomUpdate(); // 2/2으로 바꾸기
				List<TetrisDTO> list = dao.roomList();

				data = new TetrisDTO();
				data.setCommand(Info.ENTERROOM);
				data.setDtoList(list);

				try {
					oos.writeObject(data);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// 선택한 방으로 들어가기.
				getContentPane().removeAll();
				gameRoom.setRoomName(roomName);
				gameRoom.setNick(nickname); // 닉네임 나오게끔
				getContentPane().add(gameRoom, BorderLayout.CENTER);
				this.setContentPane(getContentPane());

				// 패널변환
				pc = true;
			}

		}

	}

	// [서버
	// 메소드]==============================================================================
	public void service() {
		try {
			socket = new Socket(serverIP, 9500);

			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			gameRoom.setOos(oos);

		} catch (UnknownHostException e) {
			System.out.println("서버를 찾을 수 없습니다.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		gameRoom.setOos(oos);

		// 서버에 접속했음을 보내주고, 유저정보랑 리스트 보내주기.
		TetrisDTO dto = new TetrisDTO();

		dao.TetrisLoginUpdate(this.id); // db 로그인 1 올려주기

		List<TetrisDTO> dtoList = loginData();
		System.out.println(dtoList);
		dto.setDtoList(dtoList); // db에 1올라간거 넣어주기.
		nickname = dao.loginArticle(id, pw).getNick();

		dto.setNick(nickname);
		dto.setCommand(Info.LOGIN);

		// 서버로 보내기.
		try {
			oos.writeObject(dto);
			oos.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 서버스레드 시작.
		Thread t = new Thread(this);
		t.start();
//		Thread t2 = new Thread(gameRoom);
//		t2.start();

	}

	// 서버에서 받는 역할
	@Override
	public void run() {
		TetrisDTO dto = null;
		while (true) {
			try {
				dto = (TetrisDTO) ois.readObject();

				// 서버에서 전달받은 디티오가 조인일때 모델에 닉네임 올려주는 것
				if (dto.getCommand() == Info.LOGIN) {
					model.removeAllElements();

					loadTA.append(dto.getMessage() + "\n");

					for (TetrisDTO data : dto.getDtoList()) {
						model.addElement(data);
					}

				}

				else if (dto.getCommand() == Info.LOADLISTUPDATE) {

					model.removeAllElements();
					for (TetrisDTO data : dto.getDtoList()) {
						model.addElement(data);
						System.out.println(data);
					}

				}

				else if (dto.getCommand() == Info.EXIT) {

					oos.close();
					ois.close();
					socket.close();

					System.exit(0);
				}

				else if (dto.getCommand() == Info.LOADSEND) {
					loadTA.append(dto.getMessage() + "\n");

				}

				else if (dto.getCommand() == Info.MAKEROOM) {
					RoomModel.setRowCount(0);

					List<TetrisDTO> list = dto.getDtoList();
					for (TetrisDTO a : list) {
						Vector<String> v = new Vector<String>();
						v.add("" + a.getSeq());
						v.add(a.getRoomName());
						v.add(a.getRoomPeople());
						v.add(a.getRoomState());

						RoomModel.addRow(v);// 행 단위로 붙이겠다.

					}

					gameRoom.setMasterNick(dto.getNick());
//					gameRoom.getRiverProfile().setText("                         [RIVER]\n             닉네임 : "
//							+ gameRoom.getEnterNick() + "\n             점수 : " + gameRoom.getCount());

				} else if (dto.getCommand() == Info.ENTERROOM) {
					// 대기실 방리스트 업데이트해주고~
					RoomModel.setRowCount(0);

					List<TetrisDTO> list = dto.getDtoList();
					for (TetrisDTO a : list) {
						Vector<String> v = new Vector<String>();
						v.add("" + a.getSeq());
						v.add(a.getRoomName());
						v.add(a.getRoomPeople());
						v.add(a.getRoomState());

						RoomModel.addRow(v);// 행 단위로 붙이겠다.

					}

					gameRoom.getGameNotice().append(dto.getMessage());
					gameRoom.setEnterNick(dto.getNick());
					if (!master) {
						gameRoom.getRiverProfile().setText("                         [RIVER]\n             닉네임 : "
								+ gameRoom.getMasterNick() + "\n             점수 : " + gameRoom.getCount());
					} else {
						gameRoom.getRiverProfile().setText("                         [RIVER]\n             닉네임 : "
								+ gameRoom.getEnterNick() + "\n             점수 : " + gameRoom.getCount());
					}
				}

				else if (dto.getCommand() == Info.MASTEROUTROOM) {
					RoomModel.setRowCount(0);
					List<TetrisDTO> list = dto.getDtoList();
					int sw = 0; // 룸네임이 없으면 0 이다.

					// 둘다 나가게끔 하기.
					for (TetrisDTO a : list) {

						if (a.getRoomName().equals(dto.getRoomName())) {
							sw = 1;
						}

						Vector<String> v = new Vector<String>();
						v.add("" + a.getSeq());
						v.add(a.getRoomName());
						v.add(a.getRoomPeople());
						v.add(a.getRoomState());

						RoomModel.addRow(v);

					} // 문제는 seq값이 고정이라 없어지면 앞번호가 지워짐..흠..

					if (sw == 0) {
						getContentPane().removeAll();
						getContentPane().add(loadP, BorderLayout.CENTER);
						TetrisClient.this.setContentPane(getContentPane());
					}

				}

				else if (dto.getCommand() == Info.OUTROOM) {
					RoomModel.setRowCount(0);

					List<TetrisDTO> list = dto.getDtoList();
					for (TetrisDTO a : list) {
						Vector<String> v = new Vector<String>();
						v.add("" + a.getSeq());
						v.add(a.getRoomName());
						v.add(a.getRoomPeople());
						v.add(a.getRoomState());

						RoomModel.addRow(v);

					}
				}

				else if (dto.getCommand() == Info.START) {
					int[][] block = new int[10][18];
					int stop;

					block = dto.getBlock();
					stop = dto.getBlock_stop();

					TetrisP riverP = gameRoom.getRiverP();
					riverP.setBlock(block);
					riverP.setStop(stop);
					riverP.move();
					gameRoom.getP3().add(riverP);

				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
