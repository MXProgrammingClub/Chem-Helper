/*
 * Parses and balances an equation. Displays equations with latex.
 * equation() returns true- saves latest balanced equation and can display a saved one.
 * 
 * Author: Julia McClellan, Hyun Choi
 * Version: 1/19/2016
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ChemHelper.InvalidInputException;
import Equation.Equation;
import HelperClasses.TextField;

public class EquationReader extends Function
{
	private JPanel panel;
	private TextField enter;
	private JLabel instructions, result, balanced;
	private JButton button;
	private Equation equation;
	
	public EquationReader()
	{
		super("Equation Reader");
		
		instructions = new JLabel("Type your equation below:");
		enter = new TextField();
		button = new JButton("Balance");
		button.addActionListener(new BListener());
		result = new JLabel();
		balanced = new JLabel();
		
		Box box = Box.createVerticalBox();
		box.add(instructions);
		box.add(enter);
		box.add(button);
		box.add(result);
		box.add(balanced);
		
		panel = new JPanel();
		panel.add(box);
		equation = null;
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
	
	private class BListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			String input = enter.getText();
			try
			{
				equation = Equation.parseEquation(input);
				boolean isBalanced = equation.balance();
				result.setIcon(latex(equation).getIcon());
				if(!isBalanced) balanced.setText("This equation could not be balanced");
			}
			catch(Throwable e)
			{
				if(!(e instanceof InvalidInputException)) e = new InvalidInputException(-1);
				String output = e.getMessage();
				result.setText(output);
			}
			enter.grabFocus();
		}
	}
	
	public void resetFocus()
	{
		enter.grabFocus();
	}
	
	public Equation getEquation()
	{
		return equation;
	}
	
	public boolean equation()
	{
		return true;
	}
	
	public Equation saveEquation()
	{
		enter.grabFocus();
		return equation;
	}
	
	public void useSaved(Equation equation)
	{
		enter.grabFocus();
		this.equation = equation;
		JLabel label = latex(equation);
		result.setIcon(label.getIcon());
	}
}