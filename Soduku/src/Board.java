import java.io.File;
import java.util.Scanner;

public class Board {
	/*The Sudoku Board is made of 9x9 cells for a total of 81 cells.
	 * In this program we will be representing the Board using a 2D Array of cells.
	 * 
	 */

	private Cell[][] board = new Cell[9][9];

	//The variable "level" records the level of the puzzle being solved.
	private String level = "";

	private int numOfGuesses = 0;
	//board that save changes
	private Board[] savedBoard = new Board[81];
	
	///TODO: CONSTRUCTOR
	//This must initialize every cell on the board with a generic cell.  It must also assign all of the boxIDs to the cells
	public Board()
	{
		for(int x = 0; x < 9; x++)
			for(int y = 0 ; y < 9; y++)
			{
				board[x][y] = new Cell();
				board[x][y].setBoxID( 3*(x/3) + (y)/3+1);
			}
	}



	///TODO: loadPuzzle
	/*This method will take a single String as a parameter.  The String must be either "easy", "medium" or "hard"
	 * If it is none of these, the method will set the String to "easy".  The method will set each of the 9x9 grid
	 * of cells by accessing either "easyPuzzle.txt", "mediumPuzzle.txt" or "hardPuzzle.txt" and setting the Cell.number to 
	 * the number given in the file.  
	 * 
	 * This must also set the "level" variable
	 * TIP: Remember that setting a cell's number affects the other cells on the board.
	 */
	public void loadPuzzle(String level) throws Exception
	{
		this.level = level;
		String fileName = "easyPuzzle.txt";
		if(level.contentEquals("medium"))
			fileName = "mediumPuzzle.txt";
		else if(level.contentEquals("hard"))
			fileName = "hardPuzzle.txt";
		else if(level.contentEquals("oni"))
			fileName = "oni.txt";
		Scanner input = new Scanner (new File(fileName));

		for(int x = 0; x < 9; x++)
			for(int y = 0 ; y < 9; y++)
			{
				int number = input.nextInt();
				if(number != 0)
					solve(x, y, number);
			}

		input.close();

	}

	///TODO: isSolved
	/*This method scans the board and returns TRUE if every cell has been solved.  Otherwise it returns FALSE
	 * 
	 */
	public boolean isSolved()
	{
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(board[i][j].getNumber() == 0) {
//					System.out.println("UnSolved");
					return false;
				}
			}
		}
		return true;
	}


	///TODO: DISPLAY
	/*This method displays the board neatly to the screen.  It must have dividing lines to show where the box boundaries are
	 * as well as lines indicating the outer border of the puzzle
	 */
	public void display()
	{
		for(int i = 0; i < 9; i++) {
			int column = 0;
			
			System.out.print("|");
			
			for(int j = 0; j < 9; j++) {
				System.out.print(" " + board[i][j].getNumber()+ " ");
				column++;
				if(column%3 == 0)
					System.out.print("|");
			}
			System.out.println();
			if(i%3 == 2 && i != 8)
				System.out.println("———————————————————————————————");
		}
	}

	///TODO: solve
	/*This method solves a single cell at x,y for number.  It also must adjust the potentials of the remaining cells in the same row,
	 * column, and box.
	 */
	public void solve(int x, int y, int number)
	{
		board[x][y].setNumber(number);
		
		//r c b
		for(int i = 0; i < 9; i++)
			if(board[x][i] != board[x][y])
				board[x][i].cantBe(number);
		
		for(int i = 0; i < 9; i++) 
			if(board[i][y] != board[x][y])
				board[i][y].cantBe(number);
		

		for(int i = 0; i < 9; i++)
			for(int j = 0; j < 9; j++)
				if(board[i][j] == board[x][y])
					continue;
				else 
					if(board[i][j].getBoxID() == board[x][y].getBoxID())
						board[i][j].cantBe(number);
	}


	//logicCycles() continuously cycles through the different logic algorithms until no more changes are being made.
	public void logicCycles()throws Exception
	{
		int changesMade = 0;
		do
		{
			changesMade = 0;
			changesMade += logic1();
			changesMade += logic2();
			changesMade += logic3();
			changesMade += logic4();
			if(errorFound())
				break;
		}while(changesMade != 0);
				
	}


	
	
	///TODO: logic1
	/*This method searches each row of the puzzle and looks for cells that only have one potential.  If it finds a cell like this, it solves the cell 
	 * for that number. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic1()
	{
		int changesMade = 0;

		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {				
				if(board[i][j].numberOfPotentials() == 1 && board[i][j].getNumber() == 0) {
					solve(i,j,board[i][j].getFirstPotential());
					changesMade++;
				}
			}
		}
		return changesMade;

	}

	///TODO: logic2
	/*This method searches each row for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell.  It then does the same thing for the columns.This also tracks the number of cells that 
	 * it solved as it traversed the board and returns that number.
	 */

	public int logic2()
	{
		int changesMade = 0;
		int counter = 0, x = 0, y = 0;
		
		for(int i = 1; i < 10; i++) { 
			
			//r c
			for(int j = 0; j < 9; j++) {
				counter = 0;
				x = 0;
				y = 0;
				for(int k = 0; k < 9; k++) {
					if(board[j][k].canBe(i) && board[j][k].getNumber() != i) {
						counter++;
						x = j;
						y = k;
					}
					if(counter > 1)
						break;
				}
				if(counter == 1) {
					changesMade++;
					solve(x, y, i);
				}
			}
		
			for(int j = 0; j < 9; j++) {
				
				counter = 0;
				x = 0;
				y = 0;
				for(int k = 0; k < 9; k++) {
					if(board[k][j].canBe(i) && board[k][j].getNumber() != i) {
						counter++;
						x = k;
						y = j;
					}
					if(counter > 1)
						break;
				}
				if(counter == 1) {
					changesMade++;
					solve(x,y,i);
				}	
			}
		}
		return changesMade;
	}

	///TODO: logic3
	/*This method searches each box for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic3() {
		int changesMade = 0;
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(board[i][j].numberOfPotentials() > 1 && board[i][j].getNumber() == 0) {
					for(int k = 0; k < 10; k++) {
						boolean only = true;
						if(board[i][j].canBe(k) == true) {
							for(int x = 0; x < 9; x++) {
								for(int y = 0; y < 9; y++) {
									if(board[x][y].getBoxID() == board[i][j].getBoxID() && board[x][y].canBe(k) == true && board[x][y] != board[i][j])
										only = false;
								}
							}
							if(only && board[i][j].getNumber() == 0) {
								solve(i,j,k);
								changesMade++;
							}
						}
					}
				}
			}
		}
		return changesMade;
	}


	///TODO: logic4
		/*This method searches each row for the following conditions:
		 * 1. There are two unsolved cells that only have two potential numbers that they can be
		 * 2. These two cells have the same two potentials (They can't be anything else)
		 * 
		 * Once this occurs, all of the other cells in the row cannot have these two potentials.  Write an algorithm to set these two potentials to be false
		 * for all other cells in the row.
		 * 
		 * Repeat this process for columns and rows.
		 * 
		 * This also tracks the number of cells that it solved as it traversed the board and returns that number.
		 */
	public int logic4()
	{
		int changesMade = 0;
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(board[i][j].numberOfPotentials() == 2) {
					for(int k = j+1; k < 9; k++) {
						if(board[i][k].numberOfPotentials()==2) {
							if(board[i][j].getFirstPotential() == board[i][k].getFirstPotential() && board[i][j].getSecondPotential() == board[i][k].getSecondPotential()) {
								for(int x = 0; x < 9; x++) {
									if(x == j || x ==k)
										continue;
									if(board[i][x].canBe(board[i][j].getFirstPotential())) {
										board[i][x].cantBe(board[i][j].getFirstPotential());
										changesMade++;
									}
									if(board[i][x].canBe(board[i][j].getSecondPotential())) {
										board[i][x].cantBe(board[i][j].getSecondPotential());
										changesMade++;
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(board[i][j].numberOfPotentials() == 2) {
					for(int k = i+1; k < 9; k++) {
						if(board[k][j].numberOfPotentials()==2) {
							if(board[i][j].getFirstPotential() == board[k][j].getFirstPotential() && board[i][j].getSecondPotential() == board[k][j].getSecondPotential()) {
								for(int x = 0; x < 9; x++) {
									if(x == i || x ==k)
										continue;
									if(board[x][j].canBe(board[i][j].getFirstPotential())) {
										board[x][j].cantBe(board[i][j].getFirstPotential());
										changesMade++;
									}
									if(board[x][j].canBe(board[i][j].getSecondPotential())) {
										board[x][j].cantBe(board[i][j].getSecondPotential());
										changesMade++;
									}
								}
							}
						}
					}
				}
			}
		}
				
		return changesMade;
	}

	public void setPotential(int x, int y, int number, boolean tf)
	{
		board[x][y].setPotential(number, tf);
	}
	
	public void setNumber(int x, int y, int number)
	{
		board[x][y].setNumber(number);
	}
	
	public void guess() throws Exception
	{
		Board copy = new Board();
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				copy.setNumber(i,j,board[i][j].getNumber());
				for(int k = 1; k < 10; k++)
					copy.setPotential(i,j,k, board[i][j].canBe(k));
			}
		}
		savedBoard[numOfGuesses++] = copy;
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(board[i][j].getNumber() == 0) {
					solve(i,j,board[i][j].getFirstPotential());
					logicCycles();
					if(errorFound() == true) {
						for(int x = 0; x < 9; x++) {
							for(int y = 0; y < 9; y++) {
								board[x][y].setNumber(savedBoard[numOfGuesses-1].board[x][y].getNumber());
								for(int k = 1; k < 10; k++)
									board[x][y].setPotential(k, savedBoard[numOfGuesses-1].board[x][y].canBe(k));							}
						}
						board[i][j].cantBe(board[i][j].getFirstPotential());
						guess();
					}
					
					else if(!errorFound() && !isSolved()) {
						guess();
					}
					
					else if(!errorFound() && isSolved()) {
						return;
					}
				}
			}
		}
		
	}
	
	///TODO: errorFound
	/*This method scans the board to see if any logical errors have been made.  It can detect this by looking for a cell that no longer has the potential to be 
	 * any number.
	 */
	public boolean errorFound()
	{
		for(int i = 0; i < 9; i++)
			for(int j = 0; j < 9; j++)
				if(board[i][j].numberOfPotentials() == 0 && board[i][j].numberOfPotentials() == 0)
					return true;
				
		return false;
	}
}
