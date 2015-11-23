package ChemHelper;

import java.util.ArrayList;

import Elements.Compound;
import Functions.Function;

public class Equation
{
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
	
	public boolean balance()
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
	}
	
	/*
	 * Balances the equation if the right side is one compound and the left side compounds all contain exactly one element.
	 */
	private boolean balance1()
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
	}
	
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