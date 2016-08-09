package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import HelperClasses.EnterField;
import HelperClasses.Units;

/**
 * File: IdealGas.java
 * Package: Functions
 * Version: 08/09/2016
 * Authors: Julia McClellan and Luke Giacalone
 * -----------------------------------------------
 * Calculates missing value of PV=nRT when the others are given and shows calculation steps. Has static methods to perform conversions from kPa or torr to atm
 * and from C or F to K.
 * number() returns true- saves the latest calculated value, can use saved for P, V, n, or T.
 */
public class IdealGas extends Function 
{
	private static final int UNKNOWN_VALUE = -500, ERROR_VALUE = -501; // Values which none of the entered values could be.
	private static final String[] VALUES = {"Pressure", "Volume", "Moles", "Temperature"};
	
	private JPanel panel;
	private JButton calculate;
	private JCheckBox stp;
	private EnterField[] values;
	private JLabel result;
	private Box steps;
	private double save;
	
	public IdealGas()
	{
		super("Ideal Gas Law");
		
		Box valueBox = Box.createVerticalBox();
		values = new EnterField[VALUES.length];
		for(int index = 0; index < values.length; index++)
		{
			EnterField field = new EnterField(VALUES[index], VALUES[index]);
			values[index] = field;
			valueBox.add(field);
		}
		
		stp = new JCheckBox("STP");
		stp.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						if(stp.isSelected())
						{
							values[0].setAmount(Units.STANDARD_PRESSURE);
							values[0].setUnit(0);
							values[0].setEnabled(false);
							values[3].setAmount(Units.STANDARD_TEMPERATURE);
							values[3].setUnit(0);
							values[3].setEnabled(false);
						}
						else {
							values[0].setEnabled(true);
							values[3].setEnabled(true);
						}
					}
				});
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		
		result = new JLabel();
		Box box = Box.createVerticalBox();
		box.add(new JLabel("Enter known information and select desired unit for the unknown quantity."));
		box.add(valueBox);
		box.add(stp);
		box.add(calculate);
		box.add(result);
		
		steps = Box.createVerticalBox();
		
		panel = new JPanel();
		panel.add(box);
		panel.add(steps);
		
		save = 0;
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return save;
	}
	
	public void useSavedNumber(double num)
	{
		String selected = (String)JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, VALUES, "Pressure");
		if(selected == null) return;
		for(int index = 0; index < VALUES.length; index++)
		{
			if(VALUES[index].equals(selected))
			{
				values[index].setAmount(num);
				break;
			}
		}
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			steps.removeAll();
			steps.setVisible(false);
			steps.add(Function.latex("\\text{PV=nRT}"));
			steps.add(Box.createVerticalStrut(5));
			double[] quantities = new double[4];
			int blank = -1, sigFigs = Integer.MAX_VALUE;
			for(int index = 0; index < values.length; index++)
			{
				quantities[index] = values[index].getAmount();
				if(quantities[index] == ERROR_VALUE)
				{
					result.setText("An entered value was not a number.");
					return;
				}
				if(quantities[index] == UNKNOWN_VALUE)
				{
					if(blank == -1)
					{
						steps.add(Function.latex("\\text{" + values[index].getName() + "} = " + "\\text{? }" + values[index].getUnitName()));
						blank = index;
					}
					else
					{
						result.setText("Only one value can be left blank.");
						return;
					}
				}
				else
				{
					if(!stp.isSelected() || index != 0 && index != 3) sigFigs = Math.min(sigFigs, values[index].getSigFigs());
					String step = "\\text{" + values[index].getName() + "} = " + quantities[index] + " ";
					if(index == 0) step += "atm";
					else if(index == 1) step += "L";
					else if(index == 2) step += "mol";
					else step += "K";
					steps.add(Function.latex(step));
				}
			}
			steps.add(Function.latex("\\text{R }= " + Units.R + " \\frac{atm * L}{mol * K}"));
			steps.add(Box.createVerticalStrut(5));
			double unknown;
			String base;
			if(blank == 0)
			{
				base = "atm";
				unknown = Units.R * quantities[2] * quantities[3] / quantities[1];
				steps.add(Function.latex("\\text{" + VALUES[0] + "}= \\frac{" + Units.R + "\\frac{atm * L}{mol * K} * " + quantities[2] + "mol * " + 
						quantities[3] + "K}" + "{" + quantities[1] + "L} = " + unknown + " atm"));
			}
			else if(blank == 1)
			{
				base = "L";
				unknown = Units.R * quantities[2] * quantities[3] / quantities[0];
				steps.add(Function.latex("\\text{" + VALUES[1] + "}= \\frac{" + Units.R + "\\frac{atm * L}{mol * K} * " + quantities[2] + "mol * " + 
						quantities[3] + "K}" + "{" + quantities[0] + "atm} = " + unknown + " L"));
			}
			else if(blank == 2)
			{
				base = "mol";
				unknown = quantities[0] * quantities[1] / (Units.R * quantities[3]);
				steps.add(Function.latex("\\text{" + VALUES[2] + "}= \\frac{" + quantities[0] + "atm * " + quantities[1] + "L}{" + Units.R + 
						"\\frac{atm * L}{mol * K} * " + quantities[3] + "K} = " + unknown + " mol"));
			}
			else
			{
				base = "K";
				unknown = quantities[0] * quantities[1] / (Units.R * quantities[2]);
				steps.add(Function.latex("\\text{" + VALUES[3] + "}= \\frac{" + quantities[0] + "atm * " + quantities[1] + "L}{" + Units.R + 
						"\\frac{atm * L}{mol * K} * " + quantities[2] + "mol} = " + unknown + " K"));
			}
			double value = values[blank].getBlankAmount(unknown);
			String unit = values[blank].getUnitName();
			if(value != unknown) 
			{
				steps.add(Function.latex(unknown + " " + base + " = " + value + " " + unit));
				unknown = value;
			}
			result.setText(VALUES[blank].trim() + " = " + Function.withSigFigs(unknown, sigFigs) + " " + unit);
			save = unknown;
			steps.setVisible(true);
		}
	}
	
	public String getHelp()
	{
		return "<html>Enter all known quantities, being sure to<br>"
				+ "select the appropriate units. For STP values,<br>"
				+ "click the checkbox below the fields. For the<br>"
				+ "value remaining, select the desired unit. Click<br>"
				+ "the calculate button and ChemHelper will<br>"
				+ "calculate the remaining value.</html>";
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}