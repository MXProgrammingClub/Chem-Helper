package HelperClasses;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EnterField extends JPanel
{
	
	private static final int UNKNOWN_VALUE = -500, ERROR_VALUE = -501; // Values which none of the entered values could be.
	
	private JTextField amount;
	private JComboBox<String> unit;
	private String name;
	private boolean isString;
	
	public EnterField(String name, String[] units) //if units == null, then no units displayed
	{
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		
		this.setSize(300, this.getHeight());
		this.name = name;
		amount = new JTextField(5);
		if(units != null) {
			unit = new JComboBox<String>(units);
			unit.setSelectedIndex(0);
			unit.setPreferredSize(new Dimension(75, 28));
		}
		
		JLabel label = new JLabel(name);
		label.setPreferredSize(new Dimension(80, 16));
		
		c.gridx = 0;
		add(label, c);
		c.gridx = 1;
		add(amount, c);
		c.gridx = 2;
		
		isString = false;
		if(units != null)
			add(unit, c);
		else {
			JLabel temp = new JLabel("");
			temp.setPreferredSize(new Dimension(75, 28));
			add(temp); //same width as unit would be to make a space
			isString = true;
		}
	}
	
	public void setAmount(double newAmount)
	{
		amount.setText("" + newAmount);
	}
	
	//returns the double amount of the box
	public double getAmount()
	{
		try
		{
			double value = Double.parseDouble(amount.getText());
			return value;
		}
		catch(Throwable e)
		{
			if(e.getMessage().equals("empty String")) return UNKNOWN_VALUE;
			return ERROR_VALUE;
		}
	}
	
	//returns the string amount of the box
	public String getText() {
		return amount.getText();
	}
	
	public void setUnit(int index)
	{
		unit.setSelectedIndex(index);
	}
	
	public int getUnit()
	{
		return unit.getSelectedIndex();
	}
	
	public String getName()
	{
		return name;
	}
}

