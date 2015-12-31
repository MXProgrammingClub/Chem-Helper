/*
 * File: Compound.java
 * Name: Luke Giacalone --Julia did a very minimal portion of this class; one might call it insignificant
 * Date: 11/11/1111
 * -----------------------
 * This represents something which Julia has not told me. Clearly I did in fact write this entire class.
 */

package ChemHelper;

import Elements.Element;

public class Compound 
{
	private Ions[] ions;
	private String state;
	private int num;
	public static final String[] validStates = {"l", "g", "s", "aq", " "};
	
	public Compound(Ions[] ions) {
		this.ions = ions;
		this.state = " ";
		this.num = 1;
	}
	
	public Compound(Ions[] ions, String state)
	{
		this.ions = ions;
		boolean valid = false;
		for(String str: validStates)
		{
			if(str.equals(state))
			{
				valid = true;
				break;
			}
		}
		if(!valid) this.state = " ";
		else this.state = state;
		this.num = 1;
	}
	
	public Compound(Ions[] ions, int num)
	{
		this.ions = ions;
		this.state = " ";
		this.num = num;
	}
	
	public Compound(Ions[] ions, String state, int num)
	{
		this.ions = ions;
		boolean valid = false;
		for(String str: validStates)
		{
			if(str.equals(state))
			{
				valid = true;
				break;
			}
		}
		if(!valid) this.state = " ";
		else this.state = state;
		this.num = num;
	}
	
	public String getState()
	{
		return state;
	}
	
	public int getNum() 
	{
		return num;
	}

	public void setNum(int num) 
	{
		this.num = num;
	}

	public static String[] getValidstates() 
	{
		return validStates;
	}
	
	public Ions[] getIons()
	{
		return ions;
	}

	public String toString()
	{
		String str = "";
		if(num != 1) str += num;
		for(Ions ion: ions)
		{
			str += ion;
		}
		if(!state.equals(" ")) str += "(" + state + ")";
		return str;
	}
	
	public double getMolarMass()
	{
		double total = 0;
		for(Ions ion: ions)
		{
			total += ion.getNum() * ion.getElement().getMolarMass();
		}
		return total;
	}
	
	/*
	 * Returns a String explaining how to find the molar mass of the compound.
	 */
	public String getMolarMassSteps()
	{
		String instruction = "Multiply the molar mass of each element by its coefficient:";
		double total = 0;
		for(Ions ion: ions)
		{
			instruction += " (" + ion.getNum() + " * " + ion.getElement().getMolarMass() + " g/mol) +";
			total += ion.getNum() * ion.getElement().getMolarMass();
		}
		instruction = instruction.substring(0, instruction.length() - 1) + " = " + total + " g/mol";
		return instruction;
	}
	
	public boolean contains(Element e)
	{
		for(Ions thisOne: ions)
		{
			if(thisOne.getElement().equals(e)) return true;
		}
		return false;
	}
	
	public int indexOf(Element e)
	{
		for(int index = 0; index < ions.length; index++)
		{
			if(ions[index].getElement().equals(e)) return index;
		}
		return -1;
	}
	
	public static Compound parseCompound(String cmp) throws InvalidInputException
	{
		int stateIndex = cmp.indexOf("("), num = 0;
		try
		{
			while(true)
			{
				int next = Integer.parseInt(cmp.substring(0, 1));
				num = num * 10 + next;
				cmp = cmp.substring(1);
			}
		}
		catch(NumberFormatException e)
		{
			if(num == 0) num = 1;
		}
		String state = "";
		if(stateIndex != -1)
		{
			state = cmp.substring(stateIndex + 1, cmp.length() - 1);
			cmp = cmp.substring(0, stateIndex);
		}
		int index = 0, count = 1;
		while(cmp.indexOf("/", index) != -1)
		{
			count++;
			index = cmp.indexOf("/", index) + 1;
		}
		Ions[] ions = new Ions[count];
		for(int ion = 0; ion < ions.length; ion++)
		{
			int end = cmp.indexOf("/");
			if(end == -1) end = cmp.length();
			ions[ion] = Ions.parseIons(cmp.substring(0, end));
			if(end != cmp.length()) cmp = cmp.substring(end + 1);
		}
		return new Compound(ions, state, num);
	}
}