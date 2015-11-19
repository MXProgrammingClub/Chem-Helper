/*
 * File: Compound.java
 * Name: Luke Giacalone --Julia did a very minimal portion of this class; one might call it insignificant
 * Date: 11/11/1111
 * -----------------------
 * This represents something which Julia has not told me. Clearly I did in fact write this entire class.
 */

package Elements;

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
	
	public static Compound parseCompound(String cmp) throws InvalidInputException
	{
		int stateIndex = cmp.indexOf("("), num;
		try
		{
			num = Integer.parseInt(cmp.substring(0, 1));
			cmp = cmp.substring(1);
		}
		catch(NumberFormatException e)
		{
			num = 1;
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