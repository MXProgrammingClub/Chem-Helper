/*
 * Calculates missing value of PV=nRT when the others are given and shows calculation steps. Has static methods to perform conversions from kPa or torr to atm
 * and from C or F to K.
 * number() returns true- saves the latest calculated value, can use saved for P, V, n, or T.
 * 
 * Author: Julia McClellan and Luke Giacalone
 * Version: 2/3/2016
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
	public static final double R = .0821, STANDARD_PRESSURE = 1, STANDARD_TEMPERATURE = 273.15;
	public static final String[][] UNITS = {{"atm", "torr", "kPa"}, 
			{"pL", "nL", "\u00B5L", "mL", "cL", "dL", "L", "daL", "hL", "kL", "ML", "TL", "GL"}, 
			{"mol"}, {"K", "\u2103", "\u2109"}};
	
	private static final int UNKNOWN_VALUE = -500, ERROR_VALUE = -501; // Values which none of the entered values could be.
	private static final String[] VALUES = {"Pressure", "Volume", "Moles", "Temperature"};
	private static final String[] NO_SPACES = {"Pressure", "Volume", "Moles", "Temperature"};
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
			EnterField field = new EnterField(VALUES[index], UNITS[index]);
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
							values[0].setAmount(STANDARD_PRESSURE);
							values[0].setUnit(0);
							values[1].setUnit(6);
							values[3].setAmount(STANDARD_TEMPERATURE);
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
				null, NO_SPACES, "Pressure");
		for(int index = 0; index < NO_SPACES.length; index++)
		{
			if(NO_SPACES[index].equals(selected))
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
						steps.add(new JLabel(values[index].getName().trim() + " = " + "? " + UNITS[index][values[index].getUnit()]));
						blank = index;
					}
					else
					{
						result.setText("Only one value can be left blank.");
						return;
					}
				}
				else if((index == 1 && values[index].getUnit() != 6) || values[index].getUnit() != 0)
				{
					String step = values[index].getName().trim() + " = " + quantities[index] + " ";
					if(index == 0)
					{
						if(values[index].getUnit() == 1)
						{
							quantities[index] = Units.torrToatm(quantities[index]);
							step += "torr * " + "(0.00131579 atm / 1 torr) = " + quantities[index] + " atm";
						}
						else
						{
							quantities[index] = Units.kPaToatm(quantities[index]);
							step += "kPa * " + "(0.00986923 atm / 1 kPa) = " + quantities[index] + " atm";
						}
					}
					else if(index == 1) {
						//quantities[index] = Units.volumeToLiters(quantities[index], values[index].getUnit());
						step += values[index].getUnitName();
						if(Units.POWERS[values[index].getUnit()] != 0) step += " * " + "(10^" + Units.POWERS[values[index].getUnit()]
								+ " L / "  + values[index].getUnitName() + ") " + " = " + quantities[index] + " L";
					}
					else if(index == 3)
					{
						if(values[index].getUnit() == 1)
						{
							quantities[index] = Units.celsiusToKelvin(quantities[index]);
							step += "+ 273.15 = " + quantities[index] + " K";
						}
						else
						{
							step = values[index].getName().trim() + " = (" + quantities[index] + " + 459.67) * (5 / 9) = ";
							quantities[index] = Units.fahrenheitToKelvin(quantities[index]);
							step += quantities[index] + " K";
							
						}
					}
					steps.add(new JLabel(step));
				}
				else steps.add(new JLabel(values[index].getName().trim() + " = " + quantities[index] + " " + UNITS[index][values[index].getUnit()]));
			}
			steps.add(new JLabel("R = " + R + " (atm * L) / (mol * K)"));
			steps.add(Box.createVerticalStrut(5));
			double unknown;
			int unitNum = values[blank].getUnit();
			String unit = UNITS[blank][unitNum];
			if(blank == 0)
			{
				unknown = R * quantities[2] * quantities[3] / quantities[1];
				steps.add(new JLabel("? = (" + R + " * " + quantities[2] + " * " + quantities[3] + ") / (" + quantities[1] + ") = " + unknown + " atm"));
				if(unitNum == 1)
				{
					String step = unknown + " atm * (1 torr / 0.00131579 atm) = ";
					unknown = Units.atmTotorr(unknown);
					steps.add(new JLabel(step + unknown + " torr"));
				}
				else if(unitNum == 2)
				{
					String step = unknown + " atm * (1 kPa / 0.00986923 atm) = ";
					unknown = Units.atmTokPa(unknown);
					steps.add(new JLabel(step + unknown + " kPa"));
				}
			}
			else if(blank == 1)
			{
				unknown = R * quantities[2] * quantities[3] / quantities[0];
				steps.add(new JLabel("? = (" + R + " * " + quantities[2] + " * " + quantities[3] + ") / (" + quantities[0] + ") = " + unknown + " L"));
			}
			else if(blank == 2)
			{
				unknown = quantities[0] * quantities[1] / (R * quantities[3]);
				steps.add(new JLabel("? = (" + quantities[0] + " * " + quantities[1] + ") / (" + R + " * " + quantities[3] + ") = " + unknown + " mol"));
			}
			else
			{
				unknown = quantities[0] * quantities[1] / (R * quantities[2]);
				steps.add(new JLabel("? = (" + quantities[0] + " * " + quantities[1] + ") / (" + R + " * " + quantities[2] + ") = " + unknown + " K"));
				if(unitNum == 1)
				{
					String step = unknown + " K - 273.15 = ";
					unknown = Units.kelvinToCelsius(unknown);
					steps.add(new JLabel(step + unknown + " " + UNITS[3][1]));
				}
				else if(unitNum == 2)
				{
					String step = unknown + " K * (9 / 5) - 273.15 = ";
					unknown = Units.kelvinToFahrenheit(unknown);
					steps.add(new JLabel(step + unknown + " " + UNITS[3][2]));
				}
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