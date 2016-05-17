/*
 * Calculates something's density, mass, or volume given the other two. Shows calculation steps.
 * number() returns true- saves last calculated value and can use saved for density, mass, or volume.
 * 
 * Author: Julia McClellan
 * Version: 5/16/2016
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
	
	public Density()
	{
		super("Density Calculator");
		toSave = 0;
		
		JPanel input = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		density = new EnterField("Density", "Mass", "Volume");
		density.setUnit(9);
		density.setUnit2(6);
		volume = new EnterField("Volume", "Volume");
		volume.setUnit(6);
		mass = new EnterField("Mass", "Mass");
		mass.setUnit(9);
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		result = new JLabel();
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		
		input.add(new JLabel("Enter known informaion:"), c);
		
		c.anchor = GridBagConstraints.NORTHWEST;
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
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		subpanel.add(input, c);
		c.gridx++;
		subpanel.add(Box.createHorizontalStrut(10), c);
		c.gridx++;
		subpanel.add(steps, c);
		
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			steps.removeAll();
			steps.setVisible(false);
			steps.add(Function.latex("D = \\frac{M}{V}"));
			steps.add(Box.createVerticalStrut(5));
			double givenMass = 0, givenVolume = 0, givenDensity = 0;
			String unknown = "";

			givenMass = mass.getAmount();
			if(givenMass == Units.UNKNOWN_VALUE)
			{
				unknown = "Mass";
				steps.add(Function.latex("\\text{Mass} = ? g"));
			}
			else if(givenMass == Units.ERROR_VALUE)
			{
				result.setText("Unacceptable value for mass.");
				return;
			}
			else
			{
				givenMass /= 1000;
				steps.add(Function.latex("\\text{Mass} = " + givenMass + " kg"));
			}
			
			givenVolume = volume.getAmount();
			if(givenVolume == Units.UNKNOWN_VALUE)
			{
				if(!unknown.equals(""))
				{
					result.setText("Leave only one field blank.");
					return;
				}
				unknown = "Volume";
				steps.add(Function.latex("\\text{Volume} = ? L"));
			}
			else if(givenVolume == Units.ERROR_VALUE)
			{
				result.setText("Unacceptable value for volume.");
				return;
			}
			else steps.add(Function.latex("\\text{Volume} = " + givenVolume + " L"));
			
			givenDensity = density.getAmount();
			if(givenDensity == Units.UNKNOWN_VALUE)
			{
				if(!unknown.equals(""))
				{
					result.setText("Leave only one field blank.");
					return;
				}
				unknown = "Density";
				steps.add(Function.latex("\\text{Density} = ? \\frac{kg}{L}"));
			}
			else if(givenDensity == Units.ERROR_VALUE)
			{
				result.setText("Unacceptable value for density.");
				return;
			}
			else
			{
				givenDensity /= 1000;
				steps.add(Function.latex("\\text{Density} = " + givenDensity + " \\frac{kg}{L}"));
			}
			
			steps.add(Box.createVerticalStrut(5));
			
			String unknownUnit = "";
			int sigFigs;
			if(unknown.equals("Mass"))
			{
				sigFigs = Math.min(volume.getSigFigs(), density.getSigFigs());
				toSave = givenDensity * givenVolume;
				steps.add(Function.latex("\\text{Mass} = " + givenDensity + "\\frac{kg}{L} * " + givenVolume + "L = " + toSave + " kg"));
				unknownUnit = mass.getUnitName();
				if(!unknownUnit.equals("kg"))
				{
					String step = toSave + " kg = ";
					toSave *= 1000;
					toSave = mass.getBlankAmount(toSave);
					steps.add(Function.latex(step + toSave + " " + unknownUnit));
				}
			}
			else if(unknown.equals("Volume"))
			{
				sigFigs = Math.min(mass.getSigFigs(), density.getSigFigs());
				toSave = givenMass / givenDensity;
				steps.add(Function.latex("\\text{Volume} = \\frac{" + givenMass + "kg}{" + givenDensity + " \\frac{kg}{L}} = " + toSave + " L"));
				unknownUnit = volume.getUnitName();
				if(!unknownUnit.equals("L"))
				{
					String step = toSave + " L = ";
					toSave = volume.getBlankAmount(toSave);
					steps.add(Function.latex(step + toSave + " " + unknownUnit));
				}
			}
			else if(unknown.equals("Density"))
			{
				sigFigs = Math.min(mass.getSigFigs(), volume.getSigFigs());
				toSave = givenMass / givenVolume;
				steps.add(Function.latex("\\text{Density} = \\frac{" + givenMass + "kg}{" + givenVolume + "L} = " + toSave + "\\frac{kg}{L}"));
				String mUnit = density.getUnitName(), vUnit = density.getUnit2Name();
				if(!mUnit.equals("kg") || !vUnit.equals("L"))
				{
					String step = toSave + "\\frac{kg}{L} = ";
					if(!mUnit.equals("kg")) toSave *= 1000;
					toSave = density.getBlankAmount(toSave);
					unknownUnit = "\\frac{" + mUnit + "}{" + vUnit + "}";
					steps.add(new JLabel(step + toSave + " " + unknownUnit));
				}
			}
			else
			{
				result.setText("Leave one field blank.");
				return;
			}
			result.setText(unknown + " = " + Function.withSigFigs(toSave, sigFigs) + " " + unknownUnit);
			steps.setVisible(true);
		}
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
	
	public String getHelp()
	{
		return "<html>Enter the two known values and select the<br>"
				+ "desired unit for the unknown. Click the<br>"
				+ "calculate button for ChemHelper to find<br>"
				+ "the third value.</html>";
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}