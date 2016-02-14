/*
 * Given an amount of a product and of a reactant, calculates the percent yield of a reaction. Shows calculation steps.
 * equation() returns true- has an EquationReader as an instance variable.
 * number() returns true - saves most recently calculated value, uses saved as product or reactant amount.
 * 
 * Author: Julia McClellan
 * Version: 2/13/2015
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Equation.Compound;
import Equation.Equation;
import HelperClasses.RadioEnterField;
import HelperClasses.TextField;
import HelperClasses.Units;

public class PercentYield extends Function 
{
	private JPanel panel, stoicPanel, displayEquation, reactantPanel, productPanel, resultPanel, stepsPanel;
	private EquationReader reader;
	private JButton calculate, reset;
	private Equation equation;
	private JLabel errorMessage, instructions;
	private boolean onReactant, done;
	private RadioEnterField fieldR, fieldP;
	private Compound reactant, product;
	private Box box2;
	private double toSave;
	
	public PercentYield()
	{
		super("Percent Yield");
		toSave = 0;
		
		panel = new JPanel();
		setPanel();
	}
	
	private void setPanel()
	{
		reader = new EquationReader(this);
		
		displayEquation = new JPanel();
		instructions = new JLabel("Click on the reactant you know the quantity of.");
		reactantPanel = new JPanel();
		productPanel = new JPanel();
		resultPanel = new JPanel();
		resultPanel.setVisible(false);
		calculate = new JButton("Calculate");
		calculate.addActionListener(new CalculateListener());
		reset = new JButton("Reset");
		reset.addActionListener(new ResetListener());
		reset.setVisible(false);
		stepsPanel = new JPanel();
		stepsPanel.setVisible(false);
		errorMessage = new JLabel();
		
		box2 = Box.createVerticalBox();
		box2.add(instructions);
		box2.add(displayEquation);
		box2.add(reactantPanel);
		box2.add(productPanel);
		box2.add(resultPanel);
		box2.add(errorMessage);
		box2.add(reset);
		stoicPanel = new JPanel();
		stoicPanel.add(box2);
		stoicPanel.setVisible(false);
		
		panel.add(reader.getPanel());
		panel.add(stoicPanel);
		panel.add(stepsPanel);
		
		onReactant = true;
		done = false;
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
			super("<html>" + compound + "<html>");
			this.compound = compound;
			addMouseListener(new CompoundListener());
		}
		
		private class CompoundListener implements MouseListener
		{
			public void mouseClicked(MouseEvent arg0) 
			{
				errorMessage.setText("");
				if(!done)
				{
					stoicPanel.setVisible(false);
					if(onReactant)
					{
						if(equation.getLeft().indexOf(compound) != -1)
						{
							reactant = compound;
							String name;
							if(reactant.getNum() == 1) name = "<html>" + compound + "</html>";
							else name = "<html>" + compound.toString().substring(1) + "</html>";
							fieldR = new RadioEnterField(name, true, "Mass", "Amount");
							reactantPanel.add(fieldR);
							instructions.setText("Now click on the product you know the quantity of.");
							onReactant = false;
						}
						else
						{
							errorMessage.setText("That is not a reactant");
							stoicPanel.add(errorMessage);				
						}
					}
					else
					{
						if(equation.getRight().indexOf(compound) != -1)
						{
							product = compound;
							String name;
							if(product.getNum() == 1) name = "<html>" + compound + "</html>";
							else name = "<html>" + compound.toString().substring(1) + "</html>";
							fieldP = new RadioEnterField(name, true, "Mass", "Amount");
							productPanel.add(fieldP);
							productPanel.add(calculate);
							instructions.setText("Once you have entered the quantities and units, click the calculate button");
							done = true;
						}
						else
						{
							errorMessage.setText("That is not a product");
							stoicPanel.add(errorMessage);
						}
					}
					stoicPanel.setVisible(true);
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
			resultPanel.setVisible(false);
			
			double amount = fieldR.getAmount(), actual = fieldP.getAmount();
			if(amount == Units.ERROR_VALUE || amount == Units.UNKNOWN_VALUE)
			{
				errorMessage.setText("There was a problem with your input");
				return;
			}
			
			int sigFigs = Math.min(fieldR.getSigFigs(), fieldP.getSigFigs());
			String steps = "<html>First, find the theoretical yield-<br>" + Stoichiometry.calculate(reactant, amount, fieldR.unit1(), product, fieldP.unit1());
			double expected;
			if(fieldP.unit1())
			{
				expected = Double.parseDouble(steps.substring(steps.lastIndexOf("=") + 1, steps.lastIndexOf("g")));
				double temp = fieldR.getBlankAmount(expected);
				if(temp != expected) steps += "<br>" + expected + " g = " + temp + " " + fieldR.getUnit();
			}
			else expected = Double.parseDouble(steps.substring(steps.lastIndexOf("=") + 1, steps.lastIndexOf("mol"))); 
			double percent = 100 * actual / expected;
			toSave = percent;
			String percentString = Function.withSigFigs(percent, sigFigs) + "%", unit = fieldR.getUnit();
			steps += "<br>Then divide the actual yield by the theoretical to find the percent yield:<br>\u2003" + actual + " " + unit + " / " + expected + 
					" " + unit + " * 100 = " + percent + "%</html>";
			resultPanel.removeAll();
			resultPanel.add(new JLabel(percentString));
			reset.setVisible(true);
			resultPanel.setVisible(true);
			stepsPanel.add(new JLabel(steps));
			stepsPanel.setVisible(true);
		}
	}
	
	private class ResetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			PercentYield newPanel = new PercentYield();
			panel.setVisible(false);
			panel.removeAll();
			panel.add(newPanel.getPanel());
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
		String[] options = {"Reactant", "Product"};
		Object selected = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, options, "Reactant");
		reader.resetFocus();
		if(selected.equals("Reactant") && fieldR != null) fieldR.setText("" + num);
		else if(selected.equals("Product") && fieldP != null) fieldP.setText("" + num);
	}
	
	public boolean help()
	{
		return true;
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