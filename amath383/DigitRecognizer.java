package amath383;

import java.util.Scanner;

/**
 * Command Line Interface:
 *   This application needs jdk8 installed on user machine
 */
public class DigitRecognizer {
	/**
	 * users can select between DEFAULT and SHOW_MATH mode
	 *
	 * DEFAULT - show digit recognition result only
	 * SHOW_MATH - show digit recognition step by step
	 */
	public enum Mode {
		DEFAULT,  // Clean Mode
		SHOW_MATH  // Math Mode - to see what is behind the scene
	};
	
	public static Mode mode = Mode.DEFAULT;  // default mode
	public static final int NUM_OF_ROWS = 5;  // number of rows for the digit pattern
	public static final String[] TRAIN_PATTERNS = 
		{"11111001100110011111", "01100110011001101111", 
		 "11110001111110001111", "11110001111100011111",
		 "10011001111100010001", "11111000111100011111",
		 "11111000111110011111", "11110001000100010001",
		 "11111001111110011111", "11111001111100010001"};
	
	public static void main(String[] args) throws Exception {
		// Print welcome words and ask for number of columns.
		showWelcome();
		
		// Read input, number of columns.
		Scanner scan = new Scanner(System.in);
		int numOfcols = Integer.parseInt(scan.nextLine());
		int numOfNodes = numOfcols * NUM_OF_ROWS;  // num of nodes
		
		// Create Hopfield Network with given number of columns,
		// and initialize input and output digit patterns.
		Hopfield hopfield = new Hopfield(numOfNodes);  // hopfield for training
		
		boolean flag = true;
		while (flag) {
			// Show available options.
			showOptions();
			
			// Take an input option and translate to a command.
			String in = scan.nextLine();
			String command = toCommand(in);
			
			// Do the corresponding command.
			switch (command) {
				case "train" :
					train(hopfield, numOfNodes, scan);
					break;
				case "recognize":
					recognize(hopfield, numOfNodes, scan);
					break;
				case "recognizeAsyn":
					recognizeAsyn(hopfield, numOfNodes, scan);
				case "clear":
					clear(hopfield);
					break;
				case "select mode":
					selectMode(scan);
					break;
				case "exit":
					flag = false;
					break;
				case "alreadyTrained":
					alreadyTrained(hopfield, numOfNodes);
					break;
			}
		}
		scan.close();
		System.exit(0);
	}

	
	private static void alreadyTrained(Hopfield hopfield, int numOfNodes) throws Exception {
		for (int i = 0; i < 10; i++) {
				double [] inputDigitPattern = 
						getInput(TRAIN_PATTERNS[i], numOfNodes);
				hopfield.train(inputDigitPattern);
//				System.out.print(Matrix.getMatrix(inputDigitPattern, NUM_OF_ROWS).toPackedString());
		}
		System.out.println("---------train success----------\n");
	}
	/**
	 * Select Mode.
	 * @param scan scanner
	 */
	private static void selectMode(Scanner scan) {
		System.out.println("> Select mode: ");
		System.out.println("> 1) DEFAULT");
		System.out.println("> 2) SHOW_MATH");
		if (scan.nextLine().equalsIgnoreCase("SHOW_MATH") || scan.equals("2")) {
			mode = Mode.SHOW_MATH;
		} else {
			mode = Mode.DEFAULT;
		}
	}
	
	/**
	 * Clear the weight matrix.
	 * @param hopfield
	 */
	private static void clear(Hopfield hopfield) {
		hopfield.getWeightMatrix().clear();
		System.out.println("------- Weight matrix cleared --------");
	}
	
	/**
	 * Recognize the input digit pattern.
	 * @param hopfield Hopfield model
	 * @param numOfNodes number of nodes
	 * @param scan scanner
	 * @throws Exception
	 */
	private static void recognizeAsyn(Hopfield hopfield, int numOfNodes, Scanner scan) throws Exception {
		System.out.println("> Provide input pattern: ");
		double[] inputDigitPattern = getInput(scan.nextLine(), numOfNodes);
		double[] onputDigitPattern = hopfield.updateAsyn(inputDigitPattern);
		System.out.println("Input pattern:");
		System.out.print(Matrix.getMatrix(inputDigitPattern, NUM_OF_ROWS).toPackedString());
		System.out.println("Output pattern:");
		System.out.print(Matrix.getMatrix(onputDigitPattern, NUM_OF_ROWS).toPackedString());
	}
	
	/**
	 * Recognize the input digit pattern.
	 * @param hopfield Hopfield model
	 * @param numOfNodes number of nodes
	 * @param scan scanner
	 * @throws Exception
	 */
	private static void recognize(Hopfield hopfield, int numOfNodes, Scanner scan) throws Exception {
		System.out.println("> Provide input pattern: ");
		double[] inputDigitPattern = getInput(scan.nextLine(), numOfNodes);
		double[] onputDigitPattern = hopfield.recognize(inputDigitPattern);
		System.out.println("Input pattern:");
		System.out.print(Matrix.getMatrix(inputDigitPattern, NUM_OF_ROWS).toPackedString());
		System.out.println("Output pattern:");
		System.out.print(Matrix.getMatrix(onputDigitPattern, NUM_OF_ROWS).toPackedString());
	}
	
	/**
	 * Train the input digit pattern.
	 * @param hopfield Hopfield model
	 * @param numOfNodes number of nodes
	 * @param scan scanner
	 * @throws Exception
	 */
	private static void train(Hopfield hopfield, int numOfNodes, Scanner scan) throws Exception {
		System.out.println(
			"> Please give your training digit pattern " + "(" + numOfNodes + ") : ");
		double [] inputDigitPattern = 
				getInput(scan.nextLine(), numOfNodes);
		hopfield.train(inputDigitPattern);
		System.out.print(Matrix.getMatrix(inputDigitPattern, NUM_OF_ROWS).toPackedString());
		System.out.println("---------train success----------\n");
	}
	
	/**
	 * Show options available.
	 */
	private static void showOptions() {
		System.out.println("> Select options: ");
		System.out.println("> 1) train");
		System.out.println("> 2) recognize");
//		System.out.println("> 3) recognizeAsyn");
		System.out.println("> 4) clear");
		System.out.println("> 5) select mode");
		System.out.println("> 6) exit");
		System.out.println("> 7) alreadyTrained"); 
	}
	
	/**
	 * Translates input to the corresponding command:
	 * 	1) train
	 *  2) recognize
	 *  3) recognizeAsyn
	 *  4) clear
	 *  5) select mode
	 *  6) exit
	 *  7) alreadyTrained
	 * @param in input
	 * @return "" if no command matches,
	 * 		   the corresponding command.
	 */
	private static String toCommand(String in) {
		if (in.equalsIgnoreCase("train") || in.equals("1")) {
			return "train";
		} else if (in.equalsIgnoreCase("recognize") || in.equals("2")) {
			return "recognize";
		} else if (in.equalsIgnoreCase("recognizeAsyn") || in.equals("3")) {
			return "recognizeAsyn";
		} else if (in.equalsIgnoreCase("clear") || in.equals("4")) {
			return "clear";
		} else if (in.equalsIgnoreCase("select mode") || in.equals("5")) {
			return "select mode";
		} else if (in.equalsIgnoreCase("exit") || in.equals("6")) {
			return "exit";
		} else if (in.equalsIgnoreCase("alreadyTrained") || in.equals("7")) {
			return "alreadyTrained";
		}
		return "";
	} 
	
	/**
	 * Show welcome instructions.
	 */
	private static void showWelcome() {
		System.out.println("> =========================================================");
		System.out.println("> |        # Amath 383 Hopfield Digit Recognizer #         |");
		System.out.println("> =========================================================");
		System.out.println(">");
		System.out.println("> Input digit pattern format: ");
		System.out.println(">       [# of nodes] = [# of columns] x [5 rows]    ");
		System.out.println(">     ");
		System.out.println(">          # of columns   ");
		System.out.println(">           -------------   ");
		System.out.println("> #    1|  x x x ... x x x ");
		System.out.println("> of   2|  x x x ... x x x ");
		System.out.println("> rows 3|  x x x ... x x x ");
		System.out.println(">      4|  x x x ... x x x ");
		System.out.println(">      5|  x x x ... x x x ");
		System.out.println(">");
		System.out.println("> Please Enter # of columns (recommend 4): ");
	}

	/**
	 * @precon input.length() = size
	 * @return an array out of input
	 */
	private static double[] getInput(String input, int size) throws Exception {
		if (input.length() != size) {
			throw new Exception("Input length ("
					+ input.length()
					+ ") does not match size("
					+ size + ")");
		}
		double[] result = new double[size];
		for (int i = 0; i < size; i++) {
			result[i] = Double.parseDouble("" + input.charAt(i));
		}
		return result;
	}
}
/*
40 neurons sample test data ==>
0001100000011000000110000001100011111111
1111111100000011111111111100000011111111
1111111100000011111111110000001111111111

20 neurons sample test data ==>
01100110011001101111
11110011111111001111
11110011111100111111
10011001100110010000
*/
