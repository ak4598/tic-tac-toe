
import java.awt.Point;
import java.io.*;
import java.net.*;

public class client {
	
	private int playerID;
	private String playerName;
	private boolean win;
	private boolean end;
	private Socket s;
	
	private GUI board;
	
	private String serverIP = "127.0.0.1";
	private int port = 6000;
	
	private ReadFromServer read;
	private WriteToServer write;
	
	private Point lastMove = new Point(-1,-1);
	private Point opponentMove = new Point(-1,-1);
	
	private rule r = new rule();
	
	private void connect() {
		try {
			s = new Socket(serverIP, port);
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			
			playerID = in.readInt();
			System.out.println(String.format("You are player %d", playerID));
			System.out.println("Waiting for other player(s) to connect......");
			
			read = new ReadFromServer(in);
			write = new WriteToServer(out);
			
		}
		catch (Exception e){
			System.out.println("Cannot connect to server!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private class ReadFromServer implements Runnable{
		private DataInputStream dataIn;
		
		ReadFromServer(DataInputStream in){
			this.dataIn = in;
			System.out.println(String.format("Player %d: Reading thread for server is created.", playerID));
		}

		public void waitStartMsg() {
			try {
				if (this.dataIn.readBoolean()) {
					System.out.println(String.format("Player %d: Get start message from server!", playerID));
				}
			}
			catch (Exception e) {
				System.out.println(String.format("Player %d: Start message error!", playerID));
				e.printStackTrace();
//				System.exit(0);
			}
		}
		
		@Override
		public void run() {
			try {
				while(true) {
					if (board.getYourTurn()) {
						Thread.sleep(10);
					}
					
					else {
						opponentMove.x = this.dataIn.readInt();
						opponentMove.y = this.dataIn.readInt();
						System.out.println(String.format("Player %d: Opponent's move is (%d, %d)!", playerID, opponentMove.x, opponentMove.y));
						
						board.drawGrid(opponentMove);
						
						win = this.dataIn.readBoolean();
						end = this.dataIn.readBoolean();
						
						if (win) {
							board.winMessage(!win);
							board.TMessage.setText("You lose.");
							break;
						}
						
						if (end) {
							board.drawMessage();
							board.TMessage.setText("Draw.");
							break;
						}
						
						board.setYourTurn(true);
						board.TMessage.setText("Your opponent has moved, now is your turn.");
					}
				}
				
			}
			catch (Exception e) {
				System.out.println(String.format("Player %d: Cannot get opponent move!", playerID));
				e.printStackTrace();
//				System.exit(0);
			}
			

		}
		
//		public void gameEnd() {
//			
//		}

	}
	
	private class WriteToServer implements Runnable{
		private DataOutputStream dataOut;
		
		WriteToServer(DataOutputStream out){
			this.dataOut = out;
			System.out.println(String.format("Player %d: Writing thread for server is created.", playerID));
		}

		
		public void sendName() {
			try {
				playerName = board.getName();
				while(playerName == null) {
					playerName = board.getName();
				}
				this.dataOut.writeUTF(playerName);
				this.dataOut.flush();
			}
			catch (Exception e) {
				System.out.println(String.format("Player %d: Cannot send name!", playerID));
				e.printStackTrace();
//				System.exit(0);
			}
		}
		
		@Override
		public void run() {
			try {
				while(true) {
					if(board.getYourTurn()) {
						
						Point move = board.getLatestMove();
						
						
						while(move.x == lastMove.x && move.y == lastMove.y) {
							move = board.getLatestMove();
							Thread.sleep(10);
						}
						
						board.TMessage.setText("Valid Move, wait for your opponent.");
						
						// pointer problem
						Point thisMove = new Point(move.x, move.y);
						lastMove = thisMove;
						
						
						board.setYourTurn(false);
						this.dataOut.writeInt(move.x);
						this.dataOut.writeInt(move.y);
						System.out.println(String.format("Player %d: Latest move is (%d, %d)!", playerID, move.x, move.y));
						
						win = r.checkWin(board);
						this.dataOut.writeBoolean(win);
						System.out.println(String.format("Player %d: Latest win status is %b!", playerID, win));
						
						end = r.checkEnd(board);
						this.dataOut.writeBoolean(end);
						System.out.println(String.format("Player %d: Latest end status is %b!", playerID, end));
						
						if(win) {
							board.winMessage(win);
							board.TMessage.setText("You Win.");
							break;
						}
												
						if(end) {
							board.drawMessage();
							board.TMessage.setText("Draw.");
							break;
						}
						
						}
					else {
						Thread.sleep(10);
					}
				}
				
			}
			catch (Exception e) {
				System.out.println(String.format("Player %d: Cannot send move!", playerID));
				e.printStackTrace();
//				System.exit(0);
			}
		}
	}
	
	
	public static void main(String[] args) {
		client player = new client();
		player.connect();
		player.board = new GUI(player.playerID);
		player.write.sendName();
		player.read.waitStartMsg();
		player.board.board.setTitle(String.format("Tic Tac Toe - %s", player.board.getName()));
		
		// start game
		if (player.playerID == 1) {
			player.board.setYourTurn(true);
		}
		
		else if (player.playerID == 2) {
			player.board.setYourTurn(false);
		}
		
		Thread pWriteThread = new Thread(player.write);
		Thread pReadThread = new Thread(player.read);
		
		pWriteThread.start();
		pReadThread.start();
		
	}
}



