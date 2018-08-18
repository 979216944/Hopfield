package amath383;

import amath383.DigitRecognizer.Mode;

/**
 * <b>Hopfield</b> uses a hopfield neural network Model
 * to train, and recognize input digit patterns.
 */
public class Hopfield {
	private Matrix weightMatrix; 
	
	/**
	 * Constructor to create a hopfield object with a weight matrix.
	 * @param numOfNodes number of nodes. 
	 */
	public Hopfield(int numOfNodes) {
		weightMatrix = new Matrix(numOfNodes, numOfNodes);
	}
	
	/**
	 * Get the weight matrix.
	 * @return the weight matrix.
	 */
	public Matrix getWeightMatrix() { 
		return weightMatrix; 
	}
	
	/**
	 * Train an input digit pattern.
	 * @param inputDigitPattern input digit pattern.
	 * @throws Exception
	 */
	public void train(double[] inputDigitPattern) throws Exception {
		// Transform input digit pattern to a bipolar pattern.
		double[] bipolarInput = toBipolar(inputDigitPattern);
		// Build bipolar input as a matrix.
		Matrix bipolarMatrix = Matrix.toRowMatrix(bipolarInput); 
		// Transpose the bipolar matrix: (bipolar matrix)^T.
		Matrix transposedBipolarMatrix = bipolarMatrix.transpose();
		// (Transposed bipolar matrix) x (bipolar matrix).
		Matrix crossProductMatrix = transposedBipolarMatrix.multiply(bipolarMatrix);
		// Trained matrix = (3) - (Identity Matrix).
		Matrix trainedMatrix = crossProductMatrix.subtract(Matrix.identity(weightMatrix.getData().length));
		
		// Show mathematical process for getting trained matrix if mode = SHOW_MATH.
		if (DigitRecognizer.mode == Mode.SHOW_MATH) {
			System.out.println("#-- train --#");
			System.out.println("#-- Calculate the Trained Matrix --#");
			System.out.println("1) Get the bipolar matrix \n" + bipolarMatrix);
			System.out.println("2) Transpose the bipolar matrix:\n"+ transposedBipolarMatrix);
			System.out.println("3) (Transposed bipolar matrix) x (bipolar matrix):\n"+ crossProductMatrix);
			System.out.println("4) Trained matrix = (3) - (Identity Matrix):\n"+ trainedMatrix);
			System.out.println("<-- Update Weight Matrix -->");
			System.out.println("current weight matrix:\n" + weightMatrix.toString("N", "N"));
		}
		
		// Update weight matrix.
		weightMatrix = weightMatrix.add(trainedMatrix);
		
		// Show mathematical process if mode = SHOW_MATH.
		if (DigitRecognizer.mode == Mode.SHOW_MATH) {
			System.out.println("Updated Weight Matrix = (Trained Matrix) + (Current Weight Matrix)\n" 
					+ weightMatrix.toString("N","N"));
		}
	}
	
    //RecallPattern
    public double[] updateAsyn(double[] inputDigitPattern){
		// Transform the input digit pattern to a bipolar pattern.
		double[] bipolarInput = toBipolar(inputDigitPattern);
		// weight matrix
        double[][] w = weightMatrix.getData();
        // value matrix
        double[] v = bipolarInput;
        boolean doWhile=true;
        int iteration=1;
        int n = w.length;

        //Recalling
        while (doWhile) {
            int stateChange = 0;
            System.out.println("----\nRecall of " + iteration + "-th state,");
            for (int i = 0; i < n; i++) {
                int v_new = 0, net = 0;
                //Calculate each net[i]
                for (int j = 0; j < n; j++) {
                	//w[i][j]*v[j]
                    net += w[i][j] * v[j];
                }
                //Next state of v[i]
                if (net >= 0) {
                    v_new = 1;
                }
                if (net < 0) {
                    v_new = 0;
                }

                if (v_new != v[i]) {
                    stateChange++;
                    v[i] = v_new;
                }
            }
            System.out.println("Energy:" + EnergyFunction(v) + "\n----");
            
            //if Converge(stable)?
            if (stateChange == 0) {
                doWhile = false;
            }
            iteration++;
        }
        return v;
    }
    
    /**
     * Energy function
     * @param inputDigitPattern
     * @return
     */
    private double EnergyFunction(double[] inputDigitPattern){
        double energy = 0;
        double[][] w = weightMatrix.getData();
        int n = w.length;
        double[] v = inputDigitPattern;

        for (int a = 0; a < n; a++){
            for (int b = 0; b < n; b++){
                if (a != b){
                	energy += w[a][b] * v[a] * v[b];
                }
            }
        }
        return ((-0.5) * energy);
    }
    
	/**
	 * Recognize an input digit pattern.
	 * @param inputDigitPattern input digit pattern
	 * @return an output pattern.
	 */
	public double[] recognize(double[] inputDigitPattern) {
		// Transform the input digit pattern to a bipolar pattern.
		double[] bipolarInput = toBipolar(inputDigitPattern);
		// Create a output digit pattern.
		double[] outputDigitPattern = new double[inputDigitPattern.length];
		// Build the bipolar input as a matrix.
		Matrix bipolarMatrix = Matrix.toRowMatrix(bipolarInput);
		
		// Show mathematical process for recognizing an input digit pattern. 
		if (DigitRecognizer.mode == Mode.SHOW_MATH) {
			System.out.println("#-- recognize --#");
			System.out.println("1) Weight matrix:\n" + weightMatrix.toString("N", "N"));
			System.out.println("2) Get the bipolar matrix for input \n" + bipolarMatrix);
			System.out.println("3) dot product bipolar matrix & each of the columns in weight matrix");
		}
		
		// Updating nodes.
		for (int i = 0; i < inputDigitPattern.length; i++) {
			try {
				// Create a column matrix.
				Matrix columnMatrix = weightMatrix.getColumnMatrix(i);
				double dotProductResult = bipolarMatrix.dotProduct(columnMatrix);
				
				// Show mathematical process for dot product. 
				if (DigitRecognizer.mode == Mode.SHOW_MATH) {
					System.out.print("[3."+ String.format("%02d", i) +
							"] (bipolar matrix) . (Weight matrix column "+ String.format("%02d", i)+") = ");
				}
				
				// Update weights.
				if (dotProductResult > 0) {
					outputDigitPattern[i] = 1.00;
					if (DigitRecognizer.mode == Mode.SHOW_MATH) {
						System.out.println(" "+ dotProductResult + "  > 0  ==>  1");
					}
				} else {
					outputDigitPattern[i] = 0;
					if (DigitRecognizer.mode == Mode.SHOW_MATH) {
						System.out.println(dotProductResult + " <= 0  ==>  0");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return outputDigitPattern;
	}
	
	/**
	 * Transform a pattern to a bipolar pattern.
	 * @param pattern pattern
	 * @return a bipolar pattern
	 */
	public static double[] toBipolar(double[] pattern) {
		double[] bipolarPattern = new double[pattern.length];
		for (int i = 0; i < pattern.length; i++) {
			if (pattern[i] == 0) {
				bipolarPattern[i] = -1.00;
			} else {
				bipolarPattern[i] = 1.00;
			}
		}
		return bipolarPattern;
	}
	
	/**
	 * Transform a bipolar pattern to a pattern.
	 * @param bipolarPattern bipolar pattern
	 * @return pattern
	 */
	public static double[] fromBipolar(double[] bipolarPattern) {
		double[] pattern = new double[bipolarPattern.length];
		for (int i = 0; i < bipolarPattern.length; i++) {
			if (bipolarPattern[i] == 1.00) {
				pattern[i] = 1.00;
			} else {
				pattern[i] = 0.00;
			}
		}
		return pattern; 
	}
}
