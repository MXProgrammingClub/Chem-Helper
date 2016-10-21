/*
 * File: Equation.java
 * Package: Equation
 * Version: 07/31/2016
 * Author: Luke Giacalone, Julia McClellan, Hyun Choi
 * --------------------------------------------------
 * Represents a chemical equation.
 */

package Equation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ChemHelper.InvalidInputException;
import Functions.Function;
import Functions.OxidationNumber;
import Elements.Element;
import Elements.Hydrogen;
import Elements.Oxygen;

public class Equation
{
	
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
	private ArrayList<Compound> left, right;
	private Set<Element> elements;
	private boolean equilibrium;
	
	public Equation()
	{
		left = new ArrayList<Compound>();
		right = new ArrayList<Compound>();
		populateElements();
	}
	
	public Equation(ArrayList<Compound> left, ArrayList<Compound> right)
	{
		this.left = left;
		this.right = right;
		populateElements();
	}
	
	public Equation(ArrayList<Compound> left, ArrayList<Compound> right, boolean equilibrium)
	{
		this.left = left;
		this.right = right;
		this.equilibrium = equilibrium;
		populateElements();
	}
	
	/**
	 * Creates a set of Element objects that contains all the types of Elements this Equation has.
	 */
	private void populateElements() {
		elements = new HashSet<Element>();
		for(Compound c: left) {
			elements.addAll(c.getElements());
		}
		for(Compound c: right) {
			elements.addAll(c.getElements());
		}
	}
	
	public boolean atEquilibrium()
	{
		return equilibrium;
	}
	
	public void setEquilibrium(boolean equilibrium)
	{
		this.equilibrium = equilibrium;
	}
	
	/**
	 * Returns the set of Elements that this equation contains.
	 * @return Set<Element> of Elements that this equation contains.
	 */
	public Set<Element> getElements() {
		return elements;
	}
	
	public void addToLeft(Compound c)
	{
		left.add(c);
		populateElements();
	}
	
	public void addToRight(Compound c)
	{
		right.add(c);
		populateElements();
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
		int index = eq.indexOf("\u2192");
		boolean equilibrium = false;
		if(index == -1)
		{
			index = eq.indexOf("\u21C6");
			equilibrium = true;
		}
		String left = eq.substring(0, index), right = eq.substring(index + 1);
		return new Equation(parseSide(left), parseSide(right), equilibrium);
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
		if(equilibrium)equation = equation.substring(0, equation.length() - 2) + "\u21C6 ";
		else equation = equation.substring(0, equation.length() - 2) + "\u2192 ";
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
	
	public String getEquilibrium(ArrayList<Compound> relevant, ArrayList<Compound> irrelevant, ArrayList<Integer> powers)
	{
		String num = getHalf(right, relevant, irrelevant, powers);
		String denom = getHalf(left, relevant, irrelevant, powers);
		
		if(num.length() == 0) return "\\text{K=}\\frac{1}{" + denom + "}";
		if(denom.length() == 0) return "\\text{K=}" + num;
		return "\\text{K=}\\frac{" + num + "}{" + denom + "}";
	}
	
	private String getHalf(ArrayList<Compound> compounds, ArrayList<Compound> relevant, ArrayList<Compound> irrelevant, ArrayList<Integer> powers)
	{
		String expression = "";
		int factor = powers.size() == 0 ? 1 : -1; //Determines if the compounds are on the left or right to determine whether the power should be negative
		for(Compound c: compounds)
		{
			if(c.getState().equals("aq") || c.getState().equals("g"))
			{
				expression += "[";
				for(Ions ion: c.getIons()) expression += Function.latex(ion);
				expression += "]";
				if(c.getNum() != 1) expression += "^{" + c.getNum() + "}";
				relevant.add(c);
				powers.add(c.getNum() * factor);
			}
			else irrelevant.add(c);
		}
		return expression;
	}
	
	/*
	 * Returns if the equation is a double displacement reaction with a solid product.
	 * -1 = not of the form
	 * 0 = spectators already removed
	 * 1 = spectators need to be removed
	 */
	public int isDoubleDisplacement()
	{
		boolean type1 = true;
		if(left.size() != 2 || right.size() != 2)
		{
			if(left.size() == 1 && right.size() == 2) type1 = false;
			else return -1;
		}
		ArrayList<Compound> compounds = new ArrayList<Compound>(left);
		compounds.addAll(right);
		boolean solid = false;
		for(Compound c: compounds)
		{
			if(type1 && c.getIons().length != 2) return -1;
			String state = c.getState();
			if(state.equals("s"))
			{
				if(solid) return -1;
				solid = true;
			}
			else if(!state.equals("aq")) return -1;
		}
		if(!solid) return -1;
		return type1 ? 1 : 0; //If it passed all tests, it should be double displacement
	}
	
	/*
	 * Removes spectator ions from the equation.
	 * pre: isDoubleDisplacement returns 1.
	 */
	public void removeSpectators()
	{
		Compound solid = null;
		for(Compound c: right)
		{
			if(c.getState().equals("s"))
			{
				solid = c;
				break;
			}
		}
		
		Ions[] ions = solid.getIons(), copyIons = new Ions[ions.length];
		int[][] numbers = OxidationNumber.findNumbers(ions, 0);
		for(int index = 0; index < ions.length; index++)
		{
			if(ions[index] instanceof Polyatomic)
			{
				copyIons[index] = new Polyatomic((Polyatomic)ions[index]);
				if(ions[index].getCharge() == 0)
				{
					int charge = 0;
					for(int num: numbers[index]) charge += num;
					ions[index].setCharge(charge);
				}
			}
			else
			{
				copyIons[index] = new Monatomic((Monatomic)ions[index]);
				copyIons[index].setCharge(numbers[index][0]);
			}
			copyIons[index].setNum(1);
		}
		
		left = new ArrayList<Compound>();
		left.add(solid);
		right = new ArrayList<Compound>();
		for(int index = 0; index < copyIons.length; index++)
		{
			right.add(new Compound(new Ions[]{copyIons[index]}, "aq", ions[index].getNum()));
		}
		populateElements();
	}
	
	/**
	 * Takes the equation and balances it by using a matrix.
	 * @return Whether the equation was balanced correctly.
	 * @throws InvalidInputException If there was a problem in the balancing.
	 */
	public int balance() throws InvalidInputException {
		if(isBalanced()) return 1;
		Matrix matrix = new Matrix(this);
		matrix.solve();
		int[] coefficients = matrix.getCoefficients();
		
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

	public int balanceRedox(boolean acidic, ArrayList<Equation> halves, ArrayList<String> steps)
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
		steps.add("Separate into half reactions:");
		steps.add("<html>" + r1 + " &nbsp &nbsp &nbsp " + r2 + "<html>");
		
		//Balance non-oxygen elements in half reactions
		boolean changed = false;
		int num1 = r1.getLeft().get(0).numberOf(e1), num2 = r1.getRight().get(0).numberOf(e1);
		if(num1 != num2)
		{
			r1.getLeft().get(0).setNum(num2);
			r1.getRight().get(0).setNum(num1);
			changed = true;
		}
		
		num1 = r2.getLeft().get(0).numberOf(e2);
		num2 = r2.getRight().get(0).numberOf(e2);
		if(num1 != num2)
		{
			r2.getLeft().get(0).setNum(num2);
			r2.getRight().get(0).setNum(num1);
			changed = true;
		}
		
		if(changed)
		{
			steps.add("Balance half reactions:");
			steps.add("<html>" + r1 + " &nbsp &nbsp &nbsp " + r2 + "<html>");
		}
		
		//Balance oxygen and hydrogen in half reactions
		int[] count1 = getCounts(r1, acidic), count2 = getCounts(r2, acidic);
		if(halves != null)
		{
			Equation half1 = createHalf(r1, count1, acidic), half2 = createHalf(r2, count2, acidic);
			steps.add("Balance hydrogen, oxygen, and electrons:");
			steps.add("<html>" + half1 + " &nbsp &nbsp &nbsp " + half2 + "<html>");
			//Adds the oxidation reaction and then the reduction reaction
			if(count1[2] < 0) halves.add(half1);
			halves.add(half2);
			if(count1[2] > 0) halves.add(half1);
		}
		
		//Multiply through by electron coefficients
		int abs2 = Math.abs(count2[2]), abs1 = Math.abs(count1[2]);
		count1[0] *= abs2;
		count1[1] *= abs2;
		for(Compound c: r1.getLeft()) c.setNum(c.getNum() * abs2);
		for(Compound c: r1.getRight()) c.setNum(c.getNum() * abs2);
		
		count2[0] *= abs1;
		count2[1] *= abs2;
		for(Compound c: r2.getLeft()) c.setNum(c.getNum() * abs1);
		for(Compound c: r2.getRight()) c.setNum(c.getNum() * abs1);
		
		count1[2] *= abs2;
		count2[2] = (count2[2] / abs2) * count1[2];
		steps.add("Multiply by electron coefficients:");
		steps.add("<html>" + createHalf(r1, count1, acidic) + " &nbsp &nbsp &nbsp " + createHalf(r2, count2, acidic) + "<html>");
		
		//Add in H2O and H+/OH-
		int[] counts = {count1[0] + count2[0], count1[1] + count2[1]};
		if(acidic && counts[1] > 0) left.add(0, new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 1, 1)}, counts[1]));
		if(!acidic && counts[1] > 0) left.add(0, new Compound(new Monatomic[]{new Monatomic(new Oxygen()), new Monatomic(new Hydrogen(), -1)}, counts[1]));
		if(counts[0] > 0) left.add(0, new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 2), new Monatomic(new Oxygen())}, counts[0]));
		
		if(acidic && counts[1] < 0) right.add(0, new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 1, 1)}, -counts[1]));
		if(!acidic && counts[1] < 0) right.add(0, new Compound(new Monatomic[]{new Monatomic(new Oxygen()), new Monatomic(new Hydrogen(), 1,-1)}, -counts[1]));
		if(counts[0] < 0) right.add(0, new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 2), new Monatomic(new Oxygen())}, -counts[0]));
		
		steps.add("Add reactions together:");
		steps.add("<html>" + toString() + "<html>");
		
		if(isBalanced()) return 1;
		return 0;
	}
	
	private int[] getCounts(Equation half, boolean acidic)
	{
		Oxygen o = new Oxygen();
		int[] counts = {-(half.getLeft().get(0).numberOf(o) * half.getLeft().get(0).getNum() - half.getRight().get(0).numberOf(o)) * 
				half.getRight().get(0).getNum(), 0, 0}; //{H2O, H+/OH-, e-}
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
		
		if(counts[0] > 0) e.addToLeft(new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 2), new Monatomic(new Oxygen())}, "l", counts[0]));
		if(acidic && counts[1] > 0) e.addToLeft(new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 1, 1)}, "aq", counts[1]));
		if(!acidic && counts[1] > 0) e.addToLeft(new Compound(new Monatomic[]{new Monatomic(new Oxygen()), new Monatomic(new Hydrogen(), 1, -1)}, "aq", 
				counts[1]));
		e.addToLeft(new Compound(half.getLeft().get(0).getIons(), half.getLeft().get(0).getState(), half.getLeft().get(0).getNum()));
		if(counts[2] > 0) e.addToLeft(new Compound(new Ions[]{new Electron()}, counts[2]));
		
		e.addToRight(new Compound(half.getRight().get(0).getIons(), half.getRight().get(0).getState(), half.getRight().get(0).getNum()));
		if(counts[0] < 0) e.addToRight(new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 2), new Monatomic(new Oxygen())}, "l", -counts[0]));
		if(acidic && counts[1] < 0) e.addToRight(new Compound(new Monatomic[]{new Monatomic(new Hydrogen(), 1, 1)}, "aq", -counts[1]));
		if(!acidic && counts[1] < 0) e.addToRight(new Compound(new Monatomic[]{new Monatomic(new Oxygen()), new Monatomic(new Hydrogen(), 1, -1)}, "aq", 
				-counts[1]));
		if(counts[2] < 0) e.addToRight(new Compound(new Ions[]{new Electron()}, -counts[2]));
		return e;
	}
	
	/**
	 * Takes this equation and counts the elements on either side to see if the equation is balanced
	 * @return Whether the equation is balanced or not.
	 */
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
