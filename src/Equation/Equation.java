/*
 * Represents a chemical equation. 
 * 
 * Authors: Luke Giacalone, Julia McClellan, Hyun Choi
 * Version: 2/29/2016
 */

package Equation;

import java.util.ArrayList;

import ChemHelper.InvalidInputException;
import Functions.Function;


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
	
	public int balance() throws InvalidInputException {
		if(isBalanced()) return 1;
		String[] equations = createEquations();
		//for(String s: equations) System.out.println(s);
		equations = subForA(equations);
		//for(String s: equations) System.out.println(s);
		Matrix m;
		try{m = new Matrix(equations);}
		catch(InvalidInputException e){throw e;}
		//System.out.println(m);
		double[] solved = m.solve();
		for(double d: solved)
			if(("" + d).equals("NaN")) return 2;
		//for(double n: solved) System.out.println(n);
		double[] doubleCoeff = new double[solved.length + 1];
		doubleCoeff[0] = 1; //setting a = 1
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
		
		if(isBalanced()) return 1;
		else return 0;
	}
	
	//Balances the equation if the right side is one compound and the left side compounds all contain exactly one element.
	public int balance2() {
		//Checks if the conditions are met first before using the method.
		if(right.size() != 1) return 0;
		for(Compound c: left) if(c.getIons().length != 1) return 0;
		Compound rightCompound = right.get(0);
		for(int index = 0; index < left.size(); index ++)
		{
			Ions leftIon = left.get(index).getIons()[0], rightIon = null;
			for(Ions ion: rightCompound.getIons())
			{
				if(ion.equals(leftIon))
				{
					rightIon = ion;
					break;
				}
			}
			if(rightIon == null) return 0;
			int num1 = leftIon.getNum() * left.get(index).getNum(), num2 = rightCompound.getNum() * rightIon.getNum(), gcd = Function.gcd(num1, num2);
			num1 = num1 / gcd;
			num2 = num2 / gcd;
			rightCompound.setNum(rightCompound.getNum() * num1);
			for(int index2 = 0; index2 <= index; index2++)
			{
				left.get(index2).setNum(num2 * left.get(index2).getNum() * (index2 != index ? num1 : 1));
			}
		}
		System.out.println(isBalanced());
		if(isBalanced()) return 1;
		else return 0;
	}

	
	//creates equations for each ion
	private String[] createEquations() {
		String skeleton = "";
		ArrayList<Monatomic> ions = new ArrayList<Monatomic>();
		int var = 1;
		for(Compound c: left) { //getting and assigning vars to left side compounds
			for(Monatomic e: c.getNoPoly()) //getting all the different ions in the equation
				if(!ions.contains(e)) ions.add(new Monatomic(e));
			if(!skeleton.equals("")) skeleton += "+";
			skeleton += getNextVar(var);
			var++;
		}
		//System.out.println(ions);
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
	
	//takes an array of Fractions and makes them into integers
	private static int[] integerize(double[] c) {
		double[] c2 = c.clone();
		int times = Function.integerize(c2);
		
		int[] newCo = new int[c.length];
		for(int i = 0; i < newCo.length; i++)
			newCo[i] = (int) (c[i] * times);
		return newCo;
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
/*	
	public static void main(String[] args) {
		try {
			Equation eq = parseEquation("H<sub>2</sub>+O<sub>2</sub>+C<sub>4</sub>\u2192H<sub>2</sub>OC");
			eq.balance();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
*/
}