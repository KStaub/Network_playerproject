/* Chip.java */

package player;

/**
 * The Chip class provides an instance of every piece on the Board.
 */

public class Chip {

	public int color;
	public int x;
	public int y;
	public boolean visited;

	public Chip() {
		
	}
	
	public Chip(int cColor, int cX1, int cY1) {
		color = cColor;
		x = cX1;
		y = cY1;
	}

	/**
	 * toString() is used in the Board.printBoard() method for debugging
	 * purposes.
	 */
	public String toString() {
		if (color == Board.WHITE) {
			return "W";
		} else if (color == Board.BLACK) {
			return "B";
		} else {
			return " ";
		}
	}
	
	public int getColor() {
		return this.color;
	}

}