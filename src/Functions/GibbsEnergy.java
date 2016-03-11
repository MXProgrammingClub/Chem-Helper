/*
 * Calculates the Gibbs Free Energy of a reaction and determines if the reaction is spontaneous.
 * number returns true- saves last calculated value and can use saved in any field.
 * 
 * Author: Julia McClellan
 * Version: 3/11/2016
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

public class GibbsEnergy extends Function 
{
	private static final String[] FIELDS = {"\u0394G", "T", "\u0394S"};
	private EnterField[] fields;
	private JPanel panel;
	private JButton calculate;
	private JLabel answer, spontaneous;
	private Box steps;
	private double dG;
	
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
							spontaneous.setText("");
							return;
						}
						int sigFigs = Math.min(fields[0].getSigFigs(), fields[2].getSigFigs());
						
						//Base unit for calculations is kJ, not J
						dH /= 1000;
						dS /= 1000;
						
						if(t == Units.UNKNOWN_VALUE)
						{
							steps.add(new JLabel("\u0394G = \u0394H - T\u0394S"));
							steps.add(Box.createVerticalStrut(5));
							steps.add(new JLabel("\u0394H = " + dH + " kJ"));
							steps.add(new JLabel("\u0394S = " + dS + " kJ"));
							steps.add(Box.createVerticalStrut(5));
							steps.add(new JLabel("For spontaneous reactions, \u0394H - T\u0394S < 0"));
							steps.add(new JLabel("\u0394H < T\u0394S"));
							
							t = dH / dS;
							if(dS > 0)
							{
								steps.add(new JLabel("\u0394H / \u0394S < T"));
								steps.add(new JLabel(dH + " / " + dS + " < T"));
								steps.add(new JLabel(t + " K < T"));
								t = fields[1].getBlankAmount(t);
								if(t < 0) steps.add(new JLabel("The reaction is always spontaneous."));
								else
								{
									steps.add(new JLabel("Reaction is spontaneous for T > " + t + " " + fields[1].getUnitName()));
									answer.setText("T > " + Function.withSigFigs(t, sigFigs) + " " + fields[1].getUnitName());
									dG = t; //For saving the number.
								}
							}
							else
							{
								steps.add(new JLabel("\u0394H / \u0394S > T"));
								steps.add(new JLabel(dH + " / " + dS + " > T"));
								steps.add(new JLabel(t + " K > T"));
								t = fields[1].getBlankAmount(t);
								if(t < 0) steps.add(new JLabel("Impossible temperature range. The reaction is never spontaneous."));
								else
								{
									steps.add(new JLabel("Reaction is spontaneous for T < " + t + " " + fields[1].getUnitName()));
									answer.setText("T < " + Function.withSigFigs(t, sigFigs) + " " + fields[1].getUnitName());
									dG = t; //For saving the number.
								}
							}
						}
						else
						{
							sigFigs = Math.min(sigFigs, fields[1].getSigFigs());
							dG = dH - t * dS;
						
							steps.add(new JLabel("\u0394G = \u0394H - T\u0394S"));
							steps.add(Box.createVerticalStrut(5));
							steps.add(new JLabel("\u0394H = " + dH + " kJ"));
							steps.add(new JLabel("T = " + t + " K"));
							steps.add(new JLabel("\u0394S = " + dS + " kJ"));
							steps.add(Box.createVerticalStrut(5));
							steps.add(new JLabel("\u0394G = " + dH + " - " + t + " * " + dS + " = " + dG));
						
							answer.setText("\u0394G = " + Function.withSigFigs(dG, sigFigs));
							if(dG > 0)
							{
								spontaneous.setText("Not spontaneous");
								steps.add(new JLabel(dG + " > 0, so the reaction is not spontaneous"));
							}
							else if(dG == 0)
							{
								spontaneous.setText("Equilibrium");
								steps.add(new JLabel(dG + " = 0, so the reaction is at equilibrium"));
							}
							else
							{
								spontaneous.setText("Spontaneous");
								steps.add(new JLabel(dG + " < 0, so the reaction is spontaneous"));
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
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return dG;
	}
	
	public void useSavedNumber(double num)
	{
		String s = (String)JOptionPane.showInputDialog(panel, "Choose where to use it.", "Choose Field", JOptionPane.PLAIN_MESSAGE, null, FIELDS, FIELDS[0]);
		if(s == null) return;
		if(s.equals(FIELDS[0])) fields[0].setText("" + num);
		else if(s.equals(FIELDS[1])) fields[1].setText("" + num);
		else fields[2].setText("" + num);
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}