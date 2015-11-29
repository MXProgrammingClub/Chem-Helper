package Equation;

import java.util.ArrayList;

import ChemHelper.Compound;
import ChemHelper.InvalidInputException;
import ChemHelper.Ions;


public class Equation
{
	
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
	private ArrayList<Compound> left, right;
	
	public Equation()
	{
		left = new ArrayList<Compound>();
		right = new ArrayList<Compound>();
	}
	
	public Equation(ArrayList<Compound> left, ArrayList<Compound> right)
	{
		this.left = left;
		this.right = right;
	}
	
	public void addToLeft(Compound c)
	{
		left.add(c);
	}
	
	public void addToRight(Compound c)
	{
		right.add(c);
	}
	
	public static Equation parseEquation(String eq) throws InvalidInputException
	{
		String left, right;
		left = eq.substring(0, eq.indexOf("="));
		right = eq.substring(eq.indexOf("=") + 1);
		return new Equation(parseSide(left), parseSide(right));
	}
	
	private static ArrayList<Compound> parseSide(String side) throws InvalidInputException
	{
		ArrayList<Compound> compounds = new ArrayList<Compound>();
		while(side.indexOf("+") != -1)
		{
			compounds.add(Compound.parseCompound(side.substring(0, side.indexOf("+"))));
			side = side.substring(side.indexOf("+") + 1);
		}
		compounds.add(Compound.parseCompound(side));
		return compounds;
	}

	public String toString()
	{
		if(left.size() == 0 || right.size() == 0) return "";
		String equation = "";
		for(Compound compound: left)
		{
			equation += compound + " + ";
		}
		equation = equation.substring(0, equation.length() - 2) + "\u2192";
		for(Compound compound: right)
		{
			equation += compound + " + ";
		}
		equation = equation.substring(0, equation.length() - 2);
		return equation;
	}
	
	public ArrayList<Compound> getLeft() {
		return left;
	}
	
	public ArrayList<Compound> getRight() {
		return right;
	}
	
	public boolean balance() {
		if(isBalanced()) return true;
		String[] equations = createEquations();
		int[] coefficients = solveEquations(equations);
		if(coefficients[0] == -1) return false; //if the system could not be solved
		
		int index = 0;
		for(Compound c: left) {
			c.setNum(coefficients[index]);
			index++;
		}
		for(Compound c: right) {
			c.setNum(coefficients[index]);
			index++;
		}
		return true;
	}
	
	//creates equations for each ion
	private String[] createEquations() {
		String skeleton = "";
		ArrayList<Ions> ions = new ArrayList<Ions>();
		int var = 1;
		for(Compound c: left) { //getting and assigning vars to left side compounds
			for(Ions e: c.getIons()) //getting all the different ions in the equation
				if(!ions.contains(e)) ions.add(e);
			if(!skeleton.equals("")) skeleton += "+";
			skeleton += getNextVar(var);
			var++;
		}
		skeleton += "=";
		for(Compound c: right) { //getting and assigning vars to right side compounds
			if(skeleton.charAt(skeleton.length() - 1) != '=') skeleton += "+";
			skeleton += getNextVar(var);
			var++;
		}
		
		String[] equations = new String[ions.size()];
		int index = 0;
		//goes and adds coefficients to the equations when needed. Vars w/o coefficients have a coefficient of 0.
		for(Ions e: ions) {
			int var2 = 1;
			String eq = skeleton;
			for(Compound c: left) {
				for(Ions i: c.getIons()) {
					if(i.getElement().toString().equals(e.getElement().toString())) {
						eq = eq.substring(0, eq.indexOf(getNextVar(var2))) + i.getNum() + eq.substring(eq.indexOf(getNextVar(var2)));
						break;
					}
				}
				var2++;
			}
			for(Compound c: right) {
				for(Ions i: c.getIons()) {
					if(i.getElement().toString().equals(e.getElement().toString())) {
						eq = eq.substring(0, eq.indexOf(getNextVar(var2))) + i.getNum() + eq.substring(eq.indexOf(getNextVar(var2)));
						break;
					}
				}
				var2++;
			}
			equations[index] = eq;
			index++;
		}
		
		//now need to clean up the equations
		for(int i = 0; i < equations.length; i++) {
			String eq = equations[i];
			for(int j = eq.length() - 1; j > 0; j--) { //gets rid of useless vars everywhere except index 0
				if((eq.charAt(j) == '+') || (Character.isLetter(eq.charAt(j)) && eq.charAt(j - 1) == '+')) {
					eq = eq.substring(0, j) + eq.substring(j + 1);
					j++;
				}
			}
			while(Character.isLetter(eq.charAt(0)) || eq.charAt(0) == '+') //gets rid of useless vars at the beginning
				eq = eq.substring(1);
			
			equations[i] = eq;
		}
		
		return equations;
	}
	
	//provides a new variable
	//BUG: the variables past the alphabet dont work right  
	private static String getNextVar(int prev) {
		ArrayList<Integer> nums = new ArrayList<Integer>();
		for(; prev > 26; prev -= 26) 
			nums.add(prev % 26);
		nums.add(prev);
		String var = "";
		for(int i = nums.size() - 1; i >= 0; i--)
			var += alphabet.charAt(nums.get(i) - 1);
		return var;
	}
	
	//@FIX make more efficient
	//returns the number of the variable
	private static int getVarNum(String var) {
		int i = 1;
		while(!getNextVar(i).equals(var))
			i++;
		return i;
	}
	
	//attempt at getting the system of equations to solve
	private int[] solveEquations(String[] equations) {
		double[] coefficients = new double[left.size() + right.size()];
		for(int i = 1; i < coefficients.length; i++) //assign -1 to all but the first slots
			coefficients[i] = -1;
		coefficients[0] = 1; //we assert that the coefficient of a = 1;
		
		boolean done = false;

		while(!done) {
			for(String eq: equations) {
				ArrayList<String> vars = new ArrayList<String>(); 
				int i = 0;
				while(i < eq.length()) { //to find all the variables in the equation
					if(!Character.isLetter(eq.charAt(i)))
						i++;
					else {
						String temp2 = "";
						temp2 += eq.charAt(i);
						i++;
						while(i < eq.length() && Character.isLetter(eq.charAt(i))) { //goes to find the entire var in case >1 letter
							temp2 += eq.charAt(i);
							i++;
						}
						vars.add(temp2);
					}
				}

				boolean solvable = false;
				for(String str: vars) { //sees if only one variable is unknown
					if(coefficients[getVarNum(str) - 1] == -1)
						if(!solvable)
							solvable = true;
						else //if more than one unknown
							solvable = false;
				}

				if(solvable) { //if can solve (ie only one unknown)
					String temp = eq;
					int index = 0;
					while(index < temp.length()) { //goes through each known variable and changes it to "*" + value
						if(Character.isLetter(temp.charAt(index))) {
							int start = index;
							int end;
							while(index < temp.length() && Character.isLetter(temp.charAt(index)))
								index++;
							end = index;
							if(coefficients[getVarNum(temp.substring(start, end)) - 1] != -1) {
								temp = temp.substring(0, start) + "*" + coefficients[getVarNum(temp.substring(start, end)) - 1] + temp.substring(end);
							}
						}
						else index++;
					}

					index = 0;
					while(index < temp.length()) { //takes all the "*" + value and turns it into a number
						if(Character.isDigit(temp.charAt(index))) {
							int start = index;
							int end;
							while(Character.isDigit(temp.charAt(index)))
								index++;
							end = index;
							if(temp.charAt(end) == '*') {
								int start2 = end + 1;
								int end2 = end + 2;
								while(end2 < temp.length() && (Character.isDigit(temp.charAt(end2)) || temp.charAt(end2) == '.')) //finds the end of the var value
									end2++;
								double newVal = Double.parseDouble(temp.substring(start, end)) * Double.parseDouble(temp.substring(start2, end2));
								temp = temp.substring(0, start) + newVal + temp.substring(end2);
								index = start + ("" + newVal).length();
							}
						}
						else index++;
					}

					//to determine the side of the '=' the variable is on
					boolean isLeft = false; //the side that the variable is on
					index = 0;
					while(temp.charAt(index) != '=') {
						if(Character.isLetter(temp.charAt(index))) {
							isLeft = true;
							break;
						}
						index++;
					}

					//to add the numbers on the non var side of the equation (ie 2+5=3c becomes 7+3c)
					String side;
					if(!isLeft) 
						side = temp.split("=")[0];
					else
						side = temp.split("=")[1];
					if(side.indexOf("+") < 0); //if there is no addition to be done
					else {
						String[] nums = side.split("+");
						int total = 0;
						for(String str: nums)
							total += Integer.parseInt(str);
						if(!isLeft)
							temp = "" + total + temp.substring(temp.indexOf("="));
						else
							temp = temp.substring(0, temp.indexOf("=") + 1) + total;
					}

					//at this point, I have ie 5=2c+3 or 1=2c and need to subtract and divide
					if(isLeft && temp.substring(0, temp.indexOf("=")).indexOf("+") > 0) {
						String[] stuff = temp.substring(0, temp.indexOf("=")).split("+");
						int total = 0;
						String var = "";
						for(String str: stuff) {
							try {
								total += Integer.parseInt(str);
							}
							catch(NumberFormatException e) {
								var = str;
							}
						}
						temp = "" + (Integer.parseInt(temp.split("=")[0]) - total) + "=" + var;
					}
					if(!isLeft && temp.substring(temp.indexOf("=")).indexOf("+") > 0) {
						String[] stuff = temp.substring(temp.indexOf("=")).split("+");
						int total = 0;
						String var = "";
						for(String str: stuff) {
							try {
								total += Integer.parseInt(str);
							}
							catch(NumberFormatException e) {
								var = str;
							}
						}
						temp = var + "=" + (Integer.parseInt(temp.split("=")[1]) - total);
					}

					//now to divide (ie 4=2a becomes 2=a)
					if(isLeft) {
						String var = temp.split("=")[0];
						String num = "";
						int j = 0;
						while(Character.isDigit(var.charAt(j))) {
							num += var.charAt(j);
							j++;
						}
						var = var.substring(j);
						//System.out.println(var + ", " + num + ", " + temp);
						temp = var + "= " + (Double.parseDouble(temp.split("=")[1]) / Double.parseDouble(num));
					}
					else {
						String var = temp.split("=")[1];
						String num = "";
						int j = 0;
						while(Character.isDigit(var.charAt(j))) {
							num += var.charAt(j);
							j++;
						}
						var = var.substring(j);
						temp = var + "=" + (Double.parseDouble(temp.split("=")[0]) / Double.parseDouble(num));
					}

					//storing the coefficient in its place in the coefficients array
					coefficients[getVarNum(temp.split("=")[0]) - 1] = Double.parseDouble(temp.split("=")[1]);
				}

			}

			done = true;
			for(double d: coefficients)
				if(d == -1) done = false;

		}
		
		return integerize(coefficients);
	}
	
	//takes an array of doubles and makes them into integers
	private static int[] integerize(double[] coefficients) {
		double denom = 1;
		for(double d: coefficients)
			if(d % 1 > 0)
				denom *= (d % 1);
		int[] newCo = new int[coefficients.length];
		for(int i = 0; i < coefficients.length; i++)
			newCo[i] = (int) (coefficients[i] / denom);
		
		return newCo;
	}
	
	/*public boolean balance()
	{
		if(isBalanced()) return true;
		ArrayList<Compound> oldLeft = left, oldRight = right;
		final int NUM_METHODS = 1;
		for(int method = 1; method <= NUM_METHODS; method++)
		{
			boolean balanced = tryMethod(method);
			if(balanced) return true;
			else
			{
				left = oldLeft;
				right = oldRight;
			}
		}
		return false;
	}
	
	private boolean tryMethod(int num)
	{
		if(num == 1) return balance1();
		return false;
	}*/
	
	/*
	 * Balances the equation if the right side is one compound and the left side compounds all contain exactly one element.
	 */
	/*private boolean balance1()
	{
		//Checks if the conditions are met first before using the method.
		if(right.size() != 1) return false;
		for(Compound c: left) if(c.getIons().length != 1) return false;
		Compound rightCompound = right.get(0);
		for(int index = 0; index < left.size(); index ++)
		{
			Ions leftIon = left.get(index).getIons()[0], rightIon = null;
			for(Ions ion: rightCompound.getIons())
			{
				if(ion.getElement().equals(leftIon.getElement())) rightIon = ion;
			}
			if(rightIon == null) return false;
			int num1 = leftIon.getNum() * left.get(index).getNum(), num2 = rightCompound.getNum() * rightIon.getNum(), mult = num1 * num2, gcd = Function.gcd(num1, num2);
			while(gcd != 1)
			{
				mult = mult / gcd;
				num1 = num1 / gcd;
				num2 = num2 / gcd;
				gcd = Function.gcd(num1, num2);
			}
			int setRight = mult / rightIon.getNum(), setLeft = mult / leftIon.getNum();
			rightCompound.setNum(rightCompound.getNum() * setRight);
			left.get(index).setNum(left.get(index).getNum() * setLeft);
			for(int index2 = 0; index2 <= index; index2++)
			{
				if(index != index2) left.get(index2).setNum(setRight * left.get(index2).getNum());
			}
		}
		return isBalanced();
	}*/
	
	private boolean isBalanced()
	{
		ArrayList<Ions> leftIons = toIons(left), rightIons = toIons(right);
		for(Ions leftIon: leftIons)
		{
			boolean found = false;
			for(Ions rightIon: rightIons)
			{
				if(leftIon.getElement().equals(rightIon.getElement()))
				{
					if(leftIon.getNum() != rightIon.getNum()) return false;
					found = true;
				}
			}
			if(!found) return false;
		}
		return true;
	}
	
	private ArrayList<Ions> toIons(ArrayList<Compound> compounds)
	{
		ArrayList<Ions> ions = new ArrayList<Ions>();
		for(Compound c: compounds)
		{
			Ions[] compoundIons = c.getIons();
			for(Ions ion: compoundIons)
			{
				boolean found = false;;
				for(Ions test: ions)
				{
					if(test.getElement().equals(ion.getElement()))
					{
						test.setNum(test.getNum() + (ion.getNum() * c.getNum()));
						found = true;
						break;
					}
				}
				if(!found) ions.add(new Ions(ion.getElement(), (ion.getNum() * c.getNum())));
			}
		}
		return ions;
	}
}