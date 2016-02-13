/*
 * A class that uses two EnterFields to make: beforeValue -> afterValue
 * 
 * Author: Luke Giacalone and Julia McClellan
 * Version: 2/12/2016
 */

package HelperClasses;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DoubleEnterField extends JPanel {
	private String name;
	private boolean isLeft;
	private EnterField before;
	private EnterField after;

	public DoubleEnterField(String name, boolean isLeft, String type1, String type2) {
		this.name = name;
		this.isLeft = isLeft;
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		before = new EnterField(name, type1, type2);
		this.add(before, c);

		c.gridx = 1;
		this.add(new JLabel("\u2192"), c);

		c.gridx = 2;
		after = new EnterField(null, type1, type2);
		after.remove(0); //removing the empty space where label is
		this.add(after, c);
	}
	
	public DoubleEnterField(String name, boolean isLeft, String type)
	{
		this(name, isLeft, type, null);
	}

	public double getBeforeValue()
	{
		try
		{
			double value = before.getAmount();
			if(!isLeft) value = 1 / value;
			return value;
		}
		catch(Throwable e)
		{
			return Units.ERROR_VALUE;
		}
	}

	public void setBeforeValue(double value)
	{
		before.setText("" + value);
	}

	public void setAfterValue(double value)
	{
		after.setText("" + value);
	}

	public double getAfterValue() 
	{
		try 
		{
			double value = after.getAmount();
			if(value != Units.UNKNOWN_VALUE && value != Units.ERROR_VALUE && !isLeft) value = 1 / value;
			return value;
		}
		catch(Throwable e) 
		{
			if(e.getMessage().equals("empty String")) return Units.UNKNOWN_VALUE;
			return Units.ERROR_VALUE;
		}
	}

	public String getDesiredUnit()
	{
		String unit = after.getUnitName();
		try
		{
			return unit + " / " + after.getUnit2Name();
		}
		catch(Throwable e)
		{
			return unit;
		}
	}

	public String getName()
	{
		return name;
	}

	public boolean isLeft()
	{
		return isLeft;
	}
	
	public double getBlankAmount(double amount)
	{
		return after.getBlankAmount(amount);
	}
	
	public int getSigFigs()
	{
		int sigfigs1 = before.getSigFigs(), sigfigs2 = after.getSigFigs();
		if(after.getSigFigs() != -1) return Math.min(sigfigs1, sigfigs2);
		return sigfigs1;
	}
	
	public String getStandardUnit()
	{
		return before.getStandardUnit();
	}
}