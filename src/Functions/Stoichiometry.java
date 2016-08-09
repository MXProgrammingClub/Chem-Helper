/*
 * Determines amount of one compound given the amount of another. Shows calculation steps.
 * equation() returns true- has an EquationReader as an instance variable.
 * number() returns true- saves most recently calculated value and uses saved as known amount.
 * 
 * Author: Julia McClellan
 * Version: 4/15/2016
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Equation.Compound;
import Equation.Equation;
import HelperClasses.RadioEnterField;
import HelperClasses.Units;

public class Stoichiometry extends Function 
{
	private JPanel panel, stoicPanel, displayEquation, stepsPanel, box2;
	private EquationReader reader;
	private JButton calculate, reset;
	private Equation equation;
	private JLabel instructions;
	private boolean given, done;
	private Compound known, unknown;
	private double toSave;
	private RadioEnterField field1, field2;
	private GridBagConstraints c;
	
	public Stoichiometry()
	{
		super("Ideal Stoichiometry");
		toSave = 0;
		
		panel = new JPanel();
		setPanel();
	}

	private void setPanel()
	{
		reader = new EquationReader(this);
		
		displayEquation = new JPanel();
		instructions = new JLabel("Click on the compound you know the quantity of.");
		calculate = new JButton("Calculate");
		calculate.addActionListener(new CalculateListener());
		reset = new JButton("Reset");
		reset.addActionListener(new ResetListener());
		stepsPanel = new JPanel();
		stepsPanel.setVisible(false);
		
		box2 = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		box2.add(instructions, c);
		c.gridy++;
		box2.add(displayEquation, c);
		
		stoicPanel = new JPanel();
		stoicPanel.add(box2);
		stoicPanel.setVisible(false);
		
		given = true;
		done = false;
		
		panel.add(reader.getPanel());
		panel.add(stoicPanel);
		panel.add(stepsPanel);
	}
	
	public JPanel getPanel() 
	{
		return panel;
	}
	
	private JPanel displayEquation()
	{
		ArrayList<Compound> left = equation.getLeft(), right = equation.getRight();
		JPanel thisPanel = new JPanel();
		thisPanel.add(generateSide(left));
		thisPanel.add(new JLabel("\u2192"));
		thisPanel.add(generateSide(right));
		return thisPanel;
	}
	
	private JPanel generateSide(ArrayList<Compound> side)
	{
		JPanel sidePanel = new JPanel();
		sidePanel.add(new CompoundLabel(side.get(0)));
		for(int index = 1; index < side.size(); index++)
		{
			sidePanel.add(new JLabel("+"));
			sidePanel.add(new CompoundLabel(side.get(index)));
		}
		return sidePanel;
	}
	
	private class CompoundLabel extends JLabel
	{
		Compound compound;
		
		public CompoundLabel(Compound compound)
		{
			super("<html>" + compound.withoutCharge() + "<html>");
			this.compound = compound;
			addMouseListener(new CompoundListener());
		}
		
		private class CompoundListener implements MouseListener
		{
			public void mouseClicked(MouseEvent arg0) 
			{
				if(given)
				{
					known = compound;
					String name;
					if(known.getNum() == 1) name = "<html>" + compound.withoutCharge() + "</html>";
					else name = "<html>" + compound.withoutCharge().substring(1) + "</html>";
					field1 = new RadioEnterField(name, true, "Mass", "Amount", false);
					c.gridy++;
					box2.add(field1, c);
					instructions.setText("Now click on the compound of which you wish to find the quanitity");
					given = false;
				}
				else if (!done)
				{
					unknown = compound;
					String name;
					if(unknown.getNum() == 1) name = "<html>" + compound.withoutCharge() + "</html>";
					else name = "<html>" + compound.withoutCharge().substring(1) + "</html>";
					field2 = new RadioEnterField(name, false, "Mass", "Amount", false);
					c.gridy++;
					box2.add(field2, c);
					c.gridy++;
					box2.add(calculate, c);
					instructions.setText("Once you have entered the quantities and units, click the calculate button");
					done = true;
				}
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		}
	}
	
	private class CalculateListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			String resultString;
			try
			{
				int sigFigs = field1.getSigFigs();
				double amount = field1.getAmount();
				if(amount == Units.ERROR_VALUE || amount == Units.UNKNOWN_VALUE) return;
				String calculated = "<html>" + calculate(known, amount, field1.unit1(), unknown, field2.unit1());
				
				double result;
				if(field2.unit1()) result = Double.parseDouble(calculated.substring(calculated.lastIndexOf("=") + 1, calculated.lastIndexOf("g")));
				else result = Double.parseDouble(calculated.substring(calculated.lastIndexOf("=") + 1, calculated.lastIndexOf("mol")));
				
				toSave = field2.getBlankAmount(result);
				if(toSave != result) calculated += "<br>" + result + (field2.unit1() ? " g = " : " mol = ") + toSave + " " + field2.getUnit();
				resultString = Function.withSigFigs(toSave, sigFigs) + " " + field2.getUnit();
				stepsPanel.add(new JLabel(calculated));
				stepsPanel.setVisible(true);
			}
			catch(Throwable e)
			{
				resultString = "There was a problem with your input";
			}
			c.gridy++;
			box2.add(new JLabel(resultString), c);
			c.gridy++;
			box2.add(reset, c);
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
	
	public static String calculate(Compound c1, double amount, boolean inGrams1, Compound c2, boolean inGrams2)
	{
		double moles;
		String steps = "", c1Name = c1.toString(), c2Name = c2.toString();
		if(c1.getNum() != 1) c1Name = c1Name.substring(1);
		if(c2.getNum() != 1) c2Name = c2Name.substring(1);
		if(inGrams1) 
		{
			StringBuffer[] molarMass = new StringBuffer[2];
			double mass = c1.getMolarMassSteps(molarMass);
			steps += "Calculate the molar mass of " + c1Name + ":<br>\u2003" + molarMass;
			moles = amount / mass;
			steps += "<br>Convert " + c1Name + " from grams to moles:<br>\u2003" + amount + " g / " + mass + " g/mol = " + moles + " mol<br>";
		}
		else moles = amount;
		double molesC2 = moles / c1.getNum() * c2.getNum();
		steps += "Multiply by the mole ratio of " + c1Name + " and " + c2Name + ":<br>\u2003" + moles + " mol " + c1Name + " * " + c2.getNum() + " mol " + 
				c2Name + " / " + c1.getNum() + " mol " + c1Name +  " = " + molesC2 + " mol " + c2Name;
		if(!inGrams2) return steps;
		else
		{
			StringBuffer[] molarMass = new StringBuffer[2];
			double mass = c2.getMolarMassSteps(molarMass), answer = mass * molesC2;
			steps += "<br>Calculate the molar mass of " + c2Name + ": <br>\u2003" + molarMass;
			steps += "<br>Convert " + c2Name + " from moles to grams:<br>\u2003" + mass + " g/mol * " + molesC2 + " mol = " + answer + " g";
			return steps;
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
		displayEquation.add(displayEquation());
		stoicPanel.setVisible(true);
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		reader.resetFocus();
		return toSave;
	}
	
	public void useSavedNumber(double num)
	{
		reader.resetFocus();
		if(field1 != null) field1.setText("" + num);
	}
	
	public String getHelp()
	{
		return "<html>Enter the equation and after balancing it, click<br>"
				+ "the \"Use\" button. Click on the compound whose<br>"
				+ "amount is given, then the one whose amount is<br>"
				+ "unknown. Enter the amount of the known compound,<br>"
				+ "and select whether it is in moles or grams, then<br>"
				+ "choose whether the unknown value should be in<br>"
				+ "moles or grams. Click the \"Calculate\" button for<br>"
				+ "ChemHelper to find the unknown amount. To perform<br>"
				+ "calculations with another reaction, click the<br>"
				+ "\"Reset\" button.</html>";
	}
}