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

/**
 * File: GibbsEnergy.java
 * Package: Functions
 * Version: 08/07/2016
 * Authors: Julia McClellan
 * -----------------------------------------------
 * Calculates the Gibbs Free Energy of a reaction and determines if the reaction is spontaneous.
 * number returns true- saves last calculated value and can use saved in any field.
 */
public class GibbsEnergy extends Function 
{
	private static final String[] FIELDS = {"\u0394G", "T", "\u0394S"};
	private EnterField[] fields;
	private JPanel panel;
	private JButton calculate;
	private JLabel answer, spontaneous;
	private Box steps;
	private double dG;
	
	/**
	 * Constructs the function.
	 */
	public GibbsEnergy()
	{
		super("Gibbs Free Energy");
		JPanel box = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		box.add(new JLabel("\u0394G = \u0394H - T\u0394S"), c);		
		fields = new EnterField[3];
		fields[0] = new EnterField("\u0394H", "Energy");
		fields[1] = new EnterField("T", "Temperature");
		fields[2] = new EnterField("\u0394S", "Energy", "Temperature");
		for(EnterField field: fields)
		{
			c.gridy++;
			box.add(field, c);
		}
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						steps.removeAll();
						answer.setText("");
						spontaneous.setText("");
						double dH = fields[0].getAmount(), t = fields[1].getAmount(), dS = fields[2].getAmount();
						if(dH == Units.ERROR_VALUE || dH == Units.UNKNOWN_VALUE || t == Units.ERROR_VALUE 
								|| dS == Units.ERROR_VALUE || dS == Units.UNKNOWN_VALUE)
						{
							answer.setText("There was a problem with your input.");
							return;
						}
						int sigFigs = Math.min(fields[0].getSigFigs(), fields[2].getSigFigs());
						
						//Base unit for calculations is kJ, not J
						dH /= 1000;
						dS /= 1000;
						
						if(t == Units.UNKNOWN_VALUE)
						{
							steps.add(Function.latex("\u0394G = \u0394H - T\u0394S"));
							steps.add(Box.createVerticalStrut(5));
							steps.add(Function.latex("\u0394H = " + dH + " kJ"));
							steps.add(Function.latex("\u0394S = " + dS + " \\frac{kJ}{K}"));
							steps.add(Box.createVerticalStrut(5));
							steps.add(Function.latex("\\text{For spontaneous reactions, }\u0394H - T\u0394S < 0"));
							steps.add(Function.latex("\u0394H < T\u0394S"));
							
							t = dH / dS;
							if(dS > 0)
							{
								steps.add(Function.latex("\\frac{\u0394H}{\u0394S} < T"));
								steps.add(Function.latex("\\frac{" + dH + "kJ}{" + dS + "\\frac{kJ}{K}} < T"));
								steps.add(Function.latex(t + " K < T"));
								t = fields[1].getBlankAmount(t);
								if(t < 0) steps.add(Function.latex("\\text{The reaction is always spontaneous.}"));
								else
								{
									steps.add(Function.latex("\\text{Reaction is spontaneous for }T > " + t + " " + fields[1].getUnitName()));
									answer.setText("T > " + Function.withSigFigs(t, sigFigs) + " " + fields[1].getUnitName());
									dG = t; //For saving the number.
								}
							}
							else
							{
								steps.add(Function.latex("\\frac{\u0394H}{\u0394S} > T"));
								steps.add(Function.latex("\\frac{" + dH + "kJ}{" + dS + "\\frac{kJ}{K}} > T"));
								steps.add(new JLabel(t + " K > T"));
								t = fields[1].getBlankAmount(t);
								if(t < 0) steps.add(Function.latex("\\text{Impossible temperature range. The reaction is never spontaneous.}"));
								else
								{
									steps.add(Function.latex("\\text{Reaction is spontaneous for }T < " + t + " " + fields[1].getUnitName()));
									answer.setText("T < " + Function.withSigFigs(t, sigFigs) + " " + fields[1].getUnitName());
									dG = t; //For saving the number.
								}
							}
						}
						else
						{
							sigFigs = Math.min(sigFigs, fields[1].getSigFigs());
							dG = dH - t * dS;
						
							steps.add(Function.latex("\u0394G = \u0394H - T\u0394S"));
							steps.add(Box.createVerticalStrut(5));
							steps.add(Function.latex("\u0394H = " + dH + " kJ"));
							steps.add(Function.latex("T = " + t + " K"));
							steps.add(Function.latex("\u0394S = " + dS + " kJ"));
							steps.add(Box.createVerticalStrut(5));
							steps.add(Function.latex("\u0394G = " + dH + "kJ - " + t + "K * " + dS + "\\frac{kJ}{K} = " + dG));
						
							answer.setText("\u0394G = " + Function.withSigFigs(dG, sigFigs));
							if(dG > 0)
							{
								spontaneous.setText("Not spontaneous");
								steps.add(Function.latex(dG + " > 0\\text{, so the reaction is not spontaneous}"));
							}
							else if(dG == 0)
							{
								spontaneous.setText("Equilibrium");
								steps.add(Function.latex(dG + " = 0\\text{, so the reaction is at equilibrium}"));
							}
							else
							{
								spontaneous.setText("Spontaneous");
								steps.add(Function.latex(dG + " < 0\\text{, so the reaction is spontaneous}"));
							}
						}
					}
				});
		c.gridy++;
		box.add(calculate, c);
		
		answer = new JLabel();
		c.gridy++;
		box.add(answer, c);
		spontaneous = new JLabel();
		c.gridy++;
		box.add(spontaneous, c);
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		subpanel.add(box, c);
		c.gridx++;
		subpanel.add(Box.createHorizontalStrut(10), c);
		steps = Box.createVerticalBox();
		c.gridx++;
		subpanel.add(steps, c);
		
		panel = new JPanel();
		panel.add(subpanel);
	}
	
	/**
	 * Returns true as this function can save and use saved numbers.
	 * @return true
	 */
	@Override
	public boolean number()
	{
		return true;
	}
	
	/**
	 * Returns the most recently calculated value.
	 * @return The number to save.
	 */
	@Override
	public double saveNumber()
	{
		return dG;
	}
	
	/**
	 * Allows the user to choose where to use the number.
	 * @param num The saved number
	 */
	@Override
	public void useSavedNumber(double num)
	{
		String s = (String)JOptionPane.showInputDialog(panel, "Choose where to use it.", "Choose Field", JOptionPane.PLAIN_MESSAGE, null, FIELDS, FIELDS[0]);
		if(s == null) return;
		if(s.equals(FIELDS[0])) fields[0].setText("" + num);
		else if(s.equals(FIELDS[1])) fields[1].setText("" + num);
		else fields[2].setText("" + num);
	}
	
	/**
	 * Returns a help message for the function.
	 * @return Instructions
	 */
	@Override
	public String getHelp()
	{
		return "<html>Enter all known information. If you enter the<br>"
				+ "temperature, ChemHelper will calculate \u0394G<br>"
				+ "and determine if the reaction is spontaneous.<br>"
				+ "If you do not enter the temperatue, ChemHelper<br>"
				+ "will determine the temperature range for which<br>"
				+ "the reaction is spontaneous.</html>";
	}
	
	/**
	 * Returns the panel for the function.
	 * @return The JPanel containing the components for this function.
	 */
	@Override
	public JPanel getPanel()
	{
		return panel;
	}
}