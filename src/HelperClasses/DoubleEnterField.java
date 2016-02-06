/*
 * A class that uses two EnterFields to make: beforeValue -> afterValue
 * 
 * Author: Luke Giacalone and Julia McClellan
 * Version: 2/5/2016
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

	public DoubleEnterField(String name, boolean isLeft, String type) {
		this.name = name;
		this.isLeft = isLeft;
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		before = new EnterField(name, type);
		this.add(before, c);

		c.gridx = 1;
		this.add(new JLabel("\u2192"), c);

		c.gridx = 2;
		after = new EnterField(null, type);
		after.remove(0); //removing the empty space where label is
		this.add(after, c);
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
			if(!isLeft) value = 1 / value;
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
		return (String) after.getUnitName();
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
}