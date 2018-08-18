package amath383;

/**
 * <b>Matrix</b> is an class for immutable matrix objects
 * and implements the same behaviors of mathematical matrices,
 * including addition, subtraction, dot product, cross product.
 */
public class Matrix {
	private double data[][];
	enum ScalarOperation { ADD, SUBTRACT, MULTIPLY, DIVIDE };

	/**
	 * create an empty matrix of rows * columns
	 * where every position contains 0
	 * @precon rows > 0, columns > 0
	 * @param rows - the count of rows
	 * @param columns - the count of columns
	 */
	public Matrix(int rows, int columns) {
		data = new double[rows][columns];
	}

	/**
	 * create an matrix from data stored in 2d array
	 * @param data - the 2d array storage of matrix
	 */
	public Matrix(double data[][]) {
		this.data = new double[data.length][data[0].length];
		for (int i = 0; i < this.data.length; i++) {
			for (int j = 0; j < this.data[0].length; j++) {
				this.data[i][j] = data[i][j];
			}
		}
	}

	/**
	 * Matrix addition
	 * @param matrix - the other matrix to be added to this
	 * @return a new matrix of the result
	 * @throws Exception - if the other matrix has different same size than this
	 */
	public Matrix add(Matrix matrix) throws Exception {
		if ((data.length != matrix.data.length) || 
				(data[0].length != matrix.data[0].length)) throw new Exception("matrices must have matching size");
		double returnData[][] = new double[data.length][data[0].length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				returnData[i][j] = data[i][j] + matrix.data[i][j];
			}
		}
		return new Matrix(returnData);
	}

	/**
	 * Matrix subtraction
	 * @param matrix - the other matrix to be subtracted from this
	 * @return a new matrix of the result
	 * @throws Exception - if the other matrix has different same size than this
	 */
	public Matrix subtract(Matrix matrix) throws Exception {
		return add(matrix.scalarOperation(-1, ScalarOperation.MULTIPLY));
	}

	/**
	 * Matrix cross product
	 * @param matrix - the other matrix to multiply with this
	 * @return a new matrix of the result
	 * @throws Exception - if the row counts of the other matrix
	 * 						!= column counts of this matrix,
	 * 						meaning they cannot multiply each other
	 */
	public Matrix multiply(Matrix matrix) throws Exception {
		if (data[0].length != matrix.data.length) throw new Exception("matrices must have matching inner dimension"); 
		double returnData[][] = new double[data.length][matrix.data[0].length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j <  matrix.data[0].length; j++) {
				double result = 0;
				for (int k = 0; k < data[0].length; k++) {
					result += data[i][k] * matrix.data[k][j];
				}
				returnData[i][j] = result;
			}
		}
		return new Matrix(returnData);
	}

	/**
	 *   Helper function - a shorthand to apply scalar operation
	 *   to individual elements in this matrix
	 * @param x - offset used in operation
	 * @param scalarOperation - macro indicates which operation to use,
	 *                        	options stored in {@code ScalarOperation}
	 * @return a new matrix of result
	 */
	public Matrix scalarOperation(double x, ScalarOperation scalarOperation) {
		double returnData[][] = new double[data.length][data[0].length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				switch (scalarOperation) {
					case ADD:
						returnData[i][j] = data[i][j] + x;
						break;
					case SUBTRACT:
						returnData[i][j] = data[i][j] - x;
						break;
					case MULTIPLY:
						returnData[i][j] = data[i][j] * x;
						break;
					case DIVIDE:
						returnData[i][j] = data[i][j] / x;
						break;
				}
			}
		}
		return new Matrix(returnData);
	}

	/**
	 * helper function - IDENTITY
	 * @param size - the row/column counts of identity matrix
	 * @return an identity matrix
	 */
	public static Matrix identity(int size) {
		Matrix matrix = new Matrix(size, size);
		for (int i = 0; i < size; i++) {
			matrix.data[i][i] = 1;
		}
		return matrix;
	}

	/**
	 * transpose this matrix
	 * @return return a matrix of result
	 */
	public Matrix transpose() {
		double[][] returnData = new double[data[0].length][data.length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				returnData[j][i] = data[i][j];
			}
		}
		return new Matrix(returnData);
	}

	/**
	 * matrix dot product
	 * @param matrix - the other matrix to multiply with this
	 * @return a new matrix of result
	 * @throws Exception - if the other matrix doesn't have same size as this
	 * 						or either is not vector
	 */
	public double dotProduct(Matrix matrix) throws Exception {
		if (!this.isVector() || !matrix.isVector()) {
			throw new Exception("can only dot product 2 vectors");
		} else if ((this.flatten().length != matrix.flatten().length)) {
			throw new Exception("both vectors must have same size");
		}
		double returnValue = 0;
		for (int i = 0; i < this.flatten().length; i++) returnValue += this.flatten()[i] * matrix.flatten()[i];
		return returnValue;
	}

	/**
	 * set individual elements to 0
	 * @return a reference to this
	 */
	public Matrix clear() {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				data[i][j] = 0;
			}
		}
		return this;
	}

	/**
	 * create a row matrix
	 * @param array - store elements to put in this array
	 * @return return a matrix of 1 * array.length
	 */
	public static Matrix toRowMatrix(double[] array) {
		double[][] data = new double[1][array.length];
		System.arraycopy(array, 0, data[0], 0, array.length);
		return new Matrix(data);
	}

	/**
	 * create a snapshot of a column in this matrix
	 * @param column - the column number
	 * @return return a new matrix copy of that column
	 */
	public Matrix getColumnMatrix(int column) {
		double[][] data = new double[this.data.length][1];
		for (int i = 0; i < this.data.length; i++) {
			data[i][0] = this.data[i][column];
		}
		return new Matrix(data);
	}

	/**
	 * check if this is a vector
	 */
	public boolean isVector() {
		boolean flag = false;
		if (this.data.length == 1) flag = true;
		else if( this.data[0].length == 1) flag = true;
		return flag;
	}

	/**
	 * flatten this matrix
	 * @return an array of individual elements in this matrix
	 */
	public double[] flatten() {
		double returnValue[] = new double[data.length * data[0].length];
		int i = 0;
		for (int row = 0; row < data.length; row++) {
			for (int column = 0; column < data[0].length; column++) {
				returnValue[i++] = data[row][column];
			}
		}
		return returnValue;
	}

	/**
	 * @return the 2d array storing this matrix
	 */
	public double[][] getData() {
		return data;
	}

	/**
	 * retrieve a matrix from a flattened one, represented by
	 * an array
	 * @param data - the array storing the individual elements
	 * @param numbOfRows - row counts of resulting matrix
	 * @return a new matrix from data
	 * @throws Exception - if size of data is not dividable by numbOfRows
	 */
	public static Matrix getMatrix(double data[], int numbOfRows) throws Exception {
		if (data.length % numbOfRows != 0) throw new Exception("size of data not divisible by number of rows");
		Matrix drawingMatrix = new Matrix(numbOfRows, data.length / numbOfRows);
		int i = 0;
		for (int row = 0; row < drawingMatrix.data.length; row++) {
			for (int column = 0; column < drawingMatrix.data[0].length; column++) {
				drawingMatrix.data[row][column] = data[i++];
			}
		}
		return drawingMatrix;
	}

	/**
	 * compressed string representation of this matrix
	 * @return
	 */
	public String toPackedString() {
		StringBuffer bodySB = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				bodySB.append((int)data[i][j]);
			}
			bodySB.append("\n");
		}
		return bodySB.toString();
	}

	/**
	 * a formatted string representation of this matrix
	 * @param columnLabel
	 * @param rowLabel
	 * @return
	 */
	public String toString(String columnLabel, String rowLabel) {
		StringBuffer headingSB = new StringBuffer();
		headingSB.append("    | ");
		for (int i = 0; i < data[0].length; i++) {
			headingSB.append(" " + columnLabel + String.format("%02d", i) +"");
		}
		headingSB.append("\n");
		StringBuffer bodySB = new StringBuffer();
		for (int i = 0; i < headingSB.length(); i++) {
			bodySB.append("-");
		}
		bodySB.append("\n");
		for (int i = 0; i < data.length; i++) {
			bodySB.append(rowLabel + String.format("%02d", i) +" |");
			for (int j = 0; j < data[0].length; j++) {
				if (data[i][j] >= 0) {
					bodySB.append("   " + (int) data[i][j]);
				} else {
					bodySB.append("  "+  (int)data[i][j]);
				}
			}
			bodySB.append("\n");
		}
		return headingSB.toString() + bodySB.toString();
	}

	@Override
	public String toString() {
		return toString("C", "R");
	}
}
