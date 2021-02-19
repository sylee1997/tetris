package tetris.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tetris.bean.TetrisDTO;

public class TetrisDAO {

	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String username = "c##java";
	private String password = "bit";

	private static TetrisDAO instance;

	public TetrisDAO() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void getConnection() {
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 싱글톤====================================================================싱글톤
	public static TetrisDAO getInstance() {
		if (instance == null) {
			synchronized (TetrisDAO.class) {
				instance = new TetrisDAO();
			}
		}
		return instance;
	}

	// JOIN========================================================================JOIN
	// 아이디체크---------------------------------------------------------------------아이디체크
	public int idCheck(String id) {
		int result = 0;
		String sql = "select id from tetris";
		getConnection();

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				if (rs.getString("id").equals(id)) {
					result = 1;
					break;
				} else
					result = 0;
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	// 메일체크-----------------------------------------------------------------메일체크
	public int mailCheck(String email1, String email2) {
		int result = 0;
		String sql = "select email1, email2 from tetris";
		getConnection();

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				if (rs.getString("email1").equals(email1) && rs.getString("email2").equals(email2)) {
					result = 1;
					break;
				} else
					result = 0;
			}

		} catch (SQLException e) {
		
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	// 닉네임체크-------------------------------------------------------
	public int nickCheck(String nick) {
		int result = 0;
		String sql = "select nickname from tetris";
		getConnection();

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString("nickname").equals(nick)) {
					result = 1;
					break;
				} else
					result = 0;
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	// join > DB----------------------------------------------------------
	public void joinArticle(TetrisDTO dto) {
		String sql = "insert into tetris values(?,?,?,?,?,?,?,0,0,0,0)";
		getConnection();

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getId());
			pstmt.setString(2, dto.getPw());
			pstmt.setString(3, dto.getEmail1());
			pstmt.setString(4, dto.getEmail2());
			pstmt.setString(5, dto.getNick());
			pstmt.setInt(6, dto.getGender());
			pstmt.setString(7, dto.getBirth());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	// 삭제-------------------------------------------
//		public void deleteArticle(MafiaJoinDTO dto) {
//			String sql = "delete mafia where seq=?";
//			getConnection();
	//
//			try {
//				pstmt = conn.prepareStatement(sql);
//				pstmt.setInt(1, dto.getSeq());
//				pstmt.executeUpdate();
//			} catch (SQLException e) {
//				
//				e.printStackTrace();
//			} finally {
//				try {
//					if (pstmt != null)
//						pstmt.close();
//					if (conn != null)
//						conn.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}

	// 로그인/handle============================================================================
	public TetrisDTO loginArticle(String id, String pw) {
		String sql = "select * from tetris where id = ? and pw = ?";
		getConnection();

		TetrisDTO dto = new TetrisDTO();

		try {

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, id);
			pstmt.setString(2, pw);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				dto.setId(rs.getString("id"));
				dto.setNick(rs.getString("nickname"));
				dto.setMaxScore(rs.getInt("maxscore"));
				dto.setLogin(rs.getInt("login"));
				dto.setWin(rs.getInt("win"));
				dto.setLose(rs.getInt("lose"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return dto;
	}

	// 로그인한 대기실 목록창//============================================================================================
	public List<TetrisDTO> loginList() {
		String sql = "select * from tetris where login = ? ";
		getConnection();

		List<TetrisDTO> list = new ArrayList<TetrisDTO>();

		try {

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, 1);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				TetrisDTO dto = new TetrisDTO();

				dto.setNick(rs.getString("nickname"));
				dto.setMaxScore(rs.getInt("maxscore"));
				dto.setLogin(rs.getInt("login")); // 확인차 만듬
				dto.setWin(rs.getInt("win"));
				dto.setLose(rs.getInt("lose"));

				list.add(dto);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	// 로그인 업데이트//=====================================================================
	// login = 1, ready = 0
	public void TetrisLoginUpdate(String id) {
		String sql = "update tetris set login = ? where id = ?";
		getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, 1);
			pstmt.setString(2, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 로그아웃 업데이트//===============================================================================
	// login = 0, ready = 0
	public void TetrisLogoutUpdate(String id) {
		String sql = "update tetris set login = ? where id = ?";
		getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, 0);
			pstmt.setString(2, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// 방들어간 사람들(방 만들기를 누르든, 방 들어가기를 누르든 모두) ===============================================================================
//	public void tetrisReadyUpdate(String id) {
//		String sql = "update mafia set ready = ? where id = ?";
//		getConnection();
//		try {
//			pstmt = conn.prepareStatement(sql);
//			pstmt.setInt(1, 1);
//			pstmt.setString(2, id);
//			pstmt.executeUpdate();
//		} catch (SQLException e) {
//			
//			e.printStackTrace();
//		} finally {
//			try {
//				if (pstmt != null)
//					pstmt.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	// 게임 끝난 사람들 ===============================================================================
//		public void tetrisOutUpdate(String id) {
//			String sql = "update mafia set ready = ? where id = ?";
//			getConnection();
//			try {
//				pstmt = conn.prepareStatement(sql);
//				pstmt.setInt(1, 0);
//				pstmt.setString(2, id);
//				pstmt.executeUpdate();
//			} catch (SQLException e) {
//				
//				e.printStackTrace();
//			} finally {
//				try {
//					if (pstmt != null)
//						pstmt.close();
//					if (conn != null)
//						conn.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		
		
	//tetrisRoom ===================================================================================================
		//방 번호 seq 번호 생성
		public int getSeq() {
			int seq = 0;
			String sql = "select seq.nextval from dual";
			getConnection();

			try {
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();// 실행

				// 시퀀스 하나밖에 없으니까..
				rs.next();
				seq = rs.getInt(1);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					// 생성을 했을때만 닫아라.
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			System.out.println("seq = " + seq);
			return seq;
		}
		
		
		//방 만들기
		public void roomMakeArticle(int seq, String name) {
			String sql = "insert into tetrisroom values(?,?,?,?)";
			getConnection();

			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, seq);
				pstmt.setString(2, name);
				pstmt.setString(3, "1/2");
				pstmt.setString(4, "대기중");
				pstmt.executeUpdate();
			} catch (SQLException e) {
				
				e.printStackTrace();
			} finally {
				try {
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		
		
		
		//방 리스트
		public List<TetrisDTO> roomList() {
			String sql = "select * from tetrisroom order by seq asc"; // seq로 오름차순으로 정렬
			getConnection();

			List<TetrisDTO> list = new ArrayList<TetrisDTO>();

			try {

				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					TetrisDTO dto = new TetrisDTO();

					dto.setSeq(rs.getInt("seq"));
					dto.setRoomName(rs.getString("roomname"));
					dto.setRoomPeople(rs.getString("roompeople"));
					dto.setRoomState(rs.getString("roomState"));

					list.add(dto);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			return list;
		}
		
		
		//게임 종료 후 방장이 방을 나갔을 때.
		public void roomoutDelete(String roomName) {
		String sql = "delete tetrisroom where roomName = ?";
		getConnection();

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, roomName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
		
		
		
		
		
		
		//방 들어가기 버튼 눌렀을 때
		public void tetrisroomUpdate() {
			String sql = "update tetrisroom set roomPeople = ?, roomState = ?";
			getConnection();
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "2/2");
				pstmt.setString(2, "게임중");
				pstmt.executeUpdate();
			} catch (SQLException e) {
				
				e.printStackTrace();
			} finally {
				try {
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		//방장이 아닌 유저가 방 나갔을 때 업데이트
		public void useroutUpdate() {
			String sql = "update tetrisroom set roomPeople = ?, roomState = ?";
			getConnection();
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "1/2");
				pstmt.setString(2, "대기중");
				pstmt.executeUpdate();
			} catch (SQLException e) {

				e.printStackTrace();
			} finally {
				try {
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		

	

}
