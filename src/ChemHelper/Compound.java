/*
 * File: Compound.java
 * Name: Luke Giacalone --Julia did a very minimal portion of this class; one might call it insignificant
 * Date: 11/11/1111
 * -----------------------
 * This represents something which Julia has not told me. Clearly I did in fact write this entire class.
 */

package ChemHelper;

import java.util.ArrayList;
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
	
	public String withoutCharge()
	{
		String str = "";
		if(num != 1) str += num;
		for(Ions ion: ions)
		{
			str += ion.withoutCharge();
		}
		if(!state.equals(" ")) str += "(" + state + ")";
		return str;
	}
	
	public double getMolarMass()
	{
		double total = 0;
		for(Ions ion: ions)
		{
			total += ion.getMolarMass();
		}
		return total;
	}
	
	/*
	 * Returns a String explaining how to find the molar mass of the compound.
	 */
	public String getMolarMassSteps()
	{
		String instruction = "Multiply the molar mass of each element by its coefficient: ";
		double total = 0;
		for(Ions ion: ions)
		{
			instruction += ion.getMolarMassSteps() + " + ";
			total += ion.getMolarMass();
		}
		instruction = instruction.substring(0, instruction.length() - 3) + " = " + total + " g/mol";
		return instruction;
	}
	
	public boolean contains(Element e)
	{
		for(Ions thisOne: ions)
		{
			if(thisOne instanceof Monatomic && ((Monatomic)thisOne).getElement().equals(e)) return true;
			else if(thisOne instanceof Polyatomic && ((Polyatomic)thisOne).contains(e)) return true;
		}
		return false;
	}
	
	public int indexOf(Element e)
	{
		for(int index = 0; index < ions.length; index++)
		{
			if(ions[index] instanceof Monatomic && ((Monatomic)ions[index]).getElement().equals(e)) return index;
			else if(ions[index] instanceof Polyatomic && ((Polyatomic)ions[index]).contains(e)) return index;
		}
		return -1;
	}
	
	public Monatomic[] getNoPoly()
	{
		ArrayList<Monatomic> ionList = new ArrayList<Monatomic>();
		for(Ions ion: ions)
		{	
			for(Monatomic mon: ion.getElements())
			{
				int index = -1, factor = 1;
				Element e = mon.getElement();
				for(int i = 0; i < ionList.size(); i++)
				{
					if(e.equals(ionList.get(i).getElement()))
					{
						index = i;
						break;
					}
				}
				if(ion instanceof Polyatomic) factor = ion.getNum();
				if(index != -1) ionList.get(index).setNum(ionList.get(index).getNum() + mon.getNum() * factor);
				else
				{
					Monatomic temp = new Monatomic(mon.getElement(), mon.getNum() * factor, mon.getCharge());
					ionList.add(temp);
				}
			}
		}
		Monatomic[] array = new Monatomic[ionList.size()];
		return ionList.toArray(array);
	}
	
	public static Compound parseCompound(String cmp) throws InvalidInputException
	{
		int stateIndex = cmp.lastIndexOf("("), num = 0;
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
			if(state.indexOf("/") == -1) cmp = cmp.substring(0, stateIndex);
			else state = "";
		}
		ArrayList<Ions> ions = new ArrayList<Ions>();
		while(cmp.length() > 0)
		{
			int end = cmp.indexOf("/"), poly = cmp.indexOf('(');
			if(poly != -1 && poly < end) end = cmp.indexOf('/', cmp.indexOf(')'));
			if(end == -1) end = cmp.length();
			ions.add(Ions.parseIons(cmp.substring(0, end)));
			if(end != cmp.length()) cmp = cmp.substring(end + 1);
			else cmp = "";
		}
		Ions[] array = new Ions[ions.size()];
		return new Compound(ions.toArray(array), state, num);
	}
}