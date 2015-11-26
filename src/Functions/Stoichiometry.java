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

public class Stoichiometry extends Function 
{
	private JPanel panel, equationPanel, stoicPanel, displayEquation, knownPanel, unknownPanel, resultPanel;
	private EquationReader reader;
	private JButton acceptEquation, calculate, reset;
	private Equation equation;
	private JLabel errorMessage, instructions;
	private boolean given = true, done = false;
	private JTextField enter;
	private JRadioButton mole1, gram1, mole2, gram2;
	private Compound known, unknown;
	private Box box1, box2;
	
	public Stoichiometry()
	{
		super("Ideal Stoichiometry");
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
		instructions = new JLabel("Click on the compound you know the quantity of.");
		knownPanel = new JPanel();
		unknownPanel = new JPanel();
		resultPanel = new JPanel();
		resultPanel.setVisible(false);
		calculate = new JButton("Calculate");
		calculate.addActionListener(new CalculateListener());
		reset = new JButton("Reset");
		reset.addActionListener(new ResetListener());
		
		box2 = Box.createVerticalBox();
		box2.add(instructions);
		box2.add(displayEquation);
		box2.add(knownPanel);
		box2.add(unknownPanel);
		box2.add(resultPanel);
		stoicPanel = new JPanel();
		stoicPanel.add(box2);
		stoicPanel.setVisible(false);
		
		panel = new JPanel();
		panel.add(box1);
		panel.add(stoicPanel);
	}

	public JPanel getPanel() 
	{
		return panel;
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
				if(given)
				{
					known = compound;
					if(known.getNum() == 1) knownPanel.add(new JLabel("<html>" + compound + "</html>"));
					else knownPanel.add(new JLabel("<html>" + compound.toString().substring(1) + "</html>"));
					enter = new JTextField(5);
					knownPanel.add(enter);
					mole1 = new JRadioButton("Moles");
					gram1 = new JRadioButton("Grams", true);
					ButtonGroup group = new ButtonGroup();
					group.add(gram1);
					group.add(mole1);
					knownPanel.add(mole1);
					knownPanel.add(gram1);
					instructions.setText("Now click on the compound of which you wish to find the quanitity");
					given = false;
				}
				else if (!done)
				{
					unknown = compound;
					if(unknown.getNum() == 1) unknownPanel.add(new JLabel("<html>" + compound + "</html>"));
					else unknownPanel.add(new JLabel("<html>" + compound.toString().substring(1) + "</html>"));
					mole2 = new JRadioButton("Moles");
					gram2 = new JRadioButton("Grams", true);
					ButtonGroup group = new ButtonGroup();
					group.add(gram2);
					group.add(mole2);
					unknownPanel.add(mole2);
					unknownPanel.add(gram2);
					unknownPanel.add(calculate);
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
				int sigFigs = Function.sigFigs(enter.getText());
				double calculated = calculate(known, Double.parseDouble(enter.getText()), gram1.isSelected(), unknown, gram2.isSelected());
				resultString = Function.withSigFigs(calculated, sigFigs);
			}
			catch(Throwable e)
			{
				resultString = "There was a problem with your input";
			}
			resultPanel.add(new JLabel(resultString));
			resultPanel.add(reset);
			resultPanel.setVisible(true);
		}
	}
	
	private class ResetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			Stoichiometry newPanel = new Stoichiometry();
			panel.setVisible(false);
			panel.removeAll();
			panel.add(newPanel.getPanel());
			panel.setVisible(true);
		}
	}
	
	public static double calculate(Compound c1, double amount, boolean inGrams1, Compound c2, boolean inGrams2)
	{
		double moles;
		if(inGrams1) moles = amount / c1.getMolarMass();
		else moles = amount;
		double molesC2 = moles / c1.getNum() * c2.getNum();
		if(!inGrams2) return molesC2;
		else return c2.getMolarMass() * molesC2;
	}
}