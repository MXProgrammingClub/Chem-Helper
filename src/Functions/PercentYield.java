package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;

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

/**
 * File: PercentYield.java
 * Package: Functions
 * Version: 10/1/2016
 * Authors: Julia McClellan
 * -----------------------------------------------
 * Given an amount of a product and of a reactant, calculates the percent yield of a reaction.
 */
public class PercentYield extends Function 
{
	private JPanel panel, stoicPanel, displayEquation, reactantPanel, productPanel, resultPanel;
	private EquationReader reader;
	private JButton calculate, reset;
	private Equation equation;
	private JLabel errorMessage, instructions;
	private boolean onReactant, done;
	private RadioEnterField fieldR, fieldP;
	private Compound reactant, product;
	private Box box2, steps;
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
		steps = Box.createVerticalBox();		
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
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.anchor = GridBagConstraints.NORTHWEST;
		subpanel.add(stoicPanel, g);
		g.gridy++;
		subpanel.add(Box.createHorizontalStrut(10), g);
		g.gridx++;
		subpanel.add(steps, g);
		panel.add(subpanel);
		
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
							fieldR = new RadioEnterField(name, true, "Mass", "Amount", false);
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
							fieldP = new RadioEnterField(name, true, "Mass", "Amount", false);
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
			if(fieldR.isEmpty() ||fieldP.isEmpty()) return; 
			resultPanel.setVisible(false);
			steps.removeAll();
			double amount = fieldR.getAmount(), actual = fieldP.getAmount();
			if(amount == Units.ERROR_VALUE || amount == Units.UNKNOWN_VALUE)
			{
				errorMessage.setText("There was a problem with your input");
				return;
			}
			
			int sigFigs = Math.min(fieldR.getSigFigs(), fieldP.getSigFigs());
			steps.add(Function.latex("\\text{First, find the theoretical yield:}"));
			LinkedList<String> stepList = new LinkedList<String>();
			double expected = Stoichiometry.calculate(reactant, amount, fieldR.unit1(), product, fieldP.unit1(), stepList);
			for(String step: stepList)
			{
				steps.add(Function.latex("\\hspace{1cm}" + step));
			}
			if(fieldP.unit1())
			{
				double temp = fieldR.getBlankAmount(expected);
				if(temp != expected) steps.add(Function.latex(expected + " g = " + temp + " " + fieldR.getUnit()));
			}
			double percent = 100 * actual / expected;
			toSave = percent;
			String percentString = Function.withSigFigs(percent, sigFigs) + "%", unit = fieldR.getUnit();
			steps.add(Function.latex("\\text{Then divide the actual yield by the theoretical to find the percent yield:}"));
			steps.add(Function.latex("\\frac{" + actual + unit + "}{" + expected + unit + "} * 100 = " + percent + "\\%"));
			resultPanel.removeAll();
			resultPanel.add(new JLabel(percentString));
			reset.setVisible(true);
			resultPanel.setVisible(true);
			steps.setVisible(true);
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
		if(selected == null) return;
		if(selected.equals("Reactant") && fieldR != null) fieldR.setText("" + num);
		else if(selected.equals("Product") && fieldP != null) fieldP.setText("" + num);
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