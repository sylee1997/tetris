package tetris.bean;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;

public class TetrisDTO implements Serializable {

	// 로그인, 회원가입
	private String id;
	private String pw;
	private String email1;
	private String email2;
	private String nick;
	private int gender;
	private String birth;

	// 게임 대기실
	private int MaxScore;
	private int login;
	private int ready; //대기방에 들어간 사람
	private Info command;
	private String message;
	private List<TetrisDTO> dtoList;
	private int win;
	private int lose;
	
	//대기방
	private int seq;
	private String roomName;
	private String roomPeople;
	private String roomState;
	
	//상대방패널
	private int[][] block;
	private int block_stop;
	private String profile;
	

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public int[][] getBlock() {
		return block;
	}

	public void setBlock(int[][] block) {
		this.block = block;
	}

	public int getBlock_stop() {
		return block_stop;
	}

	public void setBlock_stop(int block_stop) {
		this.block_stop = block_stop;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomPeople() {
		return roomPeople;
	}

	public void setRoomPeople(String roomPeople) {
		this.roomPeople = roomPeople;
	}

	public String getRoomState() {
		return roomState;
	}

	public void setRoomState(String roomState) {
		this.roomState = roomState;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLose() {
		return lose;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public Info getCommand() {
		return command;
	}

	public void setCommand(Info command) {
		this.command = command;
	}

	public int getMaxScore() {
		return MaxScore;
	}

	public void setMaxScore(int maxScore) {
		MaxScore = maxScore;
	}

	public List<TetrisDTO> getDtoList() {
		return dtoList;
	}

	public void setDtoList(List<TetrisDTO> dtoList) {
		this.dtoList = dtoList;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getReady() {
		return ready;
	}

	public void setReady(int ready) {
		this.ready = ready;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}


	@Override
	public String toString() {

		if(ready == 1) {
			return "[게임중] " + nick + " | " + "MaxScore(" + MaxScore + ")" + " win(" + win + ")" + " lose(" + lose + ")";
		}
		
		return nick + " | " + "MaxScore(" + MaxScore + ")" + " win(" + win + ")" + " lose(" + lose + ")";
	}

}
