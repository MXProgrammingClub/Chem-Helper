/*
 * Calculates something's density, mass, or volume given the other two. Shows calculation steps.
 * number() returns true- saves last calculated value and can use saved for density, mass, or volume.
 * 
 * Author: Julia McClellan
 * Version: 1/4/2016
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
	private JTextField density, volume, mass, vUnit, mUnit, dUnit;
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
		dUnit = new JTextField("kg/L", 4);
		JPanel densityPanel = new JPanel();
		densityPanel.add(new JLabel("Density:"));
		densityPanel.add(density);
		densityPanel.add(dUnit);
		
		volume = new JTextField(5);
		vUnit = new JTextField("L", 2);
		JPanel volumePanel = new JPanel();
		volumePanel.add(new JLabel("Volume:"));
		volumePanel.add(volume);
		volumePanel.add(vUnit);
		
		mass = new JTextField(5);
		mUnit = new JTextField("kg", 2);
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
				givenMass /= 1000;
				steps.add(new JLabel("Mass = " + givenMass + " kg"));
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
				steps.add(new JLabel("Density = " + givenDensity + " kg / L"));
				String unit = dUnit.getText();
				if(!unit.equals("kg/L"))
				{
					String massUnit, volumeUnit;
					try
					{
						massUnit = unit.substring(0, unit.indexOf('/')).trim();
						volumeUnit = unit.substring(unit.indexOf('/') + 1).trim();
					}
					catch(Throwable e)
					{
						result.setText("Unaccptable unit for density.");
						return;
					}
					if(!massUnit.equals("g"))
					{
						if(massUnit.length() == 2 && massUnit.charAt(1) == 'g' && prefixes.indexOf(massUnit.charAt(0)) != -1) 
						{
							givenDensity = normalizeUnit(givenDensity, massUnit.charAt(0));
							givenDensity /= 1000;
						}
						else
						{
							result.setText("Unaccptable unit for density.");
							return;
						}
					}
					else givenDensity /= 1000;
					if(!volumeUnit.equals("L"))
					{
						if(volumeUnit.length() == 2 && volumeUnit.charAt(1) == 'L' && prefixes.indexOf(volumeUnit.charAt(0)) != -1) 
						{
							givenDensity = convertUnit(givenDensity, volumeUnit.charAt(0));
						}
						else
						{
							result.setText("Unaccptable unit for density.");
							return;
						}
					}
				}
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
					unknownUnit = dUnit.getText();
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
				steps.add(new JLabel("Mass = " + givenDensity + " * " + givenVolume + " = " + toSave + " kg"));
				if(!unknownUnit.equals("kg"))
				{
					if(unknownUnit.length() == 2 && unknownUnit.charAt(1) == 'g' && prefixes.indexOf(unknownUnit.charAt(0)) != -1) 
					{
						String step = toSave + " kg = ";
						toSave *= 1000;
						toSave = convertUnit(toSave, unknownUnit.charAt(0));
						steps.add(new JLabel(step + toSave + " " + unknownUnit));
					}
					else if(unknownUnit.equals("g"))
					{
						String step = toSave + " kg = ";
						toSave *= 1000;
						steps.add(new JLabel(step + toSave + " " + unknownUnit));
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
						steps.add(new JLabel(step + toSave + " " + unknownUnit));
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
				steps.add(new JLabel("Density = " + givenMass + " / " + givenVolume + " = " + toSave + " kg/L"));
				unknownUnit = dUnit.getText();
				if(!unknownUnit.equals("kg/L"))
				{
					String step = toSave + " kg/L = ";
					String massUnit, volumeUnit;
					try
					{
						massUnit = unknownUnit.substring(0, unknownUnit.indexOf('/')).trim();
						volumeUnit = unknownUnit.substring(unknownUnit.indexOf('/') + 1).trim();
					}
					catch(Throwable e)
					{
						result.setText("Unaccptable unit for density.");
						return;
					}
					if(!massUnit.equals("g"))
					{
						if(massUnit.length() == 2 && massUnit.charAt(1) == 'g' && prefixes.indexOf(massUnit.charAt(0)) != -1) 
						{
							toSave *= 1000;
							toSave = convertUnit(toSave, massUnit.charAt(0));
						}
						else
						{
							result.setText("Unaccptable unit for density.");
							return;
						}
					}
					if(!volumeUnit.equals("L"))
					{
						if(volumeUnit.length() == 2 && volumeUnit.charAt(1) == 'L' && prefixes.indexOf(volumeUnit.charAt(0)) != -1) 
						{
							toSave = normalizeUnit(toSave, volumeUnit.charAt(0));
						}
						else
						{
							result.setText("Unaccptable unit for density.");
							return;
						}
					}
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