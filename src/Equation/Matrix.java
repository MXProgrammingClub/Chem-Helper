/*
 * File: Matrix.java
 * Package: Equation
 * Version: 01/05/2016
 * Author: Luke Giacalone
 * ----------------------
 * Represents a matrix which will be used to solve a system of equations
 */

package Equation;

public class Matrix {
	
	private int[][] matrix;
	private int numVar; //number of variables

	//precondition: the equations are in the form 1a + 2b = c where c is a constant
	//				there are the same number of equations as there are unknowns
	public Matrix(String[] equations) {
		numVar = equations[0].split("=")[0].split("\\+").length;

		matrix = new int[numVar][numVar + 1];
		int index1 = 0;
		
		while(numVar < equations.length) { //to check whether 2 equations are the same and then remove the unneeded one
			check:
			for(int i = 0; i < equations.length; i++) {
				for(int j = 0; j < equations.length; j++) {
					if(i != j && equations[i].equals(equations[j])) {
						String[] temp = new String[equations.length - 1]; 
						int index = 0;
						for(int k = 0; k < equations.length; k++)
							if(k != i) {
								temp[index] = equations[k];
								index++;
							}
						equations = temp;
						break check;
					}
				}
			}
		}
		
		for(String str: equations) {
			String half1 = str.substring(0, str.indexOf("="));
			String half2 = str.substring(str.indexOf("=") + 1);
			int index2 = 0;

			//for the part before the equal sign
			for(String part: half1.split("\\+")) {
				String temp = part.substring(0, part.length() - 1);
				while(Character.isLetter(temp.charAt(temp.length() - 1)))
					temp = temp.substring(0, temp.length() - 1);

				matrix[index1][index2] = Integer.parseInt(temp);
				index2++;
			}

			//for the part after the equal sign
			matrix[index1][index2] = Integer.parseInt(half2);

			index1++;
		}
		
	}

	//switches two rows
	//precondition: the two variables, row1 and row2, are valid row indices
	public void switchRows(int row1, int row2) {
		int[] temp = matrix[row1];
		matrix[row1] = matrix[row2];
		matrix[row2] = temp;
	}

	//multiplies a row by a given constant
	public void multiply(int row, int factor) {
		for(int i = 0; i < matrix[row].length; i++)
			matrix[row][i] *= factor;
	}

	//adds row2 to row1
	public void add(int row1, int row2) {
		for(int i = 0; i < matrix[row1].length; i++)
			matrix[row1][i] = matrix[row1][i] + matrix[row2][i];
	}

	//returns whether a matrix is an identity matrix (1s on top-left to bottom-right diagonal and 0s everywhere else)
	// [1, 0, 0, | 3]
	// [0, 1, 0, | 4]
	// [0, 0, 1, | 1]
	public boolean isIdentity() {
		for(int row = 0; row < matrix.length; row++) {
			for(int i = 0; i < matrix[row].length - 1; i++) {
				System.out.println(row + ", " + i + ", " + matrix[row][i]);
				if(!(row == i && matrix[row][i] == 1) && !(row != i && matrix[row][i] == 0)) return false;
			}
		}
		return true;
	}

	//this solve stuff I found online and adapted to our needs
	public double[] solve() {
		double[][] constants = new double[numVar][1];
		double[][] doubleMatrix = new double[matrix.length][matrix[0].length];
		for(int i = 0; i < matrix.length; i++) {
			constants[i][0] = matrix[i][matrix[0].length - 1];
			for(int j = 0; j < matrix[0].length; j++)
				doubleMatrix[i][j] = matrix[i][j];
		}

		double inverted_mat[][] = invert(doubleMatrix);

		//Multiplication of mat inverse and constants
		double[] result = new double[numVar];
		for (int i = 0; i < numVar; i++) {
			for (int j = 0; j < 1; j++) {
				for (int k = 0; k < numVar; k++){	 
					result[i] = result[i] + inverted_mat[i][k] * constants[k][j];
				}
			}
		}

		return result;
	}

	public static double[][] invert(double a[][]) 
	{
		int n = a.length;
		double x[][] = new double[n][n];
		double b[][] = new double[n][n];
		int index[] = new int[n];
		for (int i=0; i<n; ++i) 
			b[i][i] = 1;

		// Transform the matrix into an upper triangle
		gaussian(a, index);
		// Update the matrix b[i][j] with the ratios stored
		for (int i=0; i<n-1; ++i)
			for (int j=i+1; j<n; ++j)
				for (int k=0; k<n; ++k)
					b[index[j]][k]
							-= a[index[j]][i]*b[index[i]][k];

		// Perform backward substitutions
		for (int i=0; i<n; ++i) 
		{
			x[n-1][i] = b[index[n-1]][i]/a[index[n-1]][n-1];
			for (int j=n-2; j>=0; --j) 
			{
				x[j][i] = b[index[j]][i];
				for (int k=j+1; k<n; ++k) 
				{
					x[j][i] -= a[index[j]][k]*x[k][i];
				}
				x[j][i] /= a[index[j]][j];
			}
		}
		return x;
	}
	// Method to carry out the partial-pivoting Gaussian
	// elimination.  Here index[] stores pivoting order.

	public static void gaussian(double a[][], int index[]) 
	{
		int n = index.length;
		double c[] = new double[n];
		// Initialize the index
		for (int i=0; i<n; ++i) 
			index[i] = i;

		// Find the rescaling factors, one from each row
		for (int i=0; i<n; ++i) 
		{
			double c1 = 0;
			for (int j=0; j<n; ++j) 
			{
				double c0 = Math.abs(a[i][j]);
				if (c0 > c1) c1 = c0;
			}
			c[i] = c1;
		}

		// Search the pivoting element from each column
		int k = 0;
		for (int j=0; j<n-1; ++j) 
		{
			double pi1 = 0;
			for (int i=j; i<n; ++i) 
			{
				double pi0 = Math.abs(a[index[i]][j]);
				pi0 /= c[index[i]];
				if (pi0 > pi1) 
				{
					pi1 = pi0;
					k = i;
				}
			}

			// Interchange rows according to the pivoting order
			int itmp = index[j];
			index[j] = index[k];
			index[k] = itmp;
			for (int i=j+1; i<n; ++i) 	
			{
				double pj = a[index[i]][j]/a[index[j]][j];
				// Record pivoting ratios below the diagonal
				a[index[i]][j] = pj;

				// Modify other elements accordingly
				for (int l=j+1; l<n; ++l)
					a[index[i]][l] -= pj*a[index[j]][l];
			}
		}
	}

	//returns the string representation of a matrix
	public String toString() {
		String str = "";
		for(int[] line: matrix) {
			if(str.length() > 0) str += "\n";
			str += "[";
			for(int i: line) {
				if(str.charAt(str.length() - 1) != '[') str += ", ";
				str += i;
			}
			str += "]";
		}
		return str;
	}

}
