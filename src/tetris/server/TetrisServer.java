package tetris.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TetrisServer {
	private ServerSocket ss;
	private List<TetrisServerHandler> list;

	public TetrisServer() {
		try {
			
			ss = new ServerSocket(9500);
			System.out.println("서버 준비 완료");
			
			list = new ArrayList<TetrisServerHandler>();

			while (true) {
				Socket socket = ss.accept();
				TetrisServerHandler handler = new TetrisServerHandler(socket, list);
				handler.start();
				list.add(handler);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new TetrisServer();
	}
	

}
