/*
 * Given an equation and amounts of each reactant, determines limiting reactant and calculates leftover amounts of excess reactants. Shows calculation steps.
 * equation() returns true- has an EquationReader as a parameter.
 * number() returns true- can save any of calculated leftovers and use saved for any of the reactants.
 * 
 * Author: Julia McClellan
 * Version: 4/15/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Equation.Compound;
import Equation.Equation;
import HelperClasses.RadioEnterField;
import HelperClasses.Units;

public class LimitingReactant extends Function
{
	private JPanel panel, stoicPanel, enterPanel, resultPanel, stepsPanel;
	private EquationReader reader;
	private JButton calculate, reset;
	private Equation equation;
	private JLabel errorMessage, equationDisplay, limitingLabel;
	private Box box2;
	private ArrayList<RadioEnterField> compounds;
	private ArrayList<Compound> list;
	private ArrayList<Double> toSave;
	private RadioEnterField leftover;
	
	public LimitingReactant()
	{
		super("Limiting Reactant");
		toSave = new ArrayList<Double>();
		
		panel = new JPanel();
		setPanel();
	}
	
	private void setPanel()
	{
		reader = new EquationReader(this);

		calculate = new JButton("Calculate");
		calculate.addActionListener(new CalculateListener());
		reset = new JButton("Reset");
		reset.addActionListener(new ResetListener());
		stepsPanel = new JPanel();
		stepsPanel.setVisible(false);
		
		box2 = Box.createVerticalBox();
		equationDisplay = new JLabel();
		box2.add(equationDisplay);
		enterPanel = new JPanel();
		errorMessage = new JLabel();
		box2.add(enterPanel);
		resultPanel = new JPanel();
		resultPanel.setVisible(false);
		box2.add(resultPanel);
		stoicPanel = new JPanel();
		stoicPanel.add(box2);
		stoicPanel.setVisible(false);
		
		panel.add(reader.getPanel());
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		subpanel.add(stoicPanel, c);
		c.gridx++;
		subpanel.add(stepsPanel, c);
		panel.add(subpanel);
		
		toSave = new ArrayList<Double>();
	}

	private Box generateEnter()
	{
		Box enterBox = Box.createVerticalBox();
		ArrayList<Compound> compounds = equation.getLeft();
		list = new ArrayList<Compound>();
		this.compounds = new ArrayList<RadioEnterField>();
		for(Compound compound: compounds)
		{
			String compoundString = compound.toString();
			if(compound.getNum() != 1) compoundString = compoundString.substring(1);
			compoundString = "<html>" + compoundString + "</html>";
			RadioEnterField field = new RadioEnterField(compoundString, true, "Mass", "Amount", true);
			this.compounds.add(field);
			enterBox.add(field);
			list.add(compound);
		}
		JPanel subPanel = new JPanel();
		subPanel.add(new JLabel("Select a unit for the leftover compound: "));
		leftover = new RadioEnterField("", false, "Mass", "Amount", false);
		subPanel.add(leftover);
		enterBox.add(subPanel);
		enterBox.add(calculate);
		return enterBox;
	}
	
	private class CalculateListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			double[] amounts = new double[compounds.size()];
			String steps = "<html>Find which is the limiting reactant by diving moles of each reactant by its coefficient:<br>";
			Compound limiting = null;
			int limitIndex = 0, sigFigs = Integer.MAX_VALUE;
			double min = Double.MAX_VALUE;
			for(int index = 0; index < amounts.length; index++)
			{
				if(compounds.get(index).isSelected())
				{
					Compound compound = list.get(index);
					String compoundName = compound.toString();
					if(compound.getNum() != 1) compoundName = compoundName.substring(1);
					double amount;
					errorMessage.setText("");
					panel.repaint();

					int thisSigFigs = compounds.get(index).getSigFigs();
					if(thisSigFigs < sigFigs) sigFigs = thisSigFigs;
					amount = compounds.get(index).getAmount();
					if(amount == Units.ERROR_VALUE || amount == Units.UNKNOWN_VALUE)
					{
						enterPanel.setVisible(false);
						errorMessage.setText("There was a problem with your input");
						enterPanel.add(errorMessage);
						enterPanel.setVisible(true);
						return;
					}
					if(compounds.get(index).unit1()) 
					{
						StringBuffer[] molarMass = new StringBuffer[2];
						double mass = compound.getMolarMassSteps(molarMass);
						steps += "Calculate the molar mass of " + compoundName + ":<br>\u2003" + molarMass[0] + molarMass + "<br>";
						steps += "Convert grams to moles by dividing grams of " +  compoundName + " by its molar mass:<br>\u2003" + amount + " g / " + mass;
						amount = amount / mass;
						steps += " g/mol " + " = " + amount + " mol<br>";
					}
					steps += " Divide by the coefficient of " + compoundName + ":<br>\u2003" + amount + " mol / " + compound.getNum();
					amount = amount / compound.getNum();
					steps += " = " + amount + " mol<br>";
					amounts[index] = amount;
					if(amount < min)
					{
						min = amount;
						limitIndex = index;
						limiting = compound;
					}
				}
			}
			String limitingString = limiting.toString();
			if(limiting.getNum() != 1) limitingString = limitingString.substring(1);
			limitingLabel = new JLabel("<html>Limiting Reactant: " + limitingString + "</html>");
			Box resultBox = Box.createVerticalBox();
			resultBox.add(limitingLabel);
			steps += "<br>The smallest of these is " + limitingString + ", so it is the limiting reactant.";
			for(int index = 0; index < amounts.length; index++)
			{
				Compound compound = list.get(index);
				String compoundString = compound.toString();
				if(compound.getNum() != 1) compoundString = compoundString.substring(1);
				double amount = amounts[index];
				amount -= min;
				if(index != limitIndex)
				{
					amount *= compound.getNum();
					steps += "<br>The amount of " + compoundString + " leftover: (" + amounts[index] + " mol - " + min + " mol) * " + compound.getNum() + 
							" = " + amount + " mol";
					String unit = "mol";
					if(compounds.get(index).unit1()) 
					{
						StringBuffer[] molarMass = new StringBuffer[2];
						double mass = compound.getMolarMassSteps(molarMass);
						steps += "<br>Calculate the molar mass of " + compoundString + "<br>\u2003" + molarMass;
						steps += "<br>Convert the amount " + compoundString + " leftover to grams<br>\u2003" + amount + " mol * " + mass + " g/mol = ";
						amount *= compound.getMolarMass();
						steps += amount + " g";
						unit = leftover.getUnit();
						if(!unit.equals("g")) 
						{
							double temp = leftover.getBlankAmount(amount);
							steps += "<br>" + amount + " g = " + temp + " " + unit;
						}
					}
					toSave.add(amount);
					String amountString = Function.withSigFigs(amount, sigFigs) + " " + unit;
					resultBox.add(new JLabel("<html>Amount " + compoundString + " remaining: " + amountString));
				}
			}
			resultPanel.add(resultBox);
			resultPanel.setVisible(true);
			stepsPanel.add(new JLabel(steps));
			stepsPanel.setVisible(true);
			box2.add(reset);
			box2.setVisible(true);
		}
	}
	
	private class ResetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			panel.setVisible(false);
			panel.removeAll();
			setPanel();
			panel.setVisible(true);
		}
	}
	
	public void resetFocus()
	{
		reader.resetFocus();
	}
	
	public boolean equation()
	{
		return true;
	}
	
	public Equation saveEquation()
	{
		reader.resetFocus();
		return reader.saveEquation();
	}
	
	public void useSaved(Equation equation)
	{
		this.equation = equation;
		panel.remove(reader.getPanel());
		equationDisplay.setText("<html>" + equation + "<html>");
		enterPanel.add(generateEnter());
		stoicPanel.setVisible(true);
		panel.repaint();
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		if(toSave.size() == 0) return 0;
		Object selected = JOptionPane.showInputDialog(panel, "Choose which number to save", "Save Number", JOptionPane.PLAIN_MESSAGE, 
				null, toSave.toArray(), toSave.get(0));
		reader.resetFocus();
		if(selected instanceof Double) return (Double)selected;
		return 0;
	}
	
	public void useSavedNumber(double num)
	{
		ArrayList<String> compoundString = new ArrayList<String>();
		for(int index = 0; index < compounds.size(); index++)
		{
			Compound c = list.get(index);
			String string = c.toString();
			if(c.getNum() != 1) compoundString.add("<html>" + string.substring(1) + "</html>");
			else compoundString.add("<html>" + string + "</html>");
		}
		Object selected = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, compoundString.toArray(), compoundString.get(0));
		reader.resetFocus();
		if(selected instanceof String)
		{
			compounds.get(compoundString.indexOf((String)selected)).setText("" + num);
		}
	}
	
	public String getHelp()
	{
		return "<html>Enter your reaction. Once it is balanced, click<br>"
				+ "the \"Use\" button. For each reactant for which the<br>"
				+ "amount is known, have the checkbox selected, and<br>"
				+ "enter the amount of that reactant. Once all known<br>"
				+ "reactants are entered, click the \"Calculate\" button<br>"
				+ "to find out which is the limiting reactant.</html>";
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}