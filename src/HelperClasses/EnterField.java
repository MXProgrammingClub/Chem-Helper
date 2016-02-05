/*
 * A field to enter information with some sort of text field and some number of combo boxes for units.
 * 
 * Authors: Luke Giacalone and Julia McClellan
 * Version: 1/31/2016
 */

package HelperClasses;

import java.awt.Component;
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
	
	private Component amount;
	private JComboBox<String> unit, unit2;
	private String name;
	private boolean isCompoundUnit, hasCompoundField;
	
	public EnterField(String name, String[] units, String[] units2, boolean hasCompoundField)
	{
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		
		this.setSize(300, this.getHeight());
		this.name = name;
		this.hasCompoundField = hasCompoundField;
		if(!hasCompoundField) amount = new JTextField(6);
		else amount = new TextField(TextField.COMPOUND);
		if(units != null) {
			unit = new JComboBox<String>(units);
			unit.setSelectedIndex(0);
			unit.setPreferredSize(new Dimension(76, 28));
		}
		isCompoundUnit = false;
		if(units2 != null) {
			unit2 = new JComboBox<String>(units2);
			unit2.setSelectedIndex(0);
			unit2.setPreferredSize(new Dimension(76, 28));
			isCompoundUnit = true;
		}
		
		JLabel label = new JLabel(name);
		label.setPreferredSize(new Dimension(80, 16));
		
		c.gridx = 0;
		c.gridy = 0;
		add(label, c);
		c.gridx = 1;
		add(amount, c);
		c.gridx = 2;
		
		if(isCompoundUnit) {
			add(unit, c);
			c.gridx = 3;
			add(new JLabel(" / "), c);
			c.gridx = 4;
			add(unit2, c);
		}
		else if(units != null)
			add(unit, c);
		else {
			JLabel temp = new JLabel("");
			temp.setPreferredSize(new Dimension(75, 28));
			add(temp); //same width as unit would be to make a space
		}
	}
	
	public EnterField(String name) {
		this(name, null, null, false);
	}
	
	public EnterField(String name, boolean hasCompoundField) {
		this(name, null, null, hasCompoundField);
	}
	
	public EnterField(String name, String[] units) {
		this(name, units, null, false);
	}
	
	public EnterField(String name, String[] units, String[] units2) {
		this(name, units, units2, false);
	}
	
	public void setAmount(double newAmount)
	{
		if(!hasCompoundField) ((JTextField)amount).setText("" + newAmount);
	}
	
	//returns the double amount of the box
	public double getAmount() 
	{
		try 
		{
			double value;
			if(!hasCompoundField) value = Double.parseDouble(((JTextField)amount).getText());
			else value = Double.parseDouble(((TextField)amount).getText());
			return value;
		}
		catch(Throwable e) {
			if(e.getMessage().equals("empty String")) return UNKNOWN_VALUE;
			return ERROR_VALUE;
		}
	}
	
	//returns the string amount of the box
	public String getText() {
		if(!hasCompoundField) return ((JTextField)amount).getText();
		else return ((TextField)amount).getText();
	}
	
	public void setText(String text) {
		if(!hasCompoundField) 
			((JTextField)amount).setText(text);
		else
			((TextField)amount).setText(text);
	}
	
	public void setUnit(int index)
	{
		try {
			unit.setSelectedIndex(index);
		}
		catch(Throwable e) {}
	}
	
	public void setUnit2(int index) 
	{
		try {
			unit2.setSelectedIndex(index);
		}
		catch(Throwable e) {}
	}
	
	public int getUnit() 
	{
		try {
			return unit.getSelectedIndex();
		}
		catch(Throwable e) {}
		return -1;
	}
	
	public String getUnitName() {
			return unit.getSelectedItem().toString();
	}
	
	public int getUnit2() 
	{
		try {
			return unit2.getSelectedIndex();
		}
		catch(Throwable e) {}
		return -1;
	}
	
	public boolean isEmpty() {
		return getText().isEmpty();
	}
	
	public String getName() 
	{
		return name;
	}
}