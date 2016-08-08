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

import HelperClasses.DoubleEnterField;
import HelperClasses.Units;

/**
 * File: HenrysLaw.java
 * Package: Functions
 * Version: 08/08/2016
 * Author: Julia McClellan
 * -----------------------------------------------
 * Calculates the solubility or pressure of a solution using Henry's Law.
 */
public class HenrysLaw extends Function
{
	private JPanel panel;
	private DoubleEnterField solubility, pressure;
	private JButton calculate;
	private JLabel result;
	private Box steps;
	private double num;
	
	/**
	 * Constructs the function.
	 */
	public HenrysLaw()
	{
		super("Henry's Law");
		
		solubility = new DoubleEnterField("Solubility", true, "Mass", "Volume");
		pressure = new DoubleEnterField("Pressure", true, "Pressure");
		calculate = new JButton("Calculate");
		calculate.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					steps.setVisible(false);
					steps.removeAll();
					steps.add(Function.latex("\\frac{S_{1}}{S_{2}} = \\frac{P_{1}}{P_{2}}"));
					steps.add(Box.createVerticalStrut(5));
					
					double s1 = solubility.getBeforeValue(), s2 = solubility.getAfterValue(), p1 = pressure.getBeforeValue(), p2 = pressure.getAfterValue();
					if(s1 == Units.ERROR_VALUE || s2 == Units.ERROR_VALUE || p1 == Units.ERROR_VALUE || p2 == Units.ERROR_VALUE)
					{
						result.setText("There was a problem with your input.");
						return;
					}
					
					int sigFigs = Math.min(solubility.getSigFigs(), pressure.getSigFigs());
					if(sigFigs == -1)
					{
						result.setText("Do not leave an original value blank.");
					}
					
					s1 /= 10; //The enterfield will convert it to g/L, and dividing by 10 gives g/dL = g/100mL
					if(s2 == Units.UNKNOWN_VALUE)
					{
						if(p2 == Units.UNKNOWN_VALUE)
						{
							result.setText("Leave only one value blank");
							return;
						}
						steps.add(Function.latex("S_{1} = " + s1 + " \\frac{g}{100mL}"));
						steps.add(Function.latex("P_{1} = " + p1 + " atm"));
						steps.add(Function.latex("S_{2} = ? \\frac{g}{100mL}"));
						steps.add(Function.latex("P_{1} = " + p1 + " atm"));
						steps.add(Box.createVerticalStrut(5));
						
						steps.add(Function.latex("S_{2} = \\frac{S_{1} * P_{2}}{P_{1}}"));
						steps.add(Function.latex("S_{2} = \\frac{" + s1 + "\\frac{g}{100mL} * " + p2 + "atm}{" + p1 + "atm}"));
						s2 = s1 * p2 / p1;
						steps.add(Function.latex("S_{2} = " + s2 + " \\frac{g}{100mL}"));
						num = solubility.getBlankAmount(s2 * 10); //Into the standard units of g/L
						String unit = "g / 100mL";
						if(s2 != num)
						{
							unit = solubility.getDesiredUnit();
							steps.add(Function.latex(s2 + " \\frac{g}{100mL} = " + num + " " + solubility.getDesiredUnitLatex()));
						}
						result.setText("<html>S<sub>2</sub> = " + Function.withSigFigs(num, sigFigs) + " " + unit);
					}
					else
					{
						if(s2 == Units.UNKNOWN_VALUE)
						{
							result.setText("Leave only one value blank");
							return;
						}
						s2 /= 10;
						steps.add(Function.latex("S_{1} = " + s1 + " \\frac{g}{100mL}"));
						steps.add(Function.latex("P_{1} = " + p1 + " atm"));
						steps.add(Function.latex("S_{2} = " + s2 + " \\frac{g}{100mL}"));
						steps.add(Function.latex("P_{1} = ? atm"));
						steps.add(Box.createVerticalStrut(5));
						
						steps.add(Function.latex("P_{2} = \\frac{P_{1} * S_{2}}{S_{1}}"));
						steps.add(Function.latex("P_{2} = \\frac{" + p1 + "atm * " + s2 + "\\frac{g}{100mL}}{" + s1 + "\\frac{g}{100mL}}"));
						p2 = s2 * p1 / s1;
						steps.add(Function.latex("P_{2} = " + p2 + " atm"));
						num = pressure.getBlankAmount(p2);
						String unit = "atm";
						if(p2 != num)
						{
							unit = pressure.getDesiredUnit();
							steps.add(Function.latex(p2 + " atm = " + num + " " + unit));
						}
						result.setText("<html>P<sub>2</sub> = " + Function.withSigFigs(num, sigFigs) + " " + unit);
					}
					steps.setVisible(true);
				}
			});
		result = new JLabel();
		
		Box box = Box.createVerticalBox();
		box.add(new JLabel("<html>S<sub>1</sub> / S<sub>2</sub> = P<sub>1</sub> / P<sub>2</sub>"));
		box.add(solubility);
		box.add(pressure);
		box.add(calculate);
		box.add(result);
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		subpanel.add(box, c);
		c.gridx++;
		subpanel.add(Box.createHorizontalStrut(10), c);
		c.gridx++;
		steps = Box.createVerticalBox();
		subpanel.add(steps, c);
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	/**
	 * Returns true as this function can save and use saved numbers.
	 */
	@Override
	public boolean number()
	{
		return true;
	}
	
	/**
	 * Returns the value to save.
	 * @return The most recently calculated value.
	 */
	@Override
	public double saveNumber()
	{
		return num;
	}
	
	/**
	 * Uses a number previously saved by ChemHelper in this function.
	 * @param num The saved number.
	 */
	@Override
	public void useSavedNumber(double num)
	{
		String[] options = {"Solubility - before", "Solubility - after", "Pressure - before", "Pressure - after"};
		String result = (String)JOptionPane.showInputDialog(panel, "Choose where to use the number.", "Choose number", JOptionPane.PLAIN_MESSAGE, null, 
				options, options[0]);
		if(result == null) return;
		if(result.equals(options[0])) solubility.setBeforeValue(num);
		else if(result.equals(options[1])) solubility.setAfterValue(num);
		else if(result.equals(options[2])) pressure.setBeforeValue(num);
		else pressure.setAfterValue(num);
	}
	
	/**
	 * Returns the instructions for this function.
	 * @return The help string.
	 */
	@Override
	public String getHelp()
	{
		return "<html>Enter two before values and one after value<br>"
				+ "from the solution. Click the calculate button<br>"
				+ "to find the remaining after value.</html>";
	}
	
	/**
	 * Returns the panel containing the equilibrium GUI components.
	 * @return The JPanel for this function.
	 */
	@Override
	public JPanel getPanel()
	{
		return panel;
	}
}