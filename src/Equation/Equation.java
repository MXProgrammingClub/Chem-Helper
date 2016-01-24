/*
 * Represents a chemical equation. 
 * 
 * Authors: Luke Giacalone, Julia McClellan, Hyun Choi
 * Version: 1/24/2016
 */

package Equation;

import java.util.ArrayList;

import ChemHelper.InvalidInputException;


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
		String left = eq.substring(0, eq.indexOf("\u2192")), right = eq.substring(eq.indexOf("\u2192") + 1);
		return new Equation(parseSide(left), parseSide(right));
	}
	
	private static ArrayList<Compound> parseSide(String side) throws InvalidInputException
	{
		ArrayList<Compound> compounds = new ArrayList<Compound>();
		ArrayList<String> cStrings = new ArrayList<String>();
		int index = 0;
		for(int end = side.indexOf("+", index); end != -1; cStrings.add(side.substring(index, end)), index = end + 1, end = side.indexOf("+", index));
		cStrings.add(side.substring(index));
		for(String compound: cStrings)
		{
			compounds.add(Compound.parseCompound(compound.trim()));
		}
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
		equations = subForA(equations);
		Matrix m = new Matrix(equations);
		double[] solved = m.solve();
		double[] doubleCoeff = new double[solved.length + 1];
		doubleCoeff[0] = 1.0; //setting a = 1
		for(int i = 1; i < doubleCoeff.length; i++) //transferring the rest of the variables over
			doubleCoeff[i] = solved[i - 1];
		int[] coefficients = integerize(doubleCoeff);
		
		int index = 0;
		for(Compound c: left) {
			c.setNum(coefficients[index]);
			index++;
		}
		for(Compound c: right) {
			c.setNum(coefficients[index]);
			index++;
		}
		
		return isBalanced();
	}
	
	//creates equations for each ion
	private String[] createEquations() {
		String skeleton = "";
		ArrayList<Monatomic> ions = new ArrayList<Monatomic>();
		int var = 1;
		for(Compound c: left) { //getting and assigning vars to left side compounds
			for(Monatomic e: c.getNoPoly()) //getting all the different ions in the equation
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
		for(Monatomic e: ions) {
			int var2 = 1;
			String eq = skeleton;
			for(Compound c: left) {
				for(Monatomic i: c.getNoPoly()) {
					if(i.getElement().equals(e.getElement())) {
						eq = eq.substring(0, eq.indexOf(getNextVar(var2))) + i.getNum() + eq.substring(eq.indexOf(getNextVar(var2)));
						break;
					}
				}
				var2++;
			}
			for(Compound c: right) {
				for(Monatomic i: c.getNoPoly()) {
					if(i.getElement().equals(e.getElement())) {
						eq = eq.substring(0, eq.indexOf(getNextVar(var2))) + i.getNum() + eq.substring(eq.indexOf(getNextVar(var2)));
						break;
					}
				}
				var2++;
			}
			equations[index] = eq;
			index++;
		}
		
		//now add 0s in where needed
		for(int i = 0; i < equations.length; i++) {
			String eq = equations[i];
			for(int j = eq.length() - 1; j > 0; j--) { //everywhere except index 0
				if(Character.isLetter(eq.charAt(j)) && eq.charAt(j - 1) == '+') {
					eq = eq.substring(0, j) + "0" + eq.substring(j);
					j++;
				}
			}
			while(Character.isLetter(eq.charAt(0)) || eq.charAt(0) == '+') //at the beginning
				eq = "0" + eq;
			
			if(Character.isLetter(eq.charAt(1 + eq.indexOf("="))))
				eq = eq.substring(0, eq.indexOf("=") + 1) + "0" + eq.substring(eq.indexOf("=") + 1);//weird fix for 0 just after =
			
			equations[i] = eq;
		}
		
		return equations;
	}
	
	//substitutes 1 for variable a and then rearranges the equations to be vars = constant 
	private String[] subForA(String[] equations) {
		String[] newEq = new String[equations.length];
		
		int index = 0;
		for(String eq: equations) {
			eq = eq.substring(0, eq.indexOf("a")) + eq.substring(eq.indexOf("a")); //a will be the first variable used
			String firstHalf = eq.substring(0, eq.indexOf("="));
			String secondHalf = eq.substring(eq.indexOf("=") + 1);
			String[] firstHalfStuff = firstHalf.split("\\+");
			while(firstHalfStuff.length > 1) {
				secondHalf = "-" + firstHalfStuff[firstHalfStuff.length - 1] + "+" + secondHalf;
				String[] temp = new String[firstHalfStuff.length - 1];
				temp[0] = firstHalfStuff[0];
				for(int i = 2; i < firstHalfStuff.length; i++)
					temp[i - 1] = firstHalfStuff[i];
				firstHalfStuff = temp;
			}
			
			firstHalf = firstHalfStuff[0].substring(0, firstHalfStuff[0].length() - 1);
			newEq[index] = secondHalf + "=" + firstHalf;
			index++;
		}
		
		return newEq;
	}
	
	/*public static void main(String[] args) {
		Equation eq = null;
		try {
			eq = Equation.parseEquation("O.2+C.6/H.12/O.6=C/O.2+H.2/O");
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		eq.balance();
	}*/
	
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
	
	///@TODO make more efficient
	//returns the number of the variable
	private static int getVarNum(String var) {
		int i = 1;
		while(!getNextVar(i).equals(var))
			i++;
		return i;
	}
		
	//takes an array of doubles and makes them into integers
	private static int[] integerize(double[] coefficients) {
		int[] reciprocals = new int[coefficients.length];
		for(int i = 0; i < coefficients.length; i++) //e.g. 0.2 (or 1/5) becomes 5
			reciprocals[i] = (int) (1.0 / coefficients[i]);
		
		int lcm = reciprocals[0];
	    for(int i = 1; i < reciprocals.length; i++) //finds the lcm of all the reciprocals
	    	lcm = lcm(lcm, reciprocals[i]);
	    
	    int[] newCo = new int[coefficients.length];
	    for(int i = 0; i < coefficients.length; i++)
			newCo[i] = (int) (coefficients[i] * lcm);
	    
	    return newCo;
	}
	
	//finds the least common multiple of two numbers
	private static int lcm(int a, int b){
	    return a * (b / gcd(a, b));
	}
	
	//finds the greatest common divisor of two numbers
	private static int gcd(int a, int b){
	    while (b > 0) {
	        int temp = b;
	        b = a % b; // % is remainder
	        a = temp;
	    }
	    return a;
	}
	
	private boolean isBalanced()
	{
		ArrayList<Monatomic> leftIons = toIons(left), rightIons = toIons(right);
		for(Monatomic leftIon: leftIons)
		{
			boolean found = false;
			for(Monatomic rightIon: rightIons)
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
	
	private ArrayList<Monatomic> toIons(ArrayList<Compound> compounds)
	{
		ArrayList<Monatomic> ions = new ArrayList<Monatomic>();
		for(Compound c: compounds)
		{
			Monatomic[] compoundIons = c.getNoPoly();
			for(Monatomic ion: compoundIons)
			{
				boolean found = false;;
				for(Monatomic test: ions)
				{
					if(test.getElement().equals(ion.getElement()))
					{
						test.setNum(test.getNum() + (ion.getNum() * c.getNum()));
						found = true;
						break;
					}
				}
				if(!found) ions.add(new Monatomic(ion.getElement(), (ion.getNum() * c.getNum())));
			}
		}
		return ions;
	}
}