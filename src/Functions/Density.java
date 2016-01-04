/*
 * Calculates something's density, mass, or volume given the other two. Shows calculation steps.
 * number() returns true- saves last calculated value and can use saved for density, mass, or volume.
 * 
 * Author: Julia McClellan
 * Version: 1/3/2016
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Density extends Function 
{
	private JPanel panel;
	private JTextField density, volume, mass, vUnit, mUnit;
	private JButton calculate;
	private JLabel result;
	private double toSave;
	private Box steps;
	
	private static final String prefixes = "GMkdcmunp";
	private static final int[] powers = {9, 6, 3, -1, -2, -3, -6, -9, -12};
	
	public Density()
	{
		super("Density Calculator");
		toSave = 0;
		
		density = new JTextField(5);
		JPanel densityPanel = new JPanel();
		densityPanel.add(new JLabel("Density:"));
		densityPanel.add(density);
		densityPanel.add(new JLabel("g/L"));
		
		volume = new JTextField(5);
		vUnit = new JTextField("L", 2);
		JPanel volumePanel = new JPanel();
		volumePanel.add(new JLabel("Volume:"));
		volumePanel.add(volume);
		volumePanel.add(vUnit);
		
		mass = new JTextField(5);
		mUnit = new JTextField("g", 2);
		JPanel massPanel = new JPanel();
		massPanel.add(new JLabel("Mass"));
		massPanel.add(mass);
		massPanel.add(mUnit);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		
		result = new JLabel();
		
		Box box = Box.createVerticalBox();
		box.add(new JLabel("Enter known information:"));
		box.add(densityPanel);
		box.add(volumePanel);
		box.add(massPanel);
		box.add(calculate);
		box.add(result);
		
		steps = Box.createVerticalBox();
		
		panel = new JPanel();
		panel.add(box);
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
			String unknown = "", unknownUnit = "";
			try
			{
				givenMass = Double.parseDouble(mass.getText());
				String unit = mUnit.getText();
				if(!unit.equals("g"))
				{
					if(unit.length() == 2 && unit.charAt(1) == 'g' && prefixes.indexOf(unit.charAt(0)) != -1) 
					{
						givenMass = normalizeUnit(givenMass, unit.charAt(0));
					}
					else
					{
						result.setText("Unaccptable unit for mass");
						return;
					}
				}
				steps.add(new JLabel("Mass = " + givenMass + " " + 'g'));
			}
			catch(Throwable t)
			{
				if(mass.getText().trim().equals(""))
				{
					unknown = "Mass";
					unknownUnit = mUnit.getText();
					steps.add(new JLabel("Mass = ?"));
				}
				else
				{
					result.setText("Unacceptable value for mass.");
					return;
				}
			}
			
			try
			{
				givenVolume = Double.parseDouble(volume.getText());
				String unit = vUnit.getText();
				if(!unit.equals("L"))
				{
					if(unit.length() == 2 && unit.charAt(1) == 'L' && prefixes.indexOf(unit.charAt(0)) != -1) 
					{
						givenVolume = normalizeUnit(givenVolume, unit.charAt(0));
					}
					else
					{
						result.setText("Unaccptable unit for volume.");
						return;
					}
				}
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
					unknownUnit = vUnit.getText();
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
				steps.add(new JLabel("Density = " + givenDensity + " g / L"));
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
			
			if(unknown.equals("Mass"))
			{
				toSave = givenDensity * givenVolume;
				steps.add(new JLabel("Mass = " + givenDensity + " * " + givenVolume + " = " + toSave + " g"));
				if(!unknownUnit.equals("g"))
				{
					if(unknownUnit.length() == 2 && unknownUnit.charAt(1) == 'g' && prefixes.indexOf(unknownUnit.charAt(0)) != -1) 
					{
						String step = toSave + " g = ";
						toSave = convertUnit(toSave, unknownUnit.charAt(0));
						steps.add(new JLabel(step + toSave + unknownUnit));
					}
					else
					{
						result.setText("Unaccptable unit for mass");
						return;
					}
				}
			}
			else if(unknown.equals("Volume"))
			{
				toSave = givenMass / givenDensity;
				steps.add(new JLabel("Volume = " + givenMass + " / " + givenDensity + " = " + toSave + " L"));
				if(!unknownUnit.equals("L"))
				{
					if(unknownUnit.length() == 2 && unknownUnit.charAt(1) == 'L' && prefixes.indexOf(unknownUnit.charAt(0)) != -1) 
					{
						String step = toSave + " L = ";
						toSave = convertUnit(toSave, unknownUnit.charAt(0));
						steps.add(new JLabel(step + toSave + unknownUnit));
					}
					else
					{
						result.setText("Unaccptable unit for volume");
						return;
					}
				}
			}
			else if(unknown.equals("Density"))
			{
				toSave = givenMass / givenVolume;
				steps.add(new JLabel("Density = " + givenMass + " / " + givenVolume + " = " + toSave + " g/L"));
				unknownUnit = "g/L";
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
	
	private double normalizeUnit(double number, char prefix)
	{
		int index = prefixes.indexOf(prefix);
		if(index != -1) return number * Math.pow(10, powers[index]);
		return 0;
	}
	
	private double convertUnit(double number, char prefix)
	{
		int index = prefixes.indexOf(prefix);
		if(index != -1) return number / Math.pow(10, powers[index]);
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
			if(field.equals("Density")) density.setText("" + num);
			else if(field.equals("Volume")) volume.setText("" + num);
			else mass.setText("" + num);
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}