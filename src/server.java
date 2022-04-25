
import java.awt.Point;
import java.io.*;
import java.net.*;


public class server {
	
	private ServerSocket gameServer;
	private int port = 6000;
	
	private int numPlayers = 0;
	private int maxPlayers = 2;
	
	
	private Socket[] players = new Socket[maxPlayers];
	private ReadFromClient[] playersRead = new ReadFromClient[maxPlayers];
	private WriteToClient[] playersWrite = new WriteToClient[maxPlayers];
	
	private String[] playersList = new String[maxPlayers];
	
	private Point[] playersMove = { new Point(-1, -1), new Point(-1, -1) };
	private boolean[] movedStatus = { false, false };
	
	private boolean gameWon = false;
	private boolean gameEnd = false;
	
	public server() {
		try {
			gameServer = new ServerSocket(port);
		}
		
		catch (Exception e){
			System.out.println("Server: Server cannot start!");
			e.printStackTrace();
			System.exit(0);
			
		}
		
	}
	
	public void startServer() {
		try {
			while(numPlayers < maxPlayers) {
				Socket s = gameServer.accept();
				DataInputStream in = new DataInputStream(s.getInputStream());
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				
				numPlayers++;
				out.writeInt(numPlayers);
				System.out.println(String.format("Server: No, of player(s) connected: %d", numPlayers));
				
				ReadFromClient read = new ReadFromClient(numPlayers, in);
				WriteToClient write = new WriteToClient(numPlayers, out);
				
				players[numPlayers-1] = s;
				playersRead[numPlayers-1] = read;
				playersWrite[numPlayers-1] = write;
				
			}
			System.out.println("Server: Max connections reached, no longer accept connections!");			
			
		}
		catch (Exception e){
			System.out.println("Server: Server cannot accept client connection!");
			e.printStackTrace();
			System.exit(0);
			
		}
		
	}

	private class ReadFromClient implements Runnable{
		private int playerID;
		private DataInputStream dataIn;
		
		ReadFromClient(int id, DataInputStream in){
			this.playerID = id;
			this.dataIn = in;
			System.out.println(String.format("Server: Reading thread for player %d is created.", this.playerID));
		}


		@Override
		public void run() {
			
			try {
				while(true) {
					if(!movedStatus[playerID-1]) {
//						this.dataIn.readUTF();
						
						Point playerMove = new Point(-1, -1);
						
						playerMove.x = this.dataIn.readInt();
						playerMove.y = this.dataIn.readInt();
						
						System.out.println(String.format("Server: Receive player %d's move - (%d, %d)!", this.playerID, playerMove.x, playerMove.y));
						playersMove[playerID-1] = playerMove;
						movedStatus[playerID-1] = true;
						
						gameWon = this.dataIn.readBoolean();
						System.out.println(String.format("Server: Receive player %d's game win status - %b!", this.playerID, gameWon));
						
						gameEnd = this.dataIn.readBoolean();
						System.out.println(String.format("Server: Receive player %d's game end status - %b!", this.playerID, gameEnd));
						
						if (gameWon) {
							break;
						}
						
						if (gameEnd) {
							break;
						}
					}
					else if (movedStatus[playerID-1]) {
						Thread.sleep(10);
					}
				}
			}
			catch (Exception e) {
				System.out.println(String.format("Server: Cannot read from player %d!", this.playerID));
				e.printStackTrace();
				
				try {
					if (this.playerID == 1) {	
						players[1].close();
						System.out.println("Closed connection of player 2!");
					}
					
					else if (this.playerID == 2) {
						players[0].close();
						System.out.println("Closed connection of player 1!");
					}
				}
				catch (Exception e1) {
					System.out.println("Cannot close connection!");
				}
				
				
				System.exit(0);
			}
			
		}
		
		public void checkNames() {
			try {
				boolean nameReceived = false;
				while(!nameReceived) {
					playersList[this.playerID-1] = this.dataIn.readUTF();
					if (playersList[this.playerID-1] != null) {
						nameReceived = true;
					}
				}
				System.out.println(String.format("Server: Player %d's name is %s!", playerID, playersList[this.playerID-1]));
				
			}
			catch (Exception e) {
				System.out.println(String.format("Server: Cannot read player %d's name!", playerID));
				e.printStackTrace();
				System.exit(0);
			}
		}

	}
	
	private class WriteToClient implements Runnable{
		private int playerID;
		private DataOutputStream dataOut;
		
		WriteToClient(int id, DataOutputStream out){
			this.playerID = id;
			this.dataOut = out;
			System.out.println(String.format("Server: Writing thread for player %d is created.", this.playerID));
		}


		@Override
		public void run() {
			try {
				while (true) {				
					boolean moved;
					if (this.playerID == 1) {
						moved = movedStatus[1];
						System.out.println(String.format("Server: Player 2 moved - %b!", moved));
						while(!moved) {
							moved = movedStatus[1];
							Thread.sleep(10);
						}
						this.dataOut.writeInt(playersMove[1].x);
						this.dataOut.writeInt(playersMove[1].y);
						this.dataOut.flush();
						System.out.println(String.format("Server: Wrote player 2's move (%d, %d) to player 1!", playersMove[1].x, playersMove[1].y));
						movedStatus[1] = false;
					}
					
					else if (this.playerID == 2) {
						moved = movedStatus[0];
						System.out.println(String.format("Server: Player 1 moved - %b!", moved));
						while(!moved) {
							moved = movedStatus[0];
							Thread.sleep(10);
						}
						this.dataOut.writeInt(playersMove[0].x);
						this.dataOut.writeInt(playersMove[0].y);
						this.dataOut.flush();
						System.out.println(String.format("Server: Wrote player 1's move (%d, %d) to player 2!", playersMove[0].x, playersMove[0].y));
						movedStatus[0] = false;
					}
					
					this.dataOut.writeBoolean(gameWon);
					System.out.println(String.format("Server: Wrote game win status - %b to player %d!", gameWon, playerID));
					
					this.dataOut.writeBoolean(gameEnd);
					System.out.println(String.format("Server: Wrote game end status - %b to player %d!", gameEnd, playerID));
					
					if (gameWon) {
						System.exit(0);
					}
					
					if (gameEnd) {
						System.exit(0);
					}
					
					this.dataOut.flush();
				}

			}
			catch (Exception e) {
				System.out.println(String.format("Server: Cannot write player %d's move!", playerID));
				e.printStackTrace();
				System.exit(0);
			}
			
		}
		
		public void sendStartMsg() {
			try {
				this.dataOut.writeBoolean(true);
				this.dataOut.flush();
				System.out.println(String.format("Server: Sent start message to player %d!", this.playerID));
			}
			catch (Exception e) {
				System.out.println(String.format("Server: Cannot send start message to player %d!", this.playerID));
				e.printStackTrace();
				System.exit(0);
			}
		}
		
	}

	public static void main(String[] args) {
		
		server gameServer = new server();
		gameServer.startServer();
		
		gameServer.playersRead[0].checkNames();
		gameServer.playersRead[1].checkNames();

		gameServer.playersWrite[0].sendStartMsg();
		gameServer.playersWrite[1].sendStartMsg();
		
		
		Thread p1ReadThread = new Thread(gameServer.playersRead[0]);
		Thread p2ReadThread = new Thread(gameServer.playersRead[1]);
		
		Thread p1WriteThread = new Thread(gameServer.playersWrite[0]);
		Thread p2WriteThread = new Thread(gameServer.playersWrite[1]);
		
		
		p1ReadThread.start();
		p2ReadThread.start();
		p1WriteThread.start();
		p2WriteThread.start();

	}

}
