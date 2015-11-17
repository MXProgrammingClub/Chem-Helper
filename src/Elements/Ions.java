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
		String str = "<html>" + element.getSymbol();
		if(charge != 0) str += "<sup>" + charge + "</sup>";
		str += "<sub>" + num + "</sub></html>";
		return str;
	}
	
	public static Ions parseIons(String ions)
	{
		int symEnd = ions.indexOf("^");
		boolean isCharge = true, isNum = true;;
		if(symEnd == -1)
		{
			symEnd = ions.indexOf(".");
			isCharge = false;
		}
		if(symEnd == -1)
			symEnd = ions.length() + 1;
		String symbol = ions.substring(0, symEnd);
		Element e = PeriodicTable.find(symbol);
		
		int chargeEnd = ions.indexOf("."), charge;
		if(chargeEnd == -1) 
		{ 
			chargeEnd = ions.length() + 1;
			isNum = false;
		}
		if(!isCharge) charge = 0;
		else charge = Integer.parseInt(ions.substring(symEnd + 1, chargeEnd));
		
		int num;
		if(!isNum) num = 0;
		else num = Integer.parseInt(ions.substring(chargeEnd) + 1);
		
		return new Ions(e, charge, num);
	}
}