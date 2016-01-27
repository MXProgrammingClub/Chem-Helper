/*
 * Given an amount of a product and of a reactant, calculates the percent yield of a reaction. Shows calculation steps.
 * equation() returns true- has an EquationReader as an instance variable.
 * number() returns true - saves most recently calculated value, uses saved as product or reactant amount.
 * 
 * Author: Julia McClellan
 * Version: 1/26/2015
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Equation.Compound;
import Equation.Equation;
import HelperClasses.TextField;

public class PercentYield extends Function 
{
	private JPanel panel, equationPanel, stoicPanel, displayEquation, reactantPanel, productPanel, resultPanel, stepsPanel;
	private EquationReader reader;
	private JButton acceptEquation, calculate, reset;
	private Equation equation;
	private JLabel errorMessage, instructions;
	private boolean onReactant, done;
	private JTextField enterR, enterP;
	private JRadioButton mole1, gram1, mole2, gram2;
	private Compound reactant, product;
	private Box box1, box2;
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
		reader = new EquationReader();
		acceptEquation = new JButton("Use equation");
		acceptEquation.addActionListener(new AcceptEquation());
		errorMessage = new JLabel();
		box1 = Box.createVerticalBox();
		box1.add(reader.getPanel());
		equationPanel = new JPanel();
		equationPanel.add(acceptEquation);
		equationPanel.add(errorMessage);
		box1.add(equationPanel);
		
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
		
		box2 = Box.createVerticalBox();
		box2.add(instructions);
		box2.add(displayEquation);
		box2.add(reactantPanel);
		box2.add(productPanel);
		box2.add(resultPanel);
		box2.add(reset);
		stoicPanel = new JPanel();
		stoicPanel.add(box2);
		stoicPanel.setVisible(false);
		
		panel.add(box1);
		panel.add(stoicPanel);
		panel.add(stepsPanel);
		
		onReactant = true;
		done = false;
	}
	
	private class AcceptEquation implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			equation = reader.getEquation();
			if(equation == null)
			{
				errorMessage.setText("You have not entered a valid equation.");
			}
			else
			{
				panel.remove(box1);
				displayEquation.add(displayEquation());
				stoicPanel.setVisible(true);
			}
		}
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
				stoicPanel.setVisible(false);
				stoicPanel.remove(errorMessage);
				if(!done)
				{
					if(onReactant)
					{
						if(equation.getLeft().indexOf(compound) != -1)
						{
							reactant = compound;
							if(reactant.getNum() == 1) reactantPanel.add(new JLabel("<html>" + compound + "</html>"));
							else reactantPanel.add(new JLabel("<html>" + compound.toString().substring(1) + "</html>"));
							enterR = new JTextField(5);
							reactantPanel.add(enterR);
							mole1 = new JRadioButton("Moles");
							gram1 = new JRadioButton("Grams", true);
							ButtonGroup group = new ButtonGroup();
							group.add(gram1);
							group.add(mole1);
							reactantPanel.add(mole1);
							reactantPanel.add(gram1);
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
							if(product.getNum() == 1) productPanel.add(new JLabel("<html>" + compound + "</html>"));
							else productPanel.add(new JLabel("<html>" + compound.toString().substring(1) + "</html>"));
							enterP = new JTextField(5);
							productPanel.add(enterP);
							mole2 = new JRadioButton("Moles");
							gram2 = new JRadioButton("Grams", true);
							ButtonGroup group = new ButtonGroup();
							group.add(gram2);
							group.add(mole2);
							productPanel.add(mole2);
							productPanel.add(gram2);
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
			try
			{
				resultPanel.setVisible(false);
				int sigFigs = Math.min(Function.sigFigs(enterR.getText()), Function.sigFigs(enterP.getText()));
				double amount = Double.parseDouble(enterR.getText()), actual = Double.parseDouble(enterP.getText());
				String steps = "<html>First, find the theoretical yield-<br>" + Stoichiometry.calculate(reactant, amount, gram1.isSelected(), product, 
						gram2.isSelected());
				double expected = Double.parseDouble(steps.substring(steps.lastIndexOf("=") + 1, steps.lastIndexOf("g"))), percent = 100 * actual / expected;
				toSave = percent;
				String percentString = Function.withSigFigs(percent, sigFigs) + "%", unit = "mol";
				if(gram2.isSelected()) unit = "g";
				steps += "<br>Then divide the actual yield by the theoretical to find the percent yield:<br>\u2003" + actual + " " + unit + " / " + expected + 
						" " + unit + " * 100 = " + percent + "%</html>";
				resultPanel.removeAll();
				resultPanel.add(new JLabel(percentString));
				reset.setVisible(true);
				resultPanel.setVisible(true);
				stepsPanel.add(new JLabel(steps));
				stepsPanel.setVisible(true);
			}
			catch(Throwable e)
			{
				errorMessage.setText("There was a problem with your input");
			}
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
		reader.resetFocus();
		reader.useSaved(equation);
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
		if(selected.equals("Reactant") && enterR != null) enterR.setText("" + num);
		else if(selected.equals("Product") && enterP != null) enterP.setText("" + num);
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