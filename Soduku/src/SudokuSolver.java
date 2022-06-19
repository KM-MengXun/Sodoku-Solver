
public class SudokuSolver {
	public static void main(String[] args) throws Exception {
		Board puzzle = new Board();
		puzzle.loadPuzzle("oni");
		puzzle.display();
		puzzle.logicCycles();
		System.out.println("===============================");
		puzzle.display();
		System.out.println(puzzle.errorFound());
		System.out.println(puzzle.isSolved());
		puzzle.guess();
		puzzle.display();
		System.out.println(puzzle.errorFound());
		System.out.println(puzzle.isSolved());

	}
}
