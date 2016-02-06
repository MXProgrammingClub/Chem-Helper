/*
 * Calculates missing value of PV=nRT when the others are given and shows calculation steps. Has static methods to perform conversions from kPa or torr to atm
 * and from C or F to K.
 * number() returns true- saves the latest calculated value, can use saved for P, V, n, or T.
 * 
 * Author: Julia McClellan and Luke Giacalone
 * Version: 2/5/2016
 */

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
		
		values[1].setUnit(6);//sets default volume to L
		
		stp = new JCheckBox("STP");
		stp.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						if(stp.isSelected())
						{
							values[0].setAmount(Units.STANDARD_PRESSURE);
							values[0].setUnit(0);
							values[1].setUnit(6);
							values[3].setAmount(Units.STANDARD_TEMPERATURE);
							values[3].setUnit(0);
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
			steps.add(new JLabel("PV=nRT"));
			steps.add(Box.createVerticalStrut(5));
			double[] quantities = new double[4];
			int blank = -1;
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
						steps.add(new JLabel(values[index].getName().trim() + " = " + "? " + values[index].getUnitName()));
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
					String step = values[index].getName() + " = " + quantities[index] + " ";
					if(index == 0) step += "atm";
					else if(index == 1) step += "L";
					else if(index == 2) step += "mol";
					else step += "K";
					steps.add(new JLabel(step));
				}
			}
			steps.add(new JLabel("R = " + Units.R + " (atm * L) / (mol * K)"));
			steps.add(Box.createVerticalStrut(5));
			double unknown;
			String base;
			if(blank == 0)
			{
				base = "atm";
				unknown = Units.R * quantities[2] * quantities[3] / quantities[1];
				steps.add(new JLabel("? = (" + Units.R + " * " + quantities[2] + " * " + quantities[3] + ") / (" + quantities[1] + ") = " + unknown + " atm"));
			}
			else if(blank == 1)
			{
				base = "L";
				unknown = Units.R * quantities[2] * quantities[3] / quantities[0];
				steps.add(new JLabel("? = (" + Units.R + " * " + quantities[2] + " * " + quantities[3] + ") / (" + quantities[0] + ") = " + unknown + " L"));
			}
			else if(blank == 2)
			{
				base = "mol";
				unknown = quantities[0] * quantities[1] / (Units.R * quantities[3]);
				steps.add(new JLabel("? = (" + quantities[0] + " * " + quantities[1] + ") / (" + Units.R + " * " + quantities[3] + ") = " + unknown + " mol"));
			}
			else
			{
				base = "K";
				unknown = quantities[0] * quantities[1] / (Units.R * quantities[2]);
				steps.add(new JLabel("? = (" + quantities[0] + " * " + quantities[1] + ") / (" + Units.R + " * " + quantities[2] + ") = " + unknown + " K"));
			}
			double value = values[blank].getBlankAmount(unknown);
			String unit = values[blank].getUnitName();
			if(value != unknown) 
			{
				steps.add(new JLabel(unknown + " " + base + " = " + value + " " + unit));
				unknown = value;
			}
			result.setText(VALUES[blank].trim() + " = " + unknown + " " + unit);
			save = unknown;
			steps.setVisible(true);
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}