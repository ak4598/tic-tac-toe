
/**
 * Creates the set of rules to evaluate the result.
 * @author Kwan Man Hei
 */
public class rule {
	
    /**
     * Constructs the rules.
     */
	public rule() {
	}
	
	private boolean checkRow(GUI board) {
		for (int row = 0; row < 3; row++) {
				if (board.grids[row][0].getText().equals(board.grids[row][1].getText()) &&
					board.grids[row][0].getText().equals(board.grids[row][2].getText()) &&
					!board.grids[row][0].getText().equals("")) {
					
					return true;
				}
			}
		
		return false;
	}
	
	private boolean checkCol(GUI board) {
		for (int col = 0; col < 3; col++) {
			if (board.grids[0][col].getText().equals(board.grids[1][col].getText()) &&
				board.grids[0][col].getText().equals(board.grids[2][col].getText()) &&
				!board.grids[0][col].getText().equals("")) {
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkDia(GUI board) {
		
		if (board.grids[0][0].getText().equals(board.grids[1][1].getText()) &&
				board.grids[0][0].getText().equals(board.grids[2][2].getText()) &&
				!board.grids[0][0].getText().equals("")) {
			
				return true;
			}
				
		if (board.grids[0][2].getText().equals(board.grids[1][1].getText()) &&
				board.grids[0][2].getText().equals(board.grids[2][0].getText()) &&
				!board.grids[0][2].getText().equals("")) {
			
				return true;
			}
		
		return false;
	}
	
    /**
     * Determine the game is ended or not.
     * @param board the game board.
     * @return The end status of the game.
     */
	public boolean checkEnd(GUI board) {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (board.grids[row][col].getText().isEmpty()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
    /**
     * Determine any win condition achieved.
     * @param board the game board.
     * @return The win status of the game.
     */
	public boolean checkWin(GUI board) {
		
		// the winner must be the player at that turn		
		
		return checkRow(board) || checkCol(board) || checkDia(board);
	}
}
