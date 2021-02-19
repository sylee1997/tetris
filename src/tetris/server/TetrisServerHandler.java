package tetris.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import tetris.bean.Info;

import tetris.bean.TetrisDTO;



public class TetrisServerHandler extends Thread {
	private Socket socket;
	private List<TetrisServerHandler> list;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public TetrisServerHandler(Socket socket, List<TetrisServerHandler> list) {
		this.socket = socket;
		this.list = list;
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	//클라이언트에서 데이터 받아오기
	@Override
	public void run() {
		
		TetrisDTO data = null; //클라이언트에서 보낸 dto
		String nickname = null; // 클라이언트에서 받고 보낼 닉네임
		String chat = null;// 클라이언트에서 받고 보낼 채팅메세지
		String profile;
		
		List<TetrisDTO> list = null; //클라이언트에서 받고 보낼 list
		String roomName = null;
		int[][] block = null;
		int stop = 0;
		

		while (true) {
			try {
				data = (TetrisDTO) ois.readObject();
				
				//로그인 했을때
				if(data.getCommand() == Info.LOGIN) {
					nickname = data.getNick();
					//로그인한 사람의 목록 받아오기.
					list = data.getDtoList();
					
					//클라이언트에게 보내기
					TetrisDTO send = new TetrisDTO();
					send.setCommand(Info.LOGIN);
					send.setMessage("["+nickname+"] 님이 입장하였습니다.");
					send.setDtoList(list);
					broadcast(send);
					
				}
				
				
				//메세지 보낼때
				else if(data.getCommand() == Info.LOADSEND) {
					chat = data.getMessage();
					TetrisDTO send = new TetrisDTO();
					send.setCommand(Info.LOADSEND);
					send.setMessage("["+nickname+"] "+chat);
					broadcast(send);
				}
				
				
				//나가기 버튼 눌렀을 때 보내오는 것.
				else if(data.getCommand() == Info.EXIT) {
					
					TetrisDTO send = new TetrisDTO();
					
					send.setCommand(Info.EXIT);
					oos.writeObject(send);
					oos.flush();
					
					
					oos.close();
					ois.close();
					socket.close();
					
					this.list.remove(this);
				
					
					send.setCommand(Info.LOADLISTUPDATE);
					list = data.getDtoList();
					send.setDtoList(list);
					broadcast(send);
					
					
					
					break;
				}
				
				else if(data.getCommand() == Info.MAKEROOM) {
					list = data.getDtoList();
					nickname = data.getNick();
					TetrisDTO send = new TetrisDTO();
					
					send.setCommand(Info.MAKEROOM);
					send.setDtoList(list);
					send.setNick(nickname);
					broadcast(send);
				}
				
				else if(data.getCommand() == Info.ENTERROOM) {
					list = data.getDtoList();
					TetrisDTO send = new TetrisDTO();
					
					send.setCommand(Info.ENTERROOM);
					send.setNick(nickname);
					send.setMessage("["+nickname+"] 이 들어오셨습니다.");
					send.setDtoList(list);
					broadcast(send);
				}
				
				//방장이 방 나갔을때
				else if(data.getCommand() == Info.MASTEROUTROOM) {
					list = data.getDtoList();
					roomName = data.getRoomName(); // 만든 룸 네임 받아오고
					TetrisDTO send = new TetrisDTO();
					
					send.setCommand(Info.MASTEROUTROOM);
					send.setRoomName(roomName); //룸 네임 보내주기
					send.setDtoList(list);
					broadcast(send);
				}
				
				//방장이 아닌 유저가 방 나갔을 때
				else if(data.getCommand() == Info.OUTROOM) {
					list = data.getDtoList();
					TetrisDTO send = new TetrisDTO();
					
					send.setCommand(Info.OUTROOM);
					send.setDtoList(list);
					broadcast(send);
				}
				
				
				
				
				
				//테트리스 화면 송출
				else if(data.getCommand() == Info.START) {
					block = data.getBlock();
					stop = data.getBlock_stop();
					profile = data.getMessage();
					
					TetrisDTO send = new TetrisDTO();
					send.setBlock(block);
					send.setBlock_stop(stop);
					send.setCommand(Info.START);
					send.setMessage(profile);
					broadcast(send);
					
				}
				
				
				
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

	
	//서버로 보내는 메소드.
	//나를 포함 모든 클라이언트에게 보내는 메소드
	public void broadcast(TetrisDTO dto) {
		for (TetrisServerHandler data : list) {
			try {
				data.oos.writeObject(dto);
				data.oos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	
	
}
