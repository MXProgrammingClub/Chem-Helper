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

/**
 * File: LimitingReactant.java
 * Package: Functions
 * Version: 08/10/2016
 * Authors: Julia McClellan
 * -----------------------------------------------
 * Given an equation and amounts of each reactant, determines limiting reactant and calculates leftover amounts of excess reactants.
 */
public class LimitingReactant extends Function
{
	private JPanel panel, stoicPanel, enterPanel, resultPanel;
	private EquationReader reader;
	private JButton calculate, reset;
	private Equation equation;
	private JLabel errorMessage, equationDisplay, limitingLabel;
	private Box box2, steps;
	private ArrayList<RadioEnterField> compounds;
	private ArrayList<Compound> list;
	private ArrayList<Double> toSave;
	private RadioEnterField leftover;
	
	/**
	 * Constructs the function.
	 */
	public LimitingReactant()
	{
		super("Limiting Reactant");
		toSave = new ArrayList<Double>();
		
		panel = new JPanel();
		setPanel();
	}
	
	/**
	 * Sets up the panel with all components.
	 */
	private void setPanel()
	{
		reader = new EquationReader(this);

		calculate = new JButton("Calculate");
		calculate.addActionListener(new CalculateListener());
		reset = new JButton("Reset");
		reset.addActionListener(new ResetListener());
		steps = Box.createVerticalBox();
		steps.setVisible(false);
		
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
		subpanel.add(steps, c);
		panel.add(subpanel);
		
		toSave = new ArrayList<Double>();
	}
	
	/**
	 * Creates the enter fields to enter compound amounts.
	 * @return The Box of enter fields.
	 */
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
	
	/**
	 * An action listener for the calculate.
	 */
	private class CalculateListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			double[] amounts = new double[compounds.size()];
			steps.add(Function.latex("\\text{Find which is the limiting reactant by diving moles of each reactant by its coefficient:}"));
			Compound limiting = null;
			int limitIndex = 0, sigFigs = Integer.MAX_VALUE;
			double min = Double.MAX_VALUE;
			for(int index = 0; index < amounts.length; index++)
			{
				if(compounds.get(index).isSelected())
				{
					Compound compound = list.get(index);
					String compoundString = Function.latex(compound, false);
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
						double mass = compound.getMolarMassSteps(molarMass), temp = amount / mass;
						steps.add(Function.latex("\\text{Calculate the molar mass of }" + compoundString + ":"));
						for(StringBuffer str: molarMass) steps.add(Function.latex("\\hspace{1cm}" + str));
						steps.add(Function.latex("\\text{Convert grams to moles by dividing grams of }" + Function.latex(compound, false) 
							+ "\\text{ by its molar mass:}\\frac{" + amount + " g}{" + mass + "\\frac{g}{mol}} = " + temp + "mol"));
						amount = temp;
					}
					amounts[index] = amount / compound.getNum();
					steps.add(Function.latex("\\text{Divide by the coefficient of }" + compoundString + ":\\frac{" + amount + "mol}{"
							+ compound.getNum() + "} = " + amounts[index] + "mol"));
					if(amounts[index] < min)
					{
						min = amounts[index];
						limitIndex = index;
						limiting = compound;
					}
				}
			}
			String limitingString = Function.latex(limiting, false);
			limitingLabel = new JLabel("<html>Limiting Reactant: " + limiting.withoutNumState() + "</html>");
			Box resultBox = Box.createVerticalBox();
			resultBox.add(limitingLabel);
			steps.add(Function.latex("\\text{The smallest of these is }" + limitingString + "\\text{, so it is the limiting reactant.}"));
			for(int index = 0; index < amounts.length; index++)
			{
				Compound compound = list.get(index);
				String compoundString = Function.latex(compound, false);
				double amount = amounts[index];
				amount -= min;
				if(index != limitIndex)
				{
					amount *= compound.getNum();
					steps.add(Function.latex("{\\text{The amount of }" + compoundString + "\\text{ leftover:}"));
					steps.add(Function.latex("(" + amounts[index] + " mol - " + min + " mol)" + " * " + compound.getNum() + " = " + amount + " mol"));
					String unit = "mol";
					if(compounds.get(index).unit1()) 
					{
						double mass = compound.getMolarMass(), temp = amount * mass;
						steps.add(Function.latex("\\text{Convert the amount of }" + compoundString + "\\text{ leftover to grams:}"));
						steps.add(Function.latex("\\hspace{1cm}" + amount + " mol * " + mass + " \\frac{g}{mol} = " + amount + "g"));
						amount = temp;
						unit = leftover.getUnit();
						if(!unit.equals("g")) 
						{
							temp = leftover.getBlankAmount(amount);
							steps.add(Function.latex(amount + " g = " + temp + " " + unit));
						}
					}
					toSave.add(amount);
					String amountString = Function.withSigFigs(amount, sigFigs) + " " + unit;
					resultBox.add(new JLabel("<html>Amount " + compound.withoutNumState() + " remaining: " + amountString));
				}
			}
			resultPanel.add(resultBox);
			resultPanel.setVisible(true);
			steps.setVisible(true);
			box2.add(reset);
			box2.setVisible(true);
		}
	}
	
	/**
	 * An action listener for the reset button.
	 */
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
	
	/**
	 * Restores the focus to the equation reader.
	 */
	@Override
	public void resetFocus()
	{
		reader.resetFocus();
	}
	
	/**
	 * Returns true as equations can be saved from this function.
	 * @return true
	 */
	@Override
	public boolean equation()
	{
		return true;
	}
	
	/**
	 * Gives an equation for the program.
	 * @return The last equation entered.
	 */
	public Equation saveEquation()
	{
		reader.resetFocus();
		return reader.saveEquation();
	}
	
	/**
	 * Uses the parameter equation in the function.
	 * @param equation The saved equation to use.
	 */
	@Override
	public void useSaved(Equation equation)
	{
		this.equation = equation;
		panel.remove(reader.getPanel());
		equationDisplay.setText("<html>" + equation + "<html>");
		enterPanel.add(generateEnter());
		stoicPanel.setVisible(true);
		panel.repaint();
	}
	
	/**
	 * Returns true as the function can save and use saved numbers.
	 * @return true
	 */
	@Override
	public boolean number()
	{
		return true;
	}
	
	/**
	 * Returns a number for the program to save.
	 * @return The number the user selects to save
	 */
	@Override
	public double saveNumber()
	{
		if(toSave.size() == 0) return 0;
		Object selected = JOptionPane.showInputDialog(panel, "Choose which number to save", "Save Number", JOptionPane.PLAIN_MESSAGE, 
				null, toSave.toArray(), toSave.get(0));
		reader.resetFocus();
		if(selected instanceof Double) return (Double)selected;
		return 0;
	}
	
	/**
	 * Allows the user to select where to use the saved number.
	 * @param num The saved number to use.
	 */
	@Override
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
	
	/**
	 * Returns a help string for this function.
	 * @return A help message for the user.
	 */
	@Override
	public String getHelp()
	{
		return "<html>Enter your reaction. Once it is balanced, click<br>"
				+ "the \"Use\" button. For each reactant for which the<br>"
				+ "amount is known, have the checkbox selected, and<br>"
				+ "enter the amount of that reactant. Once all known<br>"
				+ "reactants are entered, click the \"Calculate\" button<br>"
				+ "to find out which is the limiting reactant.</html>";
	}
	
	/**
	 * Returns the panel for this function.
	 * @return The panel containing components for the function.
	 */
	@Override
	public JPanel getPanel()
	{
		return panel;
	}
}