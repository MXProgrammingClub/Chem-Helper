/*
 * Parses and balances an equation. Displays equations with latex.
 * equation() returns true- saves latest balanced equation and can display a saved one.
 * 
 * Author: Julia McClellan, Hyun Choi
 * Version: 2/5/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ChemHelper.InvalidInputException;
import Equation.Equation;
import HelperClasses.TextField;

public class EquationReader extends Function
{
	private JPanel panel;
	private TextField enter;
	private JLabel instructions, result, balanced;
	private JButton button, help, use;
	private Equation equation;
	
	public EquationReader()
	{
		this(null);
	}
	
	public EquationReader(final Function f)
	{
		super("Equation Balancer");
		
		instructions = new JLabel("Type your equation below:");
		enter = new TextField();
		result = new JLabel();
		balanced = new JLabel();
		
		button = new JButton("Balance");
		button.addActionListener(new BListener());
		help = new JButton("Help");
		help = new JButton("Help");
		help.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						JOptionPane.showMessageDialog(panel, "<html>" + TextField.getHelp() + "</html>", "Help", JOptionPane.QUESTION_MESSAGE);
					}
				});
		JPanel buttons = new JPanel();
		buttons.add(help);
		buttons.add(button);

		JPanel box = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		box.add(instructions, c);
		c.gridy = 1;
		box.add(enter, c);
		c.gridy = 2;
		box.add(buttons, c);
		c.gridy = 3;
		box.add(result, c);
		c.gridy = 4;
		box.add(balanced, c);
		
		if(f != null)
		{
			use = new JButton("Use");
			use.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						if(equation != null) f.useSaved(equation);
					}
				});
			use.setVisible(false);
			c.gridy = 5;
			box.add(use, c);
		}
		
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
			result.setIcon(null);
			balanced.setText("");
			String input = enter.getText();
			try
			{
				equation = Equation.parseEquation(input);
				boolean isBalanced = equation.balance();
				result.setIcon(latex(equation).getIcon());
				if(!isBalanced) balanced.setText("This equation could not be balanced");
				if(use != null)
				{
					panel.setVisible(false);
					use.setVisible(true);
					panel.setVisible(true);
				}
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
		result.setIcon(latex(equation).getIcon());
	}
	
	public boolean help()
	{
		return true;
	}
	
	public String getHelp()
	{
		return "<html>" + TextField.getHelp() + "</html>";
	}
}