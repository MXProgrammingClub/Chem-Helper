package HelperClasses;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
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
	
	public EnterField(String name, String[] units)
	{
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		
		this.setSize(300, this.getHeight());
		this.name = name;
		amount = new JTextField(5);
		unit = new JComboBox<String>(units);
		unit.setSelectedIndex(0);
		unit.setPreferredSize(new Dimension(75, 28));
		
		JLabel label = new JLabel(name);
		label.setPreferredSize(new Dimension(80, 16));
		
		c.gridx = 0;
		add(label, c);
		c.gridx = 1;
		add(amount, c);
		c.gridx = 2;
		add(unit, c);
	}
	
	public void setAmount(double newAmount)
	{
		amount.setText("" + newAmount);
	}
	
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

