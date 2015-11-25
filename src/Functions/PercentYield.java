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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import ChemHelper.Equation;
import Elements.Compound;

public class PercentYield extends Function 
{
	private JPanel panel, equationPanel, stoicPanel, displayEquation, reactantPanel, productPanel, resultPanel;
	private EquationReader reader;
	private JButton acceptEquation, calculate, reset;
	private Equation equation;
	private JLabel errorMessage, instructions;
	private boolean onReactant = true, done = false;
	private JTextField enterR, enterP;
	private JRadioButton mole1, gram1, mole2, gram2;
	private Compound reactant, product;
	private Box box1, box2;
	
	public PercentYield()
	{
		super("Percent Yield");
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
		
		panel = new JPanel();
		panel.add(box1);
		panel.add(stoicPanel);
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
				double amount = Double.parseDouble(enterR.getText()), amount1 = Double.parseDouble(enterP.getText()), 
						amount2 = Stoichiometry.calculate(reactant, amount, gram1.isSelected(), product, gram2.isSelected()),
						percent = amount1 / amount2 * 100;
				resultPanel.removeAll();
				resultPanel.add(new JLabel("The percent yield was " + percent));
				reset.setVisible(true);
				resultPanel.setVisible(true);
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
	
	public JPanel getPanel()
	{
		return panel;
	}
}