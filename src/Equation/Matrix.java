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
	
	//precondition: the equations are in the form 1a + 2b = c where c is a constant
	//				there are the same number of equations as there are unknowns
	public Matrix(String[] equations) {
		matrix = new int[equations.length][equations.length + 1];
		int index1 = 0;
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
