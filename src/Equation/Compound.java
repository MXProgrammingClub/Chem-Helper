/*
 * File: Compound.java
 * Name: Luke Giacalone --Julia did a very minimal portion of this class; one might call it insignificant
 * Date: 11/11/1111
 * -----------------------
 * This represents something which Julia has not told me. Clearly I did in fact write this entire class.
 */

package Equation;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import ChemHelper.InvalidInputException;
import Elements.Element;

public class Compound 
{
	private Ions[] ions;
	private String state;
	private int num;

	private static final Set<String> VALID_STATES = createSet();

	public Compound(Ions[] ions) {
		this.ions = ions;
		this.state = " ";
		this.num = 1;
	}

	public Compound(Ions[] ions, String state)
	{
		this.ions = ions;
		if(VALID_STATES.contains(state)) this.state = state;
		else this.state = " ";
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
		for(String str: VALID_STATES)
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

	public void setState(String newState)
	{
		if(VALID_STATES.contains(newState)) state = newState;
		else state = " "; //If it can't be the parameter, it should at least not be what it was before.
	}
	
	public int getNum() 
	{
		return num;
	}

	public void setNum(int num) 
	{
		this.num = num;
	}

	public int getCharge()
	{
		int charge = 0;
		for(Ions i: ions) charge += i.getCharge();
		return charge;
	}
	
	public static Set<String> getValidstates() 
	{
		return VALID_STATES;
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

	public String withoutNum()
	{
		String str = "";
		for(Ions ion: ions)
		{
			str += ion;
		}
		if(!state.equals(" ")) str += "(" + state + ")";
		return str;
	}
	
	public String withoutNumState()
	{
		String str = "";
		for(Ions ion: ions)
		{
			str += ion;
		}
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
	
	public int numberOf(Element e)
	{
		int count = 0;
		for(Ions i: ions)
		{
			count += i.numberOf(e);
		}
		return count;
	}

	public Monatomic[] getNoPoly()
	{
		ArrayList<Monatomic> list = new ArrayList<Monatomic>();
		for(Ions i: ions)
		{
			for(Monatomic m: i.getElements())
			{
				int index = list.indexOf(m);
				if(index == -1) list.add(new Monatomic(m));
				else
				{
					Monatomic element = list.get(index);
					element.setNum(element.getNum() + m.getNum());
				}
			}
		}
		Monatomic[] array = new Monatomic[list.size()];
		return list.toArray(array);
	}

	public static Compound parseCompound(String cmp) throws InvalidInputException
	{
		int index = 0, coefficient = 0;
		for(; index < cmp.length() && Character.isDigit(cmp.charAt(index)); index++)
		{
			coefficient = 10 * coefficient + (char)Math.abs('0' - cmp.charAt(index));
		}
		if(coefficient == 0) coefficient = 1;

		if(cmp.substring(index, cmp.length()).equals("e<sup>-</sup>"))
		{
			return new Compound(new Ions[]{new Electron()}, coefficient);
		}
		
		String state = "";
		if(cmp.charAt(cmp.length() - 1) == ')')
		{
			state = cmp.substring(cmp.lastIndexOf('(') + 1, cmp.length() - 1);
			if(VALID_STATES.contains(state)) cmp = cmp.substring(0, cmp.lastIndexOf("("));
			else state = " ";
		}
		
		ArrayList<Ions> ions = new ArrayList<Ions>();
		String temp = "";
		boolean mid = false;
		for(; index < cmp.length(); index++) 
		{
			char ch = cmp.charAt(index);
			if(!mid)
			{
				if(Character.isUpperCase(ch))
				{
					if(temp.length() > 0) ions.add(Ions.parseIons(temp));
					temp = "" + ch;
				}
				else if(ch == '(')
				{
					if(!temp.equals("")) ions.add(Ions.parseIons(temp));
					temp = "(";
					mid = true;
				}
				else temp += ch;
			}
			else
			{
				if(ch == ')') mid = false;
				temp += ch;
			}
		}
		ions.add(Ions.parseIons(temp));
		Ions[] i = new Ions[ions.size()];
		return new Compound(ions.toArray(i), state, coefficient);
	}

	private static TreeSet<String> createSet()
	{
		TreeSet<String> set = new TreeSet<String>();
		set.add("g");
		set.add("l");
		set.add("s");
		set.add("aq");
		return set;
	}
}