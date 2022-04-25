
import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;



public class GUI implements ActionListener, MouseListener{
	
	// variables for the GUI
	JFrame board = new JFrame();
	
	// menu
	JMenuBar menuBar = new JMenuBar();
	
	JMenu control = new JMenu("Control");
	JMenuItem exit = new JMenuItem("Exit");
	
	JMenu help = new JMenu("Help");
	JMenuItem instruction = new JMenuItem("Instruction");
	
	// top message
	JPanel T = new JPanel();
	JLabel TMessage = new JLabel();
	
	// game field
	JPanel gameField = new JPanel(new GridLayout(3,3,0,0));
	Border border = new LineBorder(Color.black);
	
	JLabel[][] grids = { { new JLabel(), new JLabel(), new JLabel() },
						 { new JLabel(), new JLabel(), new JLabel() },
						 { new JLabel(), new JLabel(), new JLabel() } };
	
	// bottom part
	JPanel B = new JPanel();
	JTextField nameInput = new JTextField(30);
	JButton submit = new JButton("Submit");
	
	//==========================================
	
	// variables for the game
	int playerID;
	String playerName = null;
	boolean yourTurn = false;
	
	Point latestMove = new Point(-1, -1);
	
	public GUI(int id) {
		
		this.playerID = id;
		
		board.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		board.setTitle(String.format("Tic Tac Toe - Player %d", playerID));
		
		// menu
		menuBar.add(control);
		control.add(exit);
		exit.addActionListener(this);
		
		menuBar.add(help);
		help.add(instruction);
		instruction.addActionListener(this);
		
		board.setJMenuBar(menuBar);

		// top message
		TMessage.setText("Enter you player name...");
		T.add(TMessage);
		board.add(T, BorderLayout.NORTH);
		
		// game field
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				grids[row][col].addMouseListener(this);
				grids[row][col].setBorder(border);
				gameField.add(grids[row][col]);
			}
		}

		gameField.setBackground(Color.white);
		
		board.add(gameField, BorderLayout.CENTER);
		
		
		// bottom part
		submit.addActionListener(this);
		B.add(nameInput);
		B.add(submit);
		board.add(B, BorderLayout.SOUTH);
		
		board.setSize(500, 500);
		board.setLocationRelativeTo(null);
		board.setVisible(true);
	}
	
	public String getName() {
		return this.playerName;
	}
	
	public Point getLatestMove() {
		return this.latestMove;
	}
	
	public boolean getYourTurn() {
		return this.yourTurn;
	}	
	
	public void setYourTurn(boolean b) {
		this.yourTurn = b;
	}
	
	public void drawGrid(Point opponentMove) {
		
		this.grids[opponentMove.x][opponentMove.y].setHorizontalAlignment(SwingConstants.CENTER);
		this.grids[opponentMove.x][opponentMove.y].setVerticalAlignment(SwingConstants.CENTER);
		this.grids[opponentMove.x][opponentMove.y].setFont(new Font("Helvetica", Font.BOLD, 100));
		
		if (this.playerID == 1) {
			this.grids[opponentMove.x][opponentMove.y].setText("o");
			this.grids[opponentMove.x][opponentMove.y].setForeground(Color.red);
		}
		
		else if (this.playerID == 2) {
			this.grids[opponentMove.x][opponentMove.y].setText("x");
			this.grids[opponentMove.x][opponentMove.y].setForeground(Color.green);
		}
	}
	
	public void winMessage(boolean win) {	    
	    String message;
		
	    if (win) {
	    	message = "Congratulations. You win.";
	    }
	    
	    else {
	    	message = "You lose.";
	    }
	    
	    JOptionPane.showMessageDialog(board, message, String.format("Tic Tac Toe - %s", playerName), JOptionPane.INFORMATION_MESSAGE);
	    setYourTurn(false);
	}
	
	public void drawMessage() {
	    String message = "Draw.";
	    
	    JOptionPane.showMessageDialog(board, message, String.format("Tic Tac Toe - %s", playerName), JOptionPane.INFORMATION_MESSAGE);
	    setYourTurn(false);
	}
	
	public void disconnectedMessage() {
	    String message = "Game ends. Your opponent lefts.";
	    
	    JOptionPane.showMessageDialog(board, message, String.format("Tic Tac Toe - %s", playerName), JOptionPane.INFORMATION_MESSAGE);
	    setYourTurn(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exit) {
			board.dispatchEvent(new WindowEvent(board, WindowEvent.WINDOW_CLOSING));			
		}
		
		else if (e.getSource() == instruction) {
		    String message = "Some information about the game:\n"
		    		+ "Criteria for a valid move:\n"
		    		+ "-The move is not occupied by any mark.\n"
		    		+ "-The move is made in the player's turn.\n"
		    		+ "-The move is made within the 3 x 3 board.\n"
		    		+ "The game would continue and switch among the opposite player until it reaches either one of the following conditions:\n"
		    		+ "-Player 1 wins.\n"
		    		+ "-Player 2 wins.\n"
		    		+ "-Draw.";
		    
		    JOptionPane.showMessageDialog(board, message, "Instructions", JOptionPane.INFORMATION_MESSAGE);
		}
		
		else if (e.getSource() == submit) {
			playerName = nameInput.getText();
			TMessage.setText(String.format("WELCOME %s", playerName));
			nameInput.setEnabled(false);
			submit.setEnabled(false);
		}
		
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		if (!yourTurn) {
			;
		}
		
		else {
			JLabel isClicked = (JLabel) e.getSource();
			
			if (isClicked.getText().isEmpty()) {
				
				// drawing here
				isClicked.setHorizontalAlignment(SwingConstants.CENTER);
				isClicked.setVerticalAlignment(SwingConstants.CENTER);
				isClicked.setFont(new Font("Helvetica", Font.BOLD, 100));
				
				if (this.playerID == 1) {			
					isClicked.setText("x");
					isClicked.setForeground(Color.green);
				}
				else if (this.playerID == 2) {			
					isClicked.setText("o");
					isClicked.setForeground(Color.red);
				}
				
				for (int row = 0; row < 3; row++) {
					for (int col = 0; col < 3; col++) { 
						if (isClicked.equals(grids[row][col])) {
							this.latestMove.x = row;
							this.latestMove.y = col;
						}
					}
				}
//				System.out.println(this.latestMove);
			}
			else {
				// invalid move
				;
			}
		}
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
