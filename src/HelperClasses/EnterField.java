/*
 * A field to enter information with some sort of text field and some number of combo boxes for units.
 * 
 * Authors: Luke Giacalone and Julia McClellan
 * Version: 2/8/2016
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

import Functions.Function;

public class EnterField extends JPanel
{
	private Component amount;
	private JComboBox<String> unit, unit2;
	private String name, type, type2;
	private boolean isCompoundUnit, hasCompoundField;
	
	public EnterField(String name, String unitType, String unitType2, boolean hasCompoundField)
	{
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		
		this.setSize(300, this.getHeight());
		this.name = name;
		this.hasCompoundField = hasCompoundField;
		if(!hasCompoundField) amount = new JTextField(6);
		else amount = new TextField(TextField.COMPOUND);
		if(unitType != null) {
			if(unitType.equals("Moles")) unitType = "Amount";
			type = unitType;
			unit = new JComboBox<String>(Units.getUnits(unitType));
			if(unit.getItemCount() == Units.POWERS.length) unit.setSelectedIndex(6);
			else unit.setSelectedIndex(0);
			unit.setPreferredSize(new Dimension(76, 28));
		}
		isCompoundUnit = false;
		if(unitType2 != null) {
			if(unitType2.equals("Moles")) unitType2 = "Amount";
			type2 = unitType2;
			unit2 = new JComboBox<String>(Units.getUnits(unitType2));
			if(unit2.getItemCount() == Units.POWERS.length) unit2.setSelectedIndex(6);
			else unit2.setSelectedIndex(0);
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
		else if(unitType != null)
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
	
	public EnterField(String name, String unitType) {
		this(name, unitType, null, false);
	}
	
	public EnterField(String name, String unitType, boolean hasCompoundField)
	{
		this(name, unitType, null, hasCompoundField);
	}
	
	public EnterField(String name, String unitType, String unitType2) {
		this(name, unitType, unitType2, false);
	}
	
	public void setAmount(double newAmount)
	{
		if(!hasCompoundField) ((JTextField)amount).setText("" + newAmount);
	}
	
	//returns the double amount of the box- converts to the standard unit
	public double getAmount() 
	{
		try 
		{
			double value;
			if(!hasCompoundField) value = Double.parseDouble(((JTextField)amount).getText());
			else value = Double.parseDouble(((TextField)amount).getText());
			if(type != null) value = Units.toStandard(value, unit.getSelectedIndex(), type);
			if(type2 != null) value =  Units.fromStandard(value, unit2.getSelectedIndex(), type2); //This is in the denominator, so the conversion is reversed.
			return value;
		}
		catch(Throwable e) {
			if(e.getMessage().equals("empty String")) return Units.UNKNOWN_VALUE;
			return Units.ERROR_VALUE;
		}
	}
	
	public double getBlankAmount(double amount)
	{
		if(type != null) amount = Units.fromStandard(amount, unit.getSelectedIndex(), type);
		if(type2 != null) amount = Units.toStandard(amount, unit2.getSelectedIndex(), type2);
		return amount;
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
	
	public String getUnit2Name() {
		return unit2.getSelectedItem().toString();
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
	
	public int getSigFigs()
	{
		if(isEmpty()) return -1;
		return Function.sigFigs(getText());
	}
	
	public String getName() 
	{
		return name;
	}
}