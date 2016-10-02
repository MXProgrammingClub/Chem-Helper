/*
 * An enterfield with radio buttons to choose between sets of units.
 * 
 * Author: Julia McClellan
 * Version: 2/14/16
 */

package HelperClasses;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Functions.Function;

public class RadioEnterField extends JPanel
{
	private JTextField text;
	private JComboBox<String> unit1, unit2;
	private JRadioButton one, two;
	private String type1, type2;
	private JCheckBox check;
	
	public RadioEnterField(String name, boolean textfield, String unitName1, String unitName2, boolean checkbox)
	{
		if(checkbox)
		{
			check = new JCheckBox();
			check.setSelected(true);
			add(check);
		}
		
		add(new JLabel(name));
		
		if(textfield)
		{
			text = new JTextField(6);
			add(text);
		}
		
		unit1 = new JComboBox<String>(Units.getUnits(unitName1));
		if(unit1.getItemCount() == Units.POWERS.length) unit1.setSelectedIndex(6);
		unit2 = new JComboBox<String>(Units.getUnits(unitName2));
		if(unit2.getItemCount() == Units.POWERS.length) unit2.setSelectedIndex(6);
		type1 = unitName1;
		type2 = unitName2;
		
		one = new JRadioButton();
		one.setSelected(true);
		two = new JRadioButton();
		ButtonGroup g = new ButtonGroup();
		g.add(one);
		g.add(two);
		
		add(one);
		add(unit1);
		add(two);
		add(unit2);
	}
	
	public boolean unit1()
	{
		return one.isSelected();
	}
	
	public double getAmount()
	{
		if(text == null || text.getText().equals("")) return Units.UNKNOWN_VALUE;
		try
		{
			return Units.toStandard(Double.parseDouble(text.getText()), one.isSelected() ? unit1.getSelectedIndex() : unit2.getSelectedIndex(), 
					one.isSelected() ? type1 : type2);
		}
		catch(Throwable e)
		{
			return Units.ERROR_VALUE;
			}
	}
	
	public String getUnit()
	{
		return one.isSelected() ? (String)unit1.getSelectedItem() : (String)unit2.getSelectedItem();
	}
	
	public double getBlankAmount(double amount)
	{
		return one.isSelected() ? Units.fromStandard(amount, unit1.getSelectedIndex(), type1) : Units.fromStandard(amount, unit2.getSelectedIndex(), type2);
	}
	
	public int getSigFigs()
	{
		return Function.sigFigs(text.getText());
	}
	
	public void setText(String str)
	{
		text.setText(str);
	}
	
	public boolean isSelected()
	{
		return check == null ? false : check.isSelected();
	}
	
	public boolean isEmpty()
	{
		return text.getText().trim().equals("");
	}
}