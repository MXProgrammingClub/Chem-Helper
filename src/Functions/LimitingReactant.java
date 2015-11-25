package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class LimitingReactant extends Function
{
	private JPanel panel, equationPanel, stoicPanel, enterPanel, resultPanel;
	private EquationReader reader;
	private JButton acceptEquation, calculate, reset;
	private Equation equation;
	private JLabel errorMessage, equationDisplay, limitingLabel;
	private Box box1, box2;
	private ArrayList<EnterPanel> compounds;
	private JRadioButton grams, moles;
	
	public LimitingReactant()
	{
		super("Limiting Reactant");
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

		calculate = new JButton("Calculate");
		calculate.addActionListener(new CalculateListener());
		reset = new JButton("Reset");
		reset.addActionListener(new ResetListener());
		
		box2 = Box.createVerticalBox();
		equationDisplay = new JLabel();
		box2.add(equationDisplay);
		enterPanel = new JPanel();
		box2.add(enterPanel);
		resultPanel = new JPanel();
		resultPanel.setVisible(false);
		box2.add(resultPanel);
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
				equationDisplay.setText("<html>" + equation + "<html>");
				enterPanel.add(generateEnter());
				stoicPanel.setVisible(true);
				panel.repaint();
			}
		}
	}
	
	private class EnterPanel extends JPanel
	{
		private Compound compound;
		JRadioButton moles, grams;
		JTextField enter;
		
		public EnterPanel(Compound compound)
		{
			this.compound = compound;
			String compoundString = compound.toString();
			if(compound.getNum() != 1) compoundString = compoundString.substring(1);
			compoundString = "<html>" + compoundString + "</html>";
			add(new JLabel(compoundString));
			enter = new JTextField(5);
			add(enter);
			moles = new JRadioButton("Moles");
			grams = new JRadioButton("Grams", true);
			ButtonGroup group = new ButtonGroup();
			group.add(grams);
			group.add(moles);
			add(moles);
			add(grams);
		}
		
		public Compound getCompound()
		{
			return compound;
		}
		
		public String getAmount()
		{
			return enter.getText();
		}
		
		public boolean inGrams()
		{
			return grams.isSelected();
		}	
	}
	
	private Box generateEnter()
	{
		Box enterBox = Box.createVerticalBox();
		ArrayList<Compound> compounds = equation.getLeft();
		this.compounds = new ArrayList<EnterPanel>();
		for(Compound compound: compounds)
		{
			EnterPanel panel = new EnterPanel(compound);
			this.compounds.add(panel);
			enterBox.add(panel);
		}
		moles = new JRadioButton("Moles");
		grams = new JRadioButton("Grams", true);
		ButtonGroup group = new ButtonGroup();
		group.add(grams);
		group.add(moles);
		JPanel subPanel = new JPanel();
		subPanel.add(new JLabel("Select a unit for the leftover compound: "));
		subPanel.add(moles);
		subPanel.add(grams);
		enterBox.add(subPanel);
		enterBox.add(calculate);
		return enterBox;
	}
	
	private class CalculateListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			double[] amounts = new double[compounds.size()];
			Compound limiting = null;
			int limitIndex = 0;
			double min = Double.MAX_VALUE;
			for(int index = 0; index < amounts.length; index++)
			{
				Compound compound = compounds.get(index).getCompound();
				double amount;
				enterPanel.remove(errorMessage);
				panel.repaint();
				try
				{
					amount = Double.parseDouble(compounds.get(index).getAmount());
				}
				catch(Throwable e)
				{
					enterPanel.setVisible(false);
					errorMessage.setText("There was a problem with your input");
					enterPanel.add(errorMessage);
					enterPanel.setVisible(true);
					return;
				}
				if(compounds.get(index).inGrams()) amount = amount / compound.getMolarMass();
				amount = amount / compound.getNum();
				amounts[index] = amount;
				if(amount < min)
				{
					min = amount;
					limitIndex = index;
					limiting = compound;
				}
			}
			String limitingString = limiting.toString();
			if(limiting.getNum() != 1) limitingString = limitingString.substring(1);
			limitingLabel = new JLabel("<html>Limiting Reactant: " + limitingString + "</html");
			Box resultBox = Box.createVerticalBox();
			resultBox.add(limitingLabel);
			for(int index = 0; index < amounts.length; index++)
			{
				Compound compound = compounds.get(index).getCompound();
				double amount = amounts[index];
				amount -= min;
				if(index != limitIndex)
				{
					amount *= compound.getNum();
					if(compounds.get(index).inGrams()) amount *= compound.getMolarMass();
					String compoundString = compound.toString();
					if(compound.getNum() != 1) compoundString = compoundString.substring(1);
					resultBox.add(new JLabel("<html>Amount " + compoundString + " remaining: " + amount));
				}
			}
			resultPanel.add(reset);
			resultPanel.add(resultBox);
			resultPanel.setVisible(true);
		}
	}
	
	private class ResetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			LimitingReactant newPanel = new LimitingReactant();
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