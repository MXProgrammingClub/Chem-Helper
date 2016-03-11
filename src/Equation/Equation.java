/*
 * Represents a chemical equation. 
 * 
 * Authors: Luke Giacalone, Julia McClellan, Hyun Choi
 * Version: 3/11/2016
 */

package Equation;

import java.util.ArrayList;

import ChemHelper.InvalidInputException;
import Functions.Function;
import Elements.Element;
import Elements.Hydrogen;
import Elements.Oxygen;

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
	
	public Equation reverse()
	{
		Equation eq = new Equation();
		for(Compound c: left) eq.addToRight(c);
		for(Compound c : right) eq.addToLeft(c);
		return eq;
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
		int start = 0;
		for(int index = start, end = side.indexOf("+", index); end != -1; index = end + 1, end = side.indexOf("+", index))
		{
			//In case the plus is from the charge of an ion
			if((end < 5 || !side.substring(end - 5, end).equals("<sup>") && (end > side.length() - 7 || !side.substring(end + 1, end + 7).equals("</sup>"))))
			{
				cStrings.add(side.substring(start, end));
				start = end + 1;
			}
		}
		
		if(!side.substring(start).trim().equals("")) cStrings.add(side.substring(start));
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
		equation = equation.substring(0, equation.length() - 2) + "\u2192 ";
		for(Compound compound: right)
		{
			equation += compound + " + ";
		}
		equation = equation.substring(0, equation.length() - 3);
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
		if(isBalanced()) return 1;
		else return 0;
	}

	public int balanceRedox(boolean acidic, ArrayList<Equation> halves)
	{
		if(left.size() != 2 || right.size() != 2) return 0; //Case to be dealt with: one compound used in both half reactions
		
		//Separate into half reactions
		Equation r1 = new Equation(), r2 = new Equation();
		r1.addToLeft(left.get(0));
		Element e1 = left.get(0).getIons()[0].getElements()[0].getElement(), e2 = left.get(1).getIons()[0].getElements()[0].getElement();
		boolean contains = right.get(0).contains(e1);
		r1.addToRight(contains ? right.get(0) : right.get(1)); //Case to be dealt with: compound with multiple relevant (not oxygen) ions		
		r2.addToLeft(left.get(1));
		r2.addToRight(contains ? right.get(1) : right.get(0));
		
		//Balance non-oxygen elements in half reactions
		int num1 = r1.getLeft().get(0).numberOf(e1), num2 = r1.getRight().get(0).numberOf(e1);
		if(num1 != num2)
		{
			r1.getLeft().get(0).setNum(num2);
			r1.getRight().get(0).setNum(num1);
		}
		
		num1 = r2.getLeft().get(0).numberOf(e2);
		num2 = r2.getRight().get(0).numberOf(e2);
		if(num1 != num2)
		{
			r2.getLeft().get(0).setNum(num2);
			r2.getRight().get(0).setNum(num1);
		}
		
		//Balance oxygen and hydrogen in half reactions
		int[] count1 = getCounts(r1, acidic), count2 = getCounts(r2, acidic);
		if(halves != null)
		{
			//Adds the oxidation reaction and then the reduction reaction
			if(count1[2] < 0) halves.add(createHalf(r1, count1, acidic));
			halves.add(createHalf(r2, count2, acidic));
			if(count1[2] > 0) halves.add(createHalf(r1, count1, acidic));
		}
		
		//Multiply through by electron coefficients
		count1[2] = Math.abs(count1[2]);
		count2[2] = Math.abs(count2[2]);
		
		count1[0] *= count2[2];
		count1[1] *= count2[2];
		for(Compound c: r1.getLeft()) c.setNum(c.getNum() * count2[2]);
		for(Compound c: r1.getRight()) c.setNum(c.getNum() * count2[2]);
		
		count2[0] *= count1[2];
		count2[1] *= count1[2];
		for(Compound c: r2.getLeft()) c.setNum(c.getNum() * count1[2]);
		for(Compound c: r2.getRight()) c.setNum(c.getNum() * count1[2]);
		
		//Add in H2O and H+/OH-
		int[] counts = {count1[0] + count2[0], count1[1] + count2[1]};
		if(acidic && counts[1] > 0) left.add(0, new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 1, 1)}, counts[1]));
		if(!acidic && counts[1] > 0) left.add(0, new Compound(new Monatomic[]{new Monatomic(new Oxygen()), new Monatomic(new Hydrogen(), -1)}, counts[1]));
		if(counts[0] > 0) left.add(0, new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 2), new Monatomic(new Oxygen())}, counts[0]));
		
		if(acidic && counts[1] < 0) right.add(0, new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 1, 1)}, -counts[1]));
		if(!acidic && counts[1] < 0) right.add(0, new Compound(new Monatomic[]{new Monatomic(new Oxygen()), new Monatomic(new Hydrogen(), -1)}, -counts[1]));
		if(counts[0] < 0) right.add(0, new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 2), new Monatomic(new Oxygen())}, -counts[0]));
		
		if(isBalanced()) return 1;
		return 0;
	}
	
	private int[] getCounts(Equation half, boolean acidic)
	{
		Oxygen o = new Oxygen();
		int[] counts = {-(half.getLeft().get(0).numberOf(o) - half.getRight().get(0).numberOf(o)), 0, 0}; //{H2O, H+/OH-, e-}
		counts[1] = counts[0] * -2;
		
		if(!acidic) //changes middle element to OH- instead of H+
		{
			counts[0] += counts[1];
			counts[1] *= -1;
		}
		
		int charge = acidic ? counts[1] : -counts[1];
		for(Compound c: half.getLeft()) charge += c.getCharge() * c.getNum();
		for(Compound c: half.getRight()) charge -= c.getCharge() * c.getNum();
		counts[2] = charge;
		return counts;
	}
	
	private Equation createHalf(Equation half, int[] counts, boolean acidic)
	{
		Equation e = new Equation();
		
		if(counts[0] > 0) e.addToLeft(new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 2), new Monatomic(new Oxygen())}, counts[0]));
		if(acidic && counts[1] > 0) e.addToLeft(new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 1, 1)}, counts[1]));
		if(!acidic && counts[1] > 0) e.addToLeft(new Compound(new Monatomic[]{new Monatomic(new Oxygen()), new Monatomic(new Hydrogen(), -1)}, counts[1]));
		e.addToLeft(new Compound(half.getLeft().get(0).getIons(), half.getLeft().get(0).getState()));
		if(counts[2] > 0) e.addToLeft(new Compound(new Ions[]{new Electron()}, counts[2]));
		
		e.addToRight(new Compound(half.getRight().get(0).getIons(), half.getRight().get(0).getState()));
		if(counts[0] < 0) e.addToRight(new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 2), new Monatomic(new Oxygen())}, -counts[0]));
		if(acidic && counts[1] < 0) e.addToRight(new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 1, 1)}, -counts[1]));
		if(!acidic && counts[1] < 0) e.addToRight(new Compound(new Monatomic[]{new Monatomic(new Oxygen()), new Monatomic(new Hydrogen(), -1)}, -counts[1]));
		if(counts[2] < 0) e.addToRight(new Compound(new Ions[]{new Electron()}, -counts[2]));
		return e;
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
	public static void main(String[] args)
	{
		try
		{
			Equation eq = parseEquation("Cu(s)+NO<sub>3</sub><sup>-</sup>(aq)\u2192Cu<sup>2+</sup>(aq)+NO(g)");
			ArrayList<Equation> halves = new ArrayList<Equation>();
			eq.balanceRedox(true, halves);
			System.out.println(eq);
			System.out.println(halves);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	*/
/*
	public static void main(String[] args) {
		try {
			Equation eq = parseEquation("HIO<sub>3</sub>+FeI<sub>2</sub>+HCl\u2192FeCl<sub>3</sub>+ICl+H<sub>2</sub>O");//"H<sub>2</sub>+O<sub>2</sub>+C<sub>4</sub>\u2192H<sub>2</sub>OC+O");
			eq.balance();
			System.out.println(eq);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
*/
}