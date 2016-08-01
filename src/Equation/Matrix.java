/*
 * File: Matrix.java
 * Package: Equation
 * Version: 02/23/2016
 * Author: Luke Giacalone and Julia McClellan
 * ----------------------
 * Represents a matrix which will be used to solve a system of equations
 */

package Equation;

import java.util.ArrayList;
import java.util.Arrays;

import ChemHelper.InvalidInputException;
import Elements.Element;
import Functions.Function;

public class Matrix {
	
	private int[][] matrix;
	private int rows;
	private int cols;
	//private int numVar; //number of variables
	
	public Matrix(Equation equation) {
		Element[] elements = equation.getElements().toArray(new Element[0]);
		rows = elements.length + 1;
		cols = equation.getLeft().size() + equation.getRight().size() + 1;
		matrix = new int[rows][cols];
		ArrayList<Compound> lhs = equation.getLeft();
		ArrayList<Compound> rhs = equation.getRight();
		for (int i = 0; i < elements.length; i++) {
			int j = 0;
			for (int k = 0; k < lhs.size(); j++, k++)
				matrix[i][j] = lhs.get(k).numberOf(elements[i]);
			for (int k = 0; k < rhs.size(); j++, k++)
				matrix[i][j] = -rhs.get(k).numberOf(elements[i]);
		}
	}
	
	/**
	 * Returns the matrix.
	 * @return The int[][] that represents this matrix.
	 */
	public int[][] getMatrix() {
		return matrix;
	}
	
	/**
	 * Solves this matrix as a set of linear equations.
	 * @throws InvalidInputException If all coefficients are zero.
	 */
	public void solve() throws InvalidInputException {
		gaussianMethod();
		
		// Find row with more than one non-zero coefficient
		int i;
		for (i = 0; i < rows - 1; i++) {
			if (countNonzeroCoeffs(i) > 1) break;
		}
		if (i == rows - 1) throw new InvalidInputException(InvalidInputException.NOT_BALANCED); // Unique solution with all coefficients zero
		
		// Add an inhomogeneous equation
		matrix[rows - 1][i] = 1;
		matrix[rows - 1][cols - 1] = 1;
		
		gaussianMethod();
	}
	
	/**
	 * Changes this matrix to reduced row echelon form (RREF), except that each leading coefficient is not necessarily 1. 
	 * Each row is simplified.
	 */
	private void gaussianMethod() {
		// Simplify all rows
		for(int[] x: matrix) x = simplifyRow(x);;

		// Compute row echelon form (REF)
		int numPivots = 0;
		for(int i = 0; i < cols; i++) {
			// Find pivot
			int pivotRow = numPivots;
			while(pivotRow < rows && matrix[pivotRow][i] == 0) pivotRow++;
			if(pivotRow == rows) continue;
			int pivot = matrix[pivotRow][i];
			swapRows(numPivots, pivotRow);
			numPivots++;

			// Eliminate below
			for(int j = numPivots; j < rows; j++) {
				int g = Function.gcd(pivot, matrix[j][i]);
				matrix[j] = simplifyRow(addRows(multiplyRow(matrix[j], pivot / g), multiplyRow(matrix[i], -matrix[j][i] / g)));
			}
		}

		// Compute reduced row echelon form (RREF), but the leading coefficient need not be 1
		for(int i = rows - 1; i >= 0; i--) {
			// Find pivot
			int pivotCol = 0;
			while(pivotCol < cols && matrix[i][pivotCol] == 0) pivotCol++;
			if (pivotCol == cols) continue;
			int pivot = matrix[i][pivotCol];

			// Eliminate above
			for (int j = i - 1; j >= 0; j--) {
				int g = Function.gcd(pivot, matrix[j][pivotCol]);
				matrix[j] = simplifyRow(addRows(multiplyRow(matrix[j], pivot / g), multiplyRow(matrix[i], -matrix[j][pivotCol] / g)));
			}
		}
	}
	
	/**
	 * Returns a new row where the leading non-zero number (if any) is positive, and the GCD of the row is 0 or 1. 
	 * @param x The row to be simplified.
	 * @return The simplified row.
	 */
	private int[] simplifyRow(int[] x) {
		x = x.clone();
		int sign = 0;
		for (int i = 0; i < x.length; i++) {
			if (x[i] > 0) {
				sign = 1;
				break;
			} 
			else if (x[i] < 0) {
				sign = -1;
				break;
			}
		}
		int[] y = x.clone();
		if (sign == 0) return y;
		int g = gcdRow(x) * sign;
		for (int i = 0; i < y.length; i++) y[i] /= g;
		return y;
	}
	
	/**
	 * Finds the GCD of the row.
	 * @param x The row to be used.
	 * @return The GCD of the row.
	 */
	private int gcdRow(int[] x) {
		int result = 0;
		for(int i : x) result = Function.gcd(i, result);
		return result;
	}
	
	/**
	 * Swaps the two rows at the given indexes;
	 * @param i The first row.
	 * @param j The second row.
	 */
	private void swapRows(int i, int j) {
		if (i < 0 || i >= rows || j < 0 || j >= rows) throw new IndexOutOfBoundsException();
		int[] temp = matrix[i].clone();
		matrix[i] = matrix[j];
		matrix[j] = temp;
	}
	
	/**
	 * Adds two rows together and returns their sum.
	 * @precondition The two rows are the same length.
	 * @param x The first row.
	 * @param y The second row.
	 * @return The sum of the rows.
	 */
	private int[] addRows(int[] x, int[] y) {
		x = x.clone();
		y = y.clone();
		int[] z = new int[x.length];
		for(int i = 0; i < x.length; i++) {
			z[i] = x[i] + y[i];
		}
		return z;
	}
	
	/**
	 * Multiplies the given row by the given scaler.
	 * @param x The row.
	 * @param c The scaler.
	 * @return The modified row.
	 */
	private int[] multiplyRow(int[] x, int c) {
		x = x.clone();
		for(int i = 0; i < x.length; i++) {
			x[i] = x[i] * c;
		}
		return x;
	}
	
	/**
	 * Counts the number of nonzero coefficients in the given row.
	 * @param row The row to check.
	 * @return The number of nonzero coefficients.
	 */
	private int countNonzeroCoeffs(int row) {
		int count = 0;
		for(int i = 0; i < cols; i++) {
			if(matrix[row][i] != 0) count++;
		}
		return count;
	}
	
	/**
	 * Gets the coefficients of the elements from the matrix.
	 * @return The coefficients of the matrix/equation.
	 * @throws InvalidInputException If there is a problem balancing.
	 */
	public int[] getCoefficients() throws InvalidInputException {
		if (cols - 1 > rows || matrix[cols - 2][cols - 2] == 0)
			/*throw "Multiple independent solutions";*/throw new InvalidInputException(InvalidInputException.NOT_BALANCED);
		
		int lcm = 1;
		for(int i = 0; i < cols - 1; i++)
			lcm = lcm / Function.gcd(lcm, matrix[i][i]) * matrix[i][i];
		
		int[] coefs = new int[cols - 1];
		boolean allzero = true;
		for(int i = 0; i < cols - 1; i++) {
			int coef = lcm / matrix[i][i] * matrix[i][cols - 1];
			coefs[i] = coef;
			allzero &= coef == 0;
		}
		if(allzero)
			throw new InvalidInputException(InvalidInputException.NOT_BALANCED);//throw "Assertion error: All-zero solution";
		return coefs;
	}

	/**
	 * Creates and returns the String representation of the matrix.
	 * @return The String representation of the matrix.
	 */
	@Override
	public String toString() {
		String str = "";
		for(int[] line: matrix) {
			if(str.length() > 0) str += "\n";
			str += Arrays.toString(line);
		}
		return str;
	}

}
