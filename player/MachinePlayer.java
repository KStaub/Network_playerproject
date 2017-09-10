/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

	public final static int BLACK = 0;
	public final static int WHITE = 1;
	private Board myBoard;
	private int searchDepth;
  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
	  myBoard = new Board(color);
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
	  myBoard = new Board(color);
	  this.searchDepth = searchDepth;
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    return myBoard.bestMove();
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
	  if(!(myBoard.isLegal(m))) {
		  return false;
	  } else {
		  myBoard.makeMove(m, myBoard.getOppColor());
		  return true;
	  }
    
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
	  if(!(myBoard.isLegal(m))) {
		  return false;
	  } else {
		  myBoard.makeMove(m, myBoard.getMyColor());
		  return true;
	  }

  }

}
