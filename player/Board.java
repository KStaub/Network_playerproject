/* Board.java */

package player;

import java.util.Random;

/**
 * The Board class provides an instance of the Network game populated by Chips.
 */

public class Board {

	/* Temporary score holders */
	private final static int NETWORK = 1000;
	private final static int BLOCK = 50;
	private final static int CONNECT = 100;

	/* Direction constants */
	private final static int N = 1;
	private final static int NE = 2;
	private final static int E = 3;
	private final static int SE = 4;
	private final static int S = 5;
	private final static int SW = 6;
	private final static int W = 7;
	private final static int NW = 8;

	public final static int SIZE = 8;
	public final static int BLACK = MachinePlayer.BLACK;
	public final static int WHITE = MachinePlayer.WHITE;
	public Chip[][] board = new Chip[SIZE][SIZE];
	private int myColor;
	private int oppColor;

	private int turn; // Records how many turns myColor has taken.

	/* Used in explore */
	private int startGoal;
	private int endGoal;

	/**
	 * This constructor generates a game board with your color being the input
	 * color, and your opponents color being the opposite.
	 */
	public Board(int color) {
		myColor = color;
		if (myColor == WHITE) {
			oppColor = BLACK;
		} else {
			oppColor = WHITE;
		}
		turn = 1;
	}

	/**
	 * isLegal() returns a boolean value determining if the input Move (m) is
	 * within the rules on the current board.
	 * 
	 * @param m
	 *            The attempted move.
	 * @return true if the Move m is legal
	 */
	public boolean isLegal(Move m) {
		// CORNERS
		if ((m.x1 == 0 && m.y1 == 0) || (m.x1 == 7 && m.y1 == 0)
				|| (m.x1 == 0 && m.y1 == 7) || (m.x1 == 7 && m.y1 == 7)) {
			return false;
		}
		// WRONG GOAL
		if (myColor == BLACK && (m.x1 == 0 || m.x1 == 7)) {
			return false;
		}
		if (myColor == WHITE && (m.y1 == 0 || m.y1 == 7)) {
			return false;
		}
		// OCCUPIED
		if (board[m.x1][m.y1] != null) {
			return false;
		}
		// CLUSTER
		if (formsCluster(myColor, m.x1, m.y1)) {
			return false;
		}
		// Move.ADD AFTER 10 TURNS
		if (m.moveKind == Move.ADD && turn > 9) {
			return false;
		}
		// Move.STEP BEFORE 10 TURNS
		if (m.moveKind == Move.STEP && turn < 10) {
			return false;
		}

		return true;
	}

	/**
	 * formsCluster() is used by isLegal() and returns a boolean value depending
	 * on if a Chip at (x, y) would form a cluster with other Chips of its
	 * color.
	 * 
	 * @return true if a Chip at (x,y) will form a cluster with its color.
	 * 
	 * @param x
	 *            The x-coordinate of the new Chip.
	 * @param y
	 *            The y-coordinate of the new Chip.
	 * @param color
	 *            The color of the new Chip.
	 * 
	 */
	private boolean formsCluster(int color, int x, int y) {
		int nNumber = 0;
		nNumber += numberOfNeighbors(color, x, y);
		if (nNumber > 0) {
			for (int i = -1; i <= 1; ++i) {
				for (int j = -1; j <= 1; ++j) {
					if (i != 0 && j != 0) {
						if (inBounds(x + i, y + j)) {
							if (board[x + i][y + j].getColor() == color) {
								if (numberOfNeighbors(color, x + i, y + j) > 0) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * numberOfNeighbors() returns an integer corresponding to the number of
	 * adjacent Chips of the same color to a Chip at (x,y).
	 */
	private int numberOfNeighbors(int color, int x, int y) {
		int answer = 0;
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				if (i != 0 && j != 0) {
					if (inBounds(x + i, y + j)) {
						if (board[x + i][y + j].getColor() == color) {
							answer++;
						}
					}
				}
			}
		}
		return answer;
	}

	/**
	 * explore() walks across the board, from one chip of your color to the next
	 * in a legal network fashion. The input Chip should always be in one goal.
	 * 
	 * 
	 * @param color
	 *            The color of the Chip you're starting from.
	 * @param c
	 *            The Chip from which you start to explore. Should always be in
	 *            the goal of the color indicated.
	 * @param length
	 *            The length of the exploration so far. Should start at 1.
	 * @param direction
	 *            The direction of the last iteration of explore(). The game
	 *            requires that you change direction. The directions are N, NE,
	 *            E, SE, S, SW, W, NW.
	 * 
	 * @return true if explore is able to walk from one goal to the other in >=
	 *         6 valid Move.STEPs.
	 */
	private boolean explore(int color, Chip c, int length, int direction) {
		c.visited = true;
		Chip neighbor = new Chip();
		int currentDirection = direction;

		if (length == 1) {
			if (color == WHITE && c.x == 0) {
				startGoal = 0;
				endGoal = 7;
			} else if (color == WHITE && c.x == 7) {
				startGoal = 7;
				endGoal = 0;
			} else if (color == BLACK && c.y == 0) {
				startGoal = 0;
				endGoal = 7;
			} else if (color == BLACK && c.y == 7) {
				startGoal = 7;
				endGoal = 0;
			}
		}

		for (int i = N; i <= NW; ++i) {
			// WRONG DIRECTION
			if (i != direction) {
				neighbor = findNeighbor(direction, c);
			}
			// NO NEIGHBOR
			if (neighbor == null)
				continue;

			// NEIGHBOR NOT YOUR COLOR
			if (neighbor.color != color)
				continue;

			// NEIGHBOR IN START GOAL
			if (neighbor.color == WHITE) {
				if (neighbor.x == startGoal)
					continue;
			} else if (neighbor.color == BLACK) {
				if (neighbor.y == startGoal)
					continue;
			}

			// NEIGHBOR HAS BEEN VISITED
			if (neighbor.visited)
				continue;

			// END GOAL
			if (neighbor.color == WHITE) {
				if (neighbor.x == endGoal) {
					if (length >= 6)
						return true;
				} else {
					if (explore(color, neighbor, length++, i)) {
						return true;
					}
				}
			}
			if (neighbor.color == BLACK) {
				if (neighbor.y == endGoal) {
					if (length >= 6)
						return true;
				} else {
					if (explore(color, neighbor, length++, i)) {
						return true;
					}
				}
			}

		}
		c.visited = false;
		return false;
	}

	/**
	 * findNeighbor() returns the first Chip encountered in the direction
	 * indicated from the input Chip.
	 */
	private Chip findNeighbor(int direction, Chip startC) {
		Chip c;
		int x = startC.x;
		int y = startC.y;
		for (int i = 1; i < SIZE; ++i) {
			// NORTH
			if (direction == N) {
				if (inBounds(x, y - i)) {
					if (board[x][y - i] != null) {
						c = board[x][y - i];
						return c;
					}
				}
			}
			// NORTH EAST
			if (direction == NE) {
				if (inBounds(x + i, y - i)) {
					if (board[x + i][y - i] != null) {
						c = board[x + i][y - i];
						return c;
					}
				}
			}
			// EAST
			if (direction == E) {
				if (inBounds(x + i, y)) {
					if (board[x + i][y] != null) {
						c = board[x + i][y];
						return c;
					}
				}
			}
			// SOUTH EAST
			if (direction == SE) {
				if (inBounds(x + i, y + i)) {
					if (board[x + i][y + i] != null) {
						c = board[x + i][y + i];
						return c;
					}
				}
			}
			// SOUTH
			if (direction == S) {
				if (inBounds(x, y + i)) {
					if (board[x][y + i] != null) {
						c = board[x][y + i];
						return c;
					}
				}
			}
			// SOUTH WEST
			if (direction == SW) {
				if (inBounds(x - i, y + i)) {
					if (board[x - i][y + i] != null) {
						c = board[x - i][y + i];
						return c;
					}
				}
			}
			// WEST
			if (direction == W) {
				if (inBounds(x - i, y)) {
					if (board[x - i][y] != null) {
						c = board[x - i][y];
						return c;
					}
				}

			}
			// NORTH WEST
			if (direction == NW) {
				if (inBounds(x - i, y - i)) {
					if (board[x - i][y - i] != null) {
						c = board[x - i][y - i];
						return c;
					}
				}
			}
		}
		return null;

	}

	/**
	 * bestMove() returns the best move for the myColor player based on scores
	 * for each potential move. These scores are generated using a GameTree and
	 * the search depth indicated in the initialization of the MachinePlayer.
	 * 
	 * @return The move that is most favorable for the player.
	 */
	public Move bestMove() {
		Move m = null;
		if (turn == 1) {
			m = firstMove();
		} else {
			m = randomMove(myColor);
		}
		return m;
	}

	/**
	 * makeMove() executes the input move on the current board.
	 * 
	 * @param m
	 *            This Move is the move you're trying to make.
	 * @param color
	 *            The color of the player making the move.
	 */
	public void makeMove(Move m, int color) {
		if (isLegal(m)) {
			if (m.moveKind == Move.ADD) {
				board[m.x1][m.y1] = new Chip(color, m.x1, m.y1);
			} else if (m.moveKind == Move.STEP) {
				board[m.x1][m.y1] = new Chip(color, m.x1, m.y1);
				board[m.x2][m.y2] = null;
			}
		}
		if (color == myColor) {
			turn++;
		}
	}

	/**
	 * undoMove() removes the input move from the board.
	 */
	private void undoMove(Move m) {
		int tColor = board[m.x1][m.y1].getColor();
		if (m.moveKind == Move.ADD) {
			board[m.x1][m.y1] = null;
		} else if (m.moveKind == Move.STEP) {
			board[m.x1][m.y1] = null;
			board[m.x2][m.y2] = new Chip(tColor, m.x2, m.y2);
		}
		if (tColor == myColor) {
			turn--;
		}
	}

	/**
	 * hasNetwork() returns a boolean value if the current board has a network
	 * for the specified color.
	 */
	public boolean hasNetwork(int color) {
		resetVisit();
		boolean leftgoal = false;
		boolean rightgoal = false;
		boolean topgoal = false;
		boolean bottomgoal = false;
		for (int i = 1; i < SIZE - 1; ++i) {
			if (color == WHITE) {
				if (board[0][i] != null) {
					leftgoal = explore(WHITE, board[0][i], 1, 0);
				}
				if (board[7][i] != null) {
					rightgoal = explore(WHITE, board[7][i], 1, 0);
				}

			} else if (color == BLACK) {
				if (board[i][0] != null) {
					topgoal = explore(BLACK, board[i][0], 1, 0);
				}
				if (board[i][7] != null) {
					bottomgoal = explore(BLACK, board[i][7], 1, 0);
				}
			}
		}
		if (color == WHITE) {
			if (leftgoal) {
				return leftgoal;
			}
			if (rightgoal) {
				return rightgoal;
			}
		} else if (color == BLACK) {
			if (topgoal) {
				return topgoal;
			}
			if (bottomgoal) {
				return bottomgoal;
			}
		}
		return false;

	}

	/**
	 * numConnections() returns the number of straight line connections the
	 * input Chip has to a single other Chip.
	 */
	private int numConnections(Chip c) {
		int answer = 0;
		Chip neighbor = new Chip();
		for (int i = N; i <= NW; ++i) {
			neighbor = findNeighbor(i, c);
			if (neighbor.color == c.color) {
				answer++;
			}
		}
		return answer;
	}

	/**
	 * evaluate() generates an integer score for the input Move as it applies to
	 * the input color.
	 * 
	 * @return SCORING GUIDE
	 */
	private int evaluate(Move m, int color) {
		return 1;
	}

	/**
	 * genMoves() produces an array of moves that are valid for the given color
	 * on the current board. MAY REVISIT BASED ON SPEED.
	 */
	private Move[] genMoves(int color) {
		Move[] answer = new Move[SIZE ^ 3];
		int n = 0;

		for (int i = 0; i < SIZE; ++i) {
			for (int j = 0; j < SIZE; ++j) {
				if (board[i][j].getColor() == color) {
					// Move.ADD MOVES
					if (turn < 10) {
						Move m = new Move(i, j);
						if (isLegal(m)) {
							answer[n] = m;
							n++;
						}
					} else if (turn >= 10) { // Move.STEP MOVES
						for (int k = 0; k < SIZE; ++k) {
							for (int h = 0; h < SIZE; ++h) {
								Move m = new Move(k, h, i, j);
								if (isLegal(m)) {
									answer[n] = m;
									n++;
								}
							}
						}
					}
				}
			}
		}
		return answer;
	}

	/**
	 * firstMove() returns several options for a first move on the board. Those
	 * options are (in order of preference): WHITE: {2,3}, {2,4} BLACK: {4,2},
	 * {5,4}
	 */
	public Move firstMove() {
		Move m = null;
		if (myColor == WHITE) {
			m = new Move(2, 3);
			if (isLegal(m)) {
				return m;
			} else {
				m = new Move(2, 4);
				if (isLegal(m)) {
					return m;
				}
			}
		}
		if (myColor == BLACK) {
			m = new Move(4, 2);
			if (isLegal(m)) {
				return m;
			} else {
				m = new Move(3, 2);
				if (isLegal(m)) {
					return m;
				}
			}
		}
		return m;
	}

	/**
	 * randomMove() returns a random, legal move on the current board for the
	 * input player.
	 */
	private Move randomMove(int color) {
		Random generator = new Random();
		Move m = null;
		int r;
		int q;
		int a;
		int b;
		while (!isLegal(m)) {
			r = generator.nextInt(SIZE);
			q = generator.nextInt(SIZE);
			a = generator.nextInt(SIZE);
			b = generator.nextInt(SIZE);
			if (turn < 10) {
				m = new Move(r, q);
			} else if (turn >= 10) {
				m = new Move(r, q, a, b);
			}
		}

		return m;

	}

	/**
	 * printBoard() prints the board. Used for debugging only.
	 */
	public void printBoard() {
		System.out.println();

		for (int i = SIZE - 1; i >= 0; i--) {
			System.out.print("| ");
			for (int j = 0; j < SIZE; j++)
				System.out.print(board[i][j].toString() + " | ");
			System.out.println();
			System.out.println();
		}
	}

	/**
	 * copyBoard reduces code for creating temporary boards.
	 */
	private Chip[][] copyBoard() {
		Chip[][] temp = new Chip[SIZE][SIZE];
		for (int i = 0; i < SIZE; ++i) {
			for (int j = 0; j < SIZE; ++j) {
				temp[i][j] = this.board[i][j];
			}
		}
		return temp;
	}

	/**
	 * resetVisit sets the visited property of each Chip on the board as false.
	 */
	private void resetVisit() {
		for (int i = 0; i < SIZE; ++i) {
			for (int j = 0; j < SIZE; ++j) {
				board[i][j].visited = false;
			}
		}
	}

	/**
	 * getMyColor()/getOppColor()
	 * 
	 * @return Will return the color of "this"/opponent player.
	 */
	public int getMyColor() {
		return this.myColor;
	}

	public int getOppColor() {
		return this.oppColor;
	}

	/**
	 * inBounds is helpful for reducing redundant code.
	 */
	private boolean inBounds(int x, int y) {
		if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
			return false;
		} else {
			return true;
		}
	}

}