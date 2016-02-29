/*
 * Represents a monatomic ion.
 * 
 * Author: Julia McClellan
 * Version: 2/29/2016
 */

package Equation;

import Elements.Element;

public class Monatomic extends Ions
{
	private Element element;
	
	public Monatomic(Element element)
	{
		super(1, 0);
		this.element = element;
	}
	
	public Monatomic(Element element, int num)
	{
		super(num, 0);
		this.element = element;
	}
	
	public Monatomic(Element element, int num, int charge)
	{
		super(num, charge);
		this.element = element;
	}
	
	/*
	 * A copy constructor.
	 */
	public Monatomic(Monatomic copy)
	{
		super(copy.getNum(), copy.getCharge());
		element = copy.getElement();
	}
	
	public Element getElement()
	{
		return element;
	}
	
	public Monatomic[] getElements()
	{
		Monatomic[] list = {this};
		return list;
	}
	
	public Monatomic[] getElements(boolean numbers)
	{
		return getElements();
	}

	public String toString() 
	{
		String ion = element.getSymbol();
		if(getNum() != 1) ion += "<sub>" + getNum() + "</sub>"; 
		if(getCharge() != 0) ion += "<sup>" + getCharge() + "</sup>";
		return ion;
	}
	
	public String withoutCharge()
	{
		String sym = element.getSymbol();
		if(getNum() != 1) sym += "<sub>" + getNum() + "</sub>";
		return sym;
	}

	public double getMolarMass()
	{
		return element.getMolarMass() * super.getNum();
	}

	public String getMolarMassSteps() 
	{
		return "(" + element.getMolarMass() + " * " + super.getNum() + ")";
	}
	
	/*
	 * Returns true if the element of each ion is the same.
	 */
	public boolean equals(Monatomic other)
	{
		return other.getElement().equals(element);
	}
	
	public boolean equals(Ions other)
	{
		return other instanceof Polyatomic ? false : equals((Monatomic)other);
	}
}