package Elements;

import Functions.PeriodicTable;

public class Ions 
{
	private int num, charge;
	private Element element;
	
	public Ions(Element element)
	{
		this.element = element;
		num = 1;
		charge = 0;
	}
	
	public Ions(Element element, int num)
	{
		this.element = element;
		this.num = num;
		charge = 0;
	}
	
	public Ions(Element element, int num, int charge)
	{
		this.element = element;
		this.num = num;
		this.charge = charge;
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
		return charge;
	}

	public void setCharge(int charge) 
	{
		this.charge = charge;
	}

	public Element getElement() 
	{
		return element;
	}
	
	public String toString()
	{
		String str = element.getSymbol();
		if(charge != 0) 
		{
			str += "<sup>";
			if(charge < 0) str += charge;
			else str += "+" + charge;
			str += "</sup>";
		}
		if(num != 1) str += "<sub>" + num + "</sub>";
		return str;
	}

	public static Ions parseIons(String ions) throws InvalidInputException
	{
		int symEnd = ions.indexOf("^");
		boolean isCharge = true, isNum = true;
		if(symEnd == -1)
		{
			symEnd = ions.indexOf(".");
			isCharge = false;
		}
		if(symEnd == -1)
			symEnd = ions.length();
		String symbol = ions.substring(0, symEnd);
		Element e = PeriodicTable.find(symbol);
		if(e == null) throw new InvalidInputException(0);
		int chargeEnd = ions.indexOf("."), charge;
		if(chargeEnd == -1) 
		{ 
			chargeEnd = ions.length();
			isNum = false;
		}
		if(!isCharge) charge = 0;
		else
		{
			try
			{
				charge = Integer.parseInt(ions.substring(symEnd + 1, chargeEnd));
			}
			catch(NumberFormatException e1)
			{
				throw new InvalidInputException(1);
			}
		}
		
		int num;
		if(!isNum) num = 1;
		else
		{
			try
			{
				num = Integer.parseInt(ions.substring(chargeEnd + 1));
			}
			catch(NumberFormatException e1)
			{
				throw new InvalidInputException(1);
			}
		}
		return new Ions(e, num, charge);
	}
}