/*
 * Represents a monatomic ion.
 * 
 * Author: Julia McClellan
 * Version: 2/2/2016
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
	
	public Element getElement()
	{
		return element;
	}
	
	public Monatomic[] getElements()
	{
		Monatomic[] list = {this};
		return list;
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
}