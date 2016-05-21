/*
 * Calculates the molar mass of one compound or the ratio of the rates with Graham's law of effusion. Can take either a formula or mass for the compounds.
 * Shows calculation steps.
 * number() returns true- saves latest calculated value and can used saved for a molar mass or ratio.
 * 
 * Author: Julia McClellan
 * Version: 5/21/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import Equation.Compound;
import HelperClasses.TextField;
import ChemHelper.InvalidInputException;

public class Effusion extends Function 
{
	private JPanel panel, c1Panel, c2Panel, ratePanel;
	private JLabel instruction, result, c1Label, c2Label, rateLabel;
	private TextField comp1, comp2, ratio;
	private JRadioButton formula1, formula2, mass1, mass2;
	private JButton calculate;
	private Box box, steps;
	private double toSave;
	
	public Effusion()
	{
		super("Law of Effusion");
		
		instruction = new JLabel("Enter what you know.");
		
		c1Label = new JLabel("Compound 1: ");
		comp1 = new TextField(TextField.COMPOUND);
		formula1 = new JRadioButton("Formula", true);
		mass1 = new JRadioButton("Molar mass");
		ButtonGroup g1 = new ButtonGroup();
		g1.add(formula1);
		g1.add(mass1);
		c1Panel = new JPanel();
		c1Panel.add(c1Label);
		c1Panel.add(comp1);
		c1Panel.add(formula1);
		c1Panel.add(mass1);

		c2Label = new JLabel("Compound 2: ");
		comp2 = new TextField(TextField.COMPOUND);
		formula2 = new JRadioButton("Formula", true);
		mass2 = new JRadioButton("Molar mass");
		ButtonGroup g2 = new ButtonGroup();
		g2.add(formula2);
		g2.add(mass2);
		c2Panel = new JPanel();
		c2Panel.add(c2Label);
		c2Panel.add(comp2);
		c2Panel.add(formula2);
		c2Panel.add(mass2);
		
		rateLabel = new JLabel("Ratio of rates: ");
		ratio = new TextField(TextField.COMPOUND);
		ratePanel = new JPanel();
		ratePanel.add(rateLabel);
		ratePanel.add(ratio);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		result = new JLabel();
		
		box = Box.createVerticalBox();
		box.add(instruction);
		box.add(Box.createVerticalStrut(5));
		box.add(c1Panel);
		box.add(c2Panel);
		box.add(ratePanel);
		box.add(Box.createVerticalStrut(5));
		box.add(calculate);
		box.add(Box.createVerticalStrut(10));
		box.add(result);
		
		steps = Box.createVerticalBox();
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		subpanel.add(box, c);
		c.gridx++;
		subpanel.add(Box.createHorizontalStrut(10), c);
		c.gridx++;
		subpanel.add(steps, c);
		
		panel = new JPanel();
		panel.add(subpanel);
		
		toSave = 0;
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			steps.removeAll();
			steps.setVisible(false);
			double mass1 = 0, mass2 = 0, rate = 0;
			int sigFigs = Integer.MAX_VALUE;
			
			if(!comp1.getText().trim().equals(""))
			{
				if(formula1.isSelected())
				{
					Compound c1;
					try 
					{
						c1 = Compound.parseCompound(comp1.getText());
					} 
					catch (InvalidInputException e) 
					{
						if(!(e instanceof InvalidInputException)) e = new InvalidInputException(-1);
						result.setText(e.getMessage());
						return;
					}
					StringBuffer[] massStep = new StringBuffer[2];
					mass1 = c1.getMolarMassSteps(massStep);
					steps.add(Function.latex("\\text{Find molar mass of }" + latex(c1) + "\\text{:}"));
					steps.add(Function.latex(massStep[0].toString()));
					steps.add(Function.latex(massStep[1].toString()));
				}
				else 
				{
					try
					{
						mass1 = Double.parseDouble(comp1.getText());
						sigFigs = Function.sigFigs(comp1.getText());
					}
					catch(Throwable e)
					{
						result.setText("Invalid input for compound 1.");
						return;
					}
				}
				steps.add(Function.latex("\\text{Mass 1} = " + mass1 + "\\frac{g}{mol}"));
				steps.add(Box.createVerticalStrut(5));
			}
			
			if(!comp2.getText().trim().equals(""))
			{
				if(formula2.isSelected())
				{	
					Compound c2;
					try
					{
						c2 = Compound.parseCompound(comp2.getText());
					} 
					catch (Throwable e) 
					{
						if(!(e instanceof InvalidInputException)) e = new InvalidInputException(-1);
						result.setText(e.getMessage());
						return;
					}
					StringBuffer[] massStep = new StringBuffer[2];
					mass2 = c2.getMolarMassSteps(massStep);
					steps.add(Function.latex("\\text{Find molar mass of }" + latex(c2) + "\\text{:}"));
					steps.add(Function.latex(massStep[0].toString()));
					steps.add(Function.latex(massStep[1].toString()));
				}
				else 
				{
					try
					{
						mass2 = Double.parseDouble(comp2.getText());
						sigFigs = Math.min(sigFigs, Function.sigFigs(comp2.getText()));
					}
					catch(Throwable e)
					{
						result.setText("Invalid input for compound 2.");
						return;
					}
				}
				steps.add(Function.latex("\\text{Mass 2 = }" + mass2 + "\\frac{g}{mol}"));
				steps.add(Box.createVerticalStrut(5));
			}
			else if(mass1 == 0)
			{
				result.setText("Leave only one field blank.");
				return;
			}
			
			if(!ratio.getText().trim().equals(""))
			{
				try
				{
					rate = Double.parseDouble(ratio.getText());
					sigFigs = Math.min(sigFigs, Function.sigFigs(ratio.getText()));
				}
				catch(Throwable e)
				{
					result.setText("Invalid input for ratio.");
					return;
				}
				steps.add(Function.latex("\\text{Ratio of rates = }" + rate));
				steps.add(Box.createVerticalStrut(10));
			}
			else if(mass1 == 0 || mass2 == 0)
			{
				result.setText("Leave only one field blank.");
				return;
			}
			else steps.add(Box.createVerticalStrut(5));
			
			if(rate == 0)
			{
				toSave = Math.sqrt(mass1 / mass2);
				steps.add(Function.latex("\\text{Ratio of rates = }\\sqrt{\\frac{" + mass1 + "\\frac{g}{mol}}{" + mass2 + "\\frac{g}{mol}}} = " + toSave));
				if(sigFigs == Integer.MAX_VALUE) result.setText("Ratio of rates = " + toSave);
				else result.setText("Ratio of rates = " + Function.withSigFigs(toSave, sigFigs));
				
			}
			else if(mass2 == 0)
			{
				toSave = mass1 / (rate * rate);
				steps.add(Function.latex("\\text{Mass 2 = }\\frac{" + mass1 + "\\frac{g}{mol}}{" + rate + "^2} = " + toSave + "\\frac{g}{mol}"));
				result.setText("Mass of compound 2 = " + Function.withSigFigs(toSave, sigFigs));
				
			}
			else if(mass1 == 0)
			{
				toSave = (rate * rate) / mass2;
				steps.add(new JLabel("\\text{Mass 1 = }\\frac{" + rate + "^2}{" + mass2 + "\\frac{g}{mol}} = " + toSave + "\\frac{g}{mol}"));
				result.setText("Mass of compound 1 = " + Function.withSigFigs(toSave, sigFigs));
			}
			else
			{
				result.setText("You did not enter enough information to make any calulations.");
				return;
			}
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
		String[] options = {"Compound 1", "Compound 2", "Ratio"};
		Object selected = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, options, "Compound 1");
		if(selected instanceof String)
		{
			if(selected.equals("Compound 1"))
			{
				comp1.setText("" + num);
				mass1.setSelected(true);
			}
			else if(selected.equals("Compound 2"))
			{
				comp2.setText("" + num);
				mass2.setSelected(true);
			}
			else ratio.setText("" + num);
		}
	}
	
	public String getHelp()
	{
		return "<html>" + TextField.getHelp() + "</html>";
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}
