import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;

class Node {
	int row, col;
	int initialRow = -1, initialCol = -1;

	public Node(int row, int col) {
		this.row = row;
		this.col = col;
	}
}

public class homework {
	public static final int EMPTY = 0;
	public static final int LIZARD = 1;
	public static final int TREE = 2;
	public static String algorithm = "";
	public static String lines[];
	public static int nurserySize;
	public static int noOfLizards;

	public static HashMap<Integer, TreeSet<Integer>> rowTree = new HashMap<Integer, TreeSet<Integer>>();
	public static HashMap<Integer, TreeSet<Integer>> colTree = new HashMap<Integer, TreeSet<Integer>>();

	public static HashMap<Integer, TreeSet<Integer>> rowLizard = new HashMap<Integer, TreeSet<Integer>>();
	public static HashMap<Integer, TreeSet<Integer>> colLizard = new HashMap<Integer, TreeSet<Integer>>();

	private static void initialSetUp() {
		for (int i = 0; i < nurserySize; i++) {
			rowTree.put(i, new TreeSet<Integer>());
			colTree.put(i, new TreeSet<Integer>());
			rowLizard.put(i, new TreeSet<Integer>());
			colLizard.put(i, new TreeSet<Integer>());
		}
	}

	private static boolean validPosition(int row, int col) {
		int i, j;
		Integer ir, jr, ic, jc;
		ir = rowLizard.get(row).floor(col - 1);
		if (ir != null) {
			jr = rowTree.get(row).ceiling(ir + 1);
			if (jr == null)
				return false;
			if (jr != null && jr > col)
				return false;
		}

		ic = colLizard.get(col).floor(row - 1);
		if (ic != null) {
			jc = colTree.get(col).ceiling(ic + 1);
			if (jc == null)
				return false;
			if (jc != null && jc > row)
				return false;
		}

		for (i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
			if (rowTree.get(i).contains(j))
				break;
			if (rowLizard.get(i).contains(j))
				return false;
		}

		for (i = row - 1, j = col + 1; i >= 0 && j < nurserySize; i--, j++) {
			if (rowTree.get(i).contains(j))
				break;
			if (rowLizard.get(i).contains(j))
				return false;
		}

		if (!algorithm.equalsIgnoreCase("BFS")) {
			ir = rowLizard.get(row).ceiling(col + 1);
			if (ir != null) {
				jr = rowTree.get(row).floor(ir - 1);
				if (jr == null)
					return false;
				if (jr != null && jr < col)
					return false;
			}

			ic = colLizard.get(col).ceiling(row + 1);
			if (ic != null) {
				jc = colTree.get(col).floor(ic - 1);
				if (jc == null)
					return false;
				if (jc != null && jc < row)
					return false;
			}

			for (i = row + 1, j = col + 1; i < nurserySize && j < nurserySize; i++, j++) {

				if (rowTree.get(i).contains(j))
					break;
				if (rowLizard.get(i).contains(j))
					return false;
			}

			for (i = row + 1, j = col - 1; i < nurserySize && j >= 0; i++, j--) {
				if (rowTree.get(i).contains(j))
					break;
				if (rowLizard.get(i).contains(j))
					return false;
			}
		}

		return true;
	}

	private static void displayOutput(int noOfLizardsPlaced) {
		try {
			FileWriter fw = new FileWriter("output.txt");
			PrintWriter pw = new PrintWriter(fw);
			if (noOfLizardsPlaced == noOfLizards) {
				pw.println("OK");
				System.out.println("OK");
				for (int row = 3; row < lines.length; row++) {
					char values[] = lines[row].toCharArray();
					for (int col : rowLizard.get(row - 3)) {
						values[col] = '1';
					}
					pw.println(String.valueOf(values));
					System.out.println(values);
				}
			} else {
				pw.println("FAIL");
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void BFS() {
		Queue<List<Node>> queue = new LinkedList<>();
		int noOfLizardsPlaced = 0;
		int row = 0, col = 0;
		List<Node> parentNodeList = new ArrayList<>();

		do {
			if (!queue.isEmpty()) {
				noOfLizardsPlaced = 0;
				for (int i = 0; i < nurserySize; i++) {
					rowLizard.put(i, new TreeSet<Integer>());
					colLizard.put(i, new TreeSet<Integer>());
				}
				parentNodeList = queue.remove();
				for (Node node : parentNodeList) {
					rowLizard.get(node.row).add(node.col);
					colLizard.get(node.col).add(node.row);
					noOfLizardsPlaced++;
					row = node.row;
					col = node.col + 1;
				}
			}
			if (noOfLizardsPlaced == noOfLizards) {
				break;
			}

			while (row < nurserySize) {
				if (rowLizard.get(row).size() > 0) {
					if (col < nurserySize && rowTree.get(row).ceiling(col) != null) {
						col = rowTree.get(row).ceiling(col) + 1;
					}
					if (col == nurserySize || rowTree.get(row).size() == 0) {
						if (row + 1 < nurserySize) {
							row++;
							col = 0;
						} else {
							break;
						}
					}
				}
				if (!rowTree.get(row).contains(col) && !rowLizard.get(row).contains(col)) {
					rowLizard.get(row).add(col);
					colLizard.get(col).add(row);
					if (validPosition(row, col)) {
						List<Node> newNodeList = new ArrayList<>();
						newNodeList.addAll(parentNodeList);
						newNodeList.add(new Node(row, col));
						queue.add(newNodeList);
					}
					rowLizard.get(row).remove(col);
					colLizard.get(col).remove(row);
				}
				col++;
				if (col == nurserySize) {
					if (rowTree.get(row).size() == 0) {
						break;
					}
					row++;
					col = 0;
				}
			}
		} while (!queue.isEmpty() && noOfLizardsPlaced <= noOfLizards);

		displayOutput(noOfLizardsPlaced);
	}

	private static void DFS() {
		Stack<Node> stack = new Stack<Node>();
		int countLizard = 0;
		Random random = new Random(24);

		Integer nextTree = rowTree.get(0).ceiling(0);
		if (nextTree != null && nextTree != 0) {
			stack.push(new Node(0, random.nextInt(nextTree)));
		} else {
			stack.push(new Node(0, random.nextInt(nurserySize)));
		}

		outer: while (!stack.empty()) {
			Node top = stack.peek();

			int row = top.row;
			int col = top.col;
			int i = row;
			int j = col;
			if (top.initialRow == -1) {
				top.initialRow = row;
				top.initialCol = col;
			}

			while (i < nurserySize) {
				if (!rowTree.get(i).contains(j) && !rowLizard.get(i).contains(j)) {
					rowLizard.get(i).add(j);
					colLizard.get(j).add(i);

					if (validPosition(i, j)) {
						countLizard++;
						Integer nextTreePosition = rowTree.get(i).ceiling(j + 1);
						if (countLizard == noOfLizards) {
							break outer;
						}
						if (nextTreePosition != null) {
							stack.push(
									new Node(i, random.nextInt(nextTreePosition) + (nurserySize - nextTreePosition)));
						} else if (i + 1 < nurserySize) {
							stack.push(new Node(i + 1, random.nextInt(nurserySize)));
						} else {
							rowLizard.get(i).remove(j);
							colLizard.get(j).remove(i);
							countLizard--;
							break;
						}
						top.row = i;
						top.col = j;
						continue outer;
					}
					rowLizard.get(i).remove(j);
					colLizard.get(j).remove(i);
				}
				j = (j + 1) % nurserySize;

				if (j == top.initialCol) {
					i++;
					j = 0;
					top.initialCol = 0;
				}
			}
			stack.pop();
			if (!stack.empty()) {
				Node temp = stack.peek();
				rowLizard.get(temp.row).remove(temp.col);
				colLizard.get(temp.col).remove(temp.row);
				temp.col = (temp.col + 1) % nurserySize;
				if (temp.col == temp.initialCol) {
					temp.row++;
					temp.col = 0;
				}
				countLizard--;
			}
		}
		displayOutput(countLizard);
	}

	private static void SA() {
		double currentTime = System.currentTimeMillis()/60000.0;
		double endTime = currentTime + 4;
		int currentLizardsAttack = 0;
		if(generateRandomStartState(endTime - 0.5)){
			currentLizardsAttack = countAttackingLizards();
		}
		else{
			DFS();
   		return;
		}
		double T;
		Random r = new Random(24);

		int oldRow, oldCol, newRow, newCol;
		while ((System.currentTimeMillis() / 60000.0) <= endTime && currentLizardsAttack != 0) {

			T = 1/Math.log(currentTime);
			if (T == 0) {
				break;
			}
			oldRow = r.nextInt(nurserySize);
			oldCol = 0;
			newRow = 0;
			newCol = 0;
			outer: while ((System.currentTimeMillis() / 60000.0) < endTime) {
				for (oldCol = 0; oldCol < nurserySize; oldCol++) {
					if (rowLizard.get(oldRow).contains(oldCol)) {
						break outer;
					}
				}
				if (oldCol == nurserySize)
					oldRow = r.nextInt(nurserySize);
			}
			while ((System.currentTimeMillis() / 60000.0) < endTime) {
				newRow = ((((oldRow + (r.nextInt(nurserySize) * r.nextInt(nurserySize)) + nurserySize) % nurserySize)
						+ nurserySize) % nurserySize);
				newCol = ((((oldCol + (r.nextInt(nurserySize) * r.nextInt(nurserySize)) + nurserySize) % nurserySize)
						+ nurserySize) % nurserySize);
				if (!rowTree.get(newRow).contains(newCol) && !rowLizard.get(newRow).contains(newCol)) {
					rowLizard.get(oldRow).remove(oldCol);
					colLizard.get(oldCol).remove(oldRow);

					rowLizard.get(newRow).add(newCol);
					colLizard.get(newCol).add(newRow);
					break;
				}
			}
			int newLizardsAttack = countAttackingLizards();

			int E = newLizardsAttack - currentLizardsAttack;
			if (E < 0) {
				currentLizardsAttack = newLizardsAttack;
			} else if (Math.random() < Math.exp(-E / T)) {
				currentLizardsAttack = newLizardsAttack;
			} else {
				rowLizard.get(newRow).remove(newCol);
				colLizard.get(newCol).remove(newRow);

				rowLizard.get(oldRow).add(oldCol);
				colLizard.get(oldCol).add(oldRow);
			}
			currentTime = System.currentTimeMillis()/60000.0;
		}
		if((System.currentTimeMillis() / 60000.0) > endTime){
			for (int i = 0; i < nurserySize; i++) {
				rowLizard.put(i, new TreeSet<Integer>());
				colLizard.put(i, new TreeSet<Integer>());
			}

			Stack<Node> stack = new Stack<Node>();
			int countLizard = 0;
			Random random = new Random(24);

			Integer nextTree = rowTree.get(0).ceiling(0);
			if (nextTree != null && nextTree != 0) {
				stack.push(new Node(0, random.nextInt(nextTree)));
			} else {
				stack.push(new Node(0, random.nextInt(nurserySize)));
			}

			outer: while (!stack.empty()) {
				Node top = stack.peek();

				int row = top.row;
				int col = top.col;
				int i = row;
				int j = col;
				if (top.initialRow == -1) {
					top.initialRow = row;
					top.initialCol = col;
				}

				while (i < nurserySize) {
					if (!rowTree.get(i).contains(j) && !rowLizard.get(i).contains(j)) {
						rowLizard.get(i).add(j);
						colLizard.get(j).add(i);

						if (validPosition(i, j)) {
							countLizard++;
							Integer nextTreePosition = rowTree.get(i).ceiling(j + 1);
							if (countLizard == noOfLizards) {
								break outer;
							}
							if (nextTreePosition != null) {
								stack.push(
										new Node(i, random.nextInt(nextTreePosition) + (nurserySize - nextTreePosition)));
							} else if (i + 1 < nurserySize) {
								stack.push(new Node(i + 1, random.nextInt(nurserySize)));
							} else {
								rowLizard.get(i).remove(j);
								colLizard.get(j).remove(i);
								countLizard--;
								break;
							}
							top.row = i;
							top.col = j;
							continue outer;
						}
						rowLizard.get(i).remove(j);
						colLizard.get(j).remove(i);
					}
					j = (j + 1) % nurserySize;

					if (j == top.initialCol) {
						i++;
						j = 0;
						top.initialCol = 0;
					}
				}
				stack.pop();
				if (!stack.empty()) {
					Node temp = stack.peek();
					rowLizard.get(temp.row).remove(temp.col);
					colLizard.get(temp.col).remove(temp.row);
					temp.col = (temp.col + 1) % nurserySize;
					if (temp.col == temp.initialCol) {
						temp.row++;
						temp.col = 0;
					}
					countLizard--;
				}
			}
			displayOutput(countLizard);
		}
		else{
			displayOutput(noOfLizards - currentLizardsAttack);
		}
	}

	private static boolean generateRandomStartState(double endTime) {
		Random r = new Random(24);
		int noOfLizardsPlaced = 0, col = 0, row;
     	col = r.nextInt(nurserySize);

		while ((System.currentTimeMillis() / 60000.0) < endTime && noOfLizardsPlaced < noOfLizards) {
			row = noOfLizardsPlaced % nurserySize;
			if(rowTree.get(row).size() == nurserySize)
				row = r.nextInt(nurserySize);

			if (!rowTree.get(row).contains(col) && !rowLizard.get(row).contains(col)) {
				rowLizard.get(row).add(col);
				colLizard.get(col).add(row);
				noOfLizardsPlaced++;
			}
			col = (col+2)%nurserySize;
		}
		if(noOfLizards == noOfLizardsPlaced){
			return true;
		}
		return false;
	}

	private static int countAttackingLizards() {
		int attackingLizards = 0;
		for (int row = 0; row < nurserySize; row++) {
			for (Integer col : rowLizard.get(row)) {
				if (!validPosition(row, col)) {
					attackingLizards++;
				}
			}
		}
		return attackingLizards;
	}

	public static void main(String[] args) {
		try {
			FileReader fr = new FileReader("CRLF/SA/SA(3).txt");
			BufferedReader br = new BufferedReader(fr);
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buffer.append(line.trim() + " ");
			}
			lines = buffer.toString().split(" ");
			algorithm = lines[0];
			nurserySize = Integer.parseInt(lines[1]);
			noOfLizards = Integer.parseInt(lines[2]);
			int noOfTrees = 0, j = 0;

			initialSetUp();

			for (int i = 3; i < lines.length; i++) {
				j = 0;
				while ((j = lines[i].indexOf(TREE + "", j)) != -1) {
					rowTree.get(i - 3).add(j);
					colTree.get(j).add(i - 3);
					j++;
					noOfTrees++;
				}
			}

			if (noOfLizards > nurserySize + noOfTrees) {
				displayOutput(0);
			} else {
				if (algorithm.equalsIgnoreCase("BFS")) {
					BFS();
				} else if (algorithm.equalsIgnoreCase("DFS")) {
					DFS();
				} else if (algorithm.equalsIgnoreCase("SA")) {
					SA();
				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
