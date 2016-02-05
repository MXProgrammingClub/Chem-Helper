/*
 * Calculates something's density, mass, or volume given the other two. Shows calculation steps.
 * number() returns true- saves last calculated value and can use saved for density, mass, or volume.
 * 
 * Author: Julia McClellan
 * Version: 2/5/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import HelperClasses.EnterField;
import HelperClasses.Units;

public class Density extends Function 
{
	private JPanel panel;
	private EnterField density, volume, mass;
	private JButton calculate;
	private JLabel result;
	private double toSave;
	private Box steps;
	
	private static final String[] MASS_UNITS = Units.getUnits("Mass");
	private static final String[] VOLUME_UNITS = Units.getUnits("Volume");
	
	public Density()
	{
		super("Density Calculator");
		toSave = 0;
		
		JPanel input = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		density = new EnterField("Density", MASS_UNITS, VOLUME_UNITS);
		density.setUnit(9);
		density.setUnit2(6);
		volume = new EnterField("Volume", VOLUME_UNITS);
		volume.setUnit(6);
		mass = new EnterField("Mass", MASS_UNITS);
		mass.setUnit(9);
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		result = new JLabel();
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		
		input.add(new JLabel("Enter known informaion:"), c);
		
		c.anchor = GridBagConstraints.WEST;
		c.gridy = 1;
		input.add(density, c);
		c.gridy = 2;
		input.add(volume, c);
		c.gridy = 3;
		input.add(mass, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridy = 4;
		input.add(calculate, c);
		
		c.gridy = 5;
		input.add(result, c);
		
		steps = Box.createVerticalBox();
		
		panel = new JPanel();
		panel.add(input);
		panel.add(steps);
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			steps.removeAll();
			steps.setVisible(false);
			steps.add(new JLabel("D = M / V"));
			steps.add(Box.createVerticalStrut(5));
			double givenMass = 0, givenVolume = 0, givenDensity = 0;
			String unknown = "";
			try
			{
				givenMass = Double.parseDouble(mass.getText());
				int unit = mass.getUnit();
				if(unit != 6) givenMass = normalizeUnit(givenMass, unit);
				givenMass /= 1000;
				steps.add(new JLabel("Mass = " + givenMass + " kg"));
			}
			catch(Throwable t)
			{
				unknown = "Mass";
				steps.add(new JLabel("Mass = ?"));
			}
			
			try
			{
				givenVolume = Double.parseDouble(volume.getText());
				int unit = volume.getUnit();
				if(unit != 6) givenVolume = normalizeUnit(givenVolume, unit);
				steps.add(new JLabel("Volume = " + givenVolume + " L"));
			}
			catch(Throwable t)
			{
				if(volume.getText().trim().equals(""))
				{
					if(!unknown.equals(""))
					{
						result.setText("Leave only one field blank.");
						return;
					}
					unknown = "Volume";
					steps.add(new JLabel("Volume = ?"));
				}
				else
				{
					result.setText("Unacceptable value for volume.");
					return;
				}
			}

			try
			{
				givenDensity = Double.parseDouble(density.getText());
				steps.add(new JLabel("Density = " + givenDensity + " kg / L"));
				int mUnit = density.getUnit(), vUnit = density.getUnit2();
				givenDensity = normalizeUnit(givenDensity, mUnit);
				givenDensity /= 1000;
				givenDensity = convertUnit(givenDensity, vUnit);
			}
			catch(Throwable t)
			{
				if(density.getText().trim().equals(""))
				{
					if(!unknown.equals(""))
					{
						result.setText("Leave only one field blank.");
						return;
					}
					unknown = "Density";
					steps.add(new JLabel("Density = ?"));
				}
				else
				{
					result.setText("Unacceptable value for density.");
					return;
				}
			}
			steps.add(Box.createVerticalStrut(5));
			
			String unknownUnit = "";
			if(unknown.equals("Mass"))
			{
				toSave = givenDensity * givenVolume;
				steps.add(new JLabel("Mass = " + givenDensity + " * " + givenVolume + " = " + toSave + " kg"));
				int unit = mass.getUnit();
				unknownUnit = MASS_UNITS[unit];
				if(unit != 10)
				{
					if(unit != 6)
					{
						String step = toSave + " kg = ";
						toSave *= 1000;
						toSave = convertUnit(toSave, unit);
						steps.add(new JLabel(step + toSave + " " + unknownUnit));
					}
					else
					{
						String step = toSave + " kg = ";
						toSave *= 1000;
						steps.add(new JLabel(step + toSave + " g"));
					}	
				}
			}
			else if(unknown.equals("Volume"))
			{
				toSave = givenMass / givenDensity;
				steps.add(new JLabel("Volume = " + givenMass + " / " + givenDensity + " = " + toSave + " L"));
				int unit = volume.getUnit();
				unknownUnit = VOLUME_UNITS[unit];
				if(unit != 6)
				{
					String step = toSave + " L = ";
					toSave = convertUnit(toSave, unit);
					steps.add(new JLabel(step + toSave + " " + unknownUnit));
				}
			}
			else if(unknown.equals("Density"))
			{
				toSave = givenMass / givenVolume;
				steps.add(new JLabel("Density = " + givenMass + " / " + givenVolume + " = " + toSave + " kg/L"));
				int mUnit = density.getUnit(), vUnit = density.getUnit2();
				if(mUnit != 10 || vUnit != 6)
				{
					String step = toSave + " kg/L = ";
					if(mUnit != 10)
					{
						toSave *= 1000;
						toSave = convertUnit(toSave, mUnit);
					}
					if(vUnit != 6)
					{
						toSave = normalizeUnit(toSave, vUnit);
					}
					unknownUnit = MASS_UNITS[mUnit] + "/" + VOLUME_UNITS[vUnit];
					steps.add(new JLabel(step + toSave + " " + unknownUnit));
				}
			}
			else
			{
				result.setText("Leave one field blank.");
				return;
			}
			result.setText(unknown + " = " + toSave + " " + unknownUnit);
			steps.setVisible(true);
		}
	}
	
	private double normalizeUnit(double number, int index)
	{
		if(index != -1) return number * Math.pow(10, Units.POWERS[index]);
		return 0;
	}
	
	private double convertUnit(double number, int index)
	{
		if(index != -1) return number / Math.pow(10, Units.POWERS[index]);
		return 0;
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return toSave;
	}
	
	public void useSavedNumber(double num)
	{
		String[] fields = {"Density", "Volume", "Mass"};
		Object field = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, fields, "Density");
		if(field instanceof String)
		{
			if(field.equals("Density")) density.setAmount(num);
			else if(field.equals("Volume")) volume.setAmount(num);
			else mass.setAmount(num);
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}