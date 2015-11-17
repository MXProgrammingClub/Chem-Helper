package Elements;

import java.util.ArrayList;

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
	
	public static Equation parseEquation(String eq)
	{
		String left, right;
		left = eq.substring(0, eq.indexOf("="));
		right = eq.substring(eq.indexOf("=") + 1);
		return new Equation(parseSide(left), parseSide(right));
	}
	
	private static ArrayList<Compound> parseSide(String side)
	{
		ArrayList<Compound> compounds = new ArrayList<Compound>();
		while(side.indexOf("+") != -1)
		{
			compounds.add(Compound.parseCompound(side.substring(0, side.indexOf("+"))));
			side = side.substring(side.indexOf("+") + 1);
		}
		System.out.println(side);
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
	
	public static void main(String[] args)
	{
		String string = "2Na^-1+2Cl^-1=2Na^-1/Cl^1";
		System.out.println(parseEquation(string));
	}
}