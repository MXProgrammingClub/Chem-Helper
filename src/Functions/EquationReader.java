/*
 * Parses and balances an equation. Displays equations with latex.
 * equation() returns true- saves latest balanced equation and can display a saved one.
 * 
 * Author: Julia McClellan, Hyun Choi, Luke Giacalone
 * Version: 3/15/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

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
	private boolean redox;
	private Function f;
	private JRadioButton acid, base;
	private Box box;
	
	public EquationReader()
	{
		this(null, false);
	}
	
	public EquationReader(Function f)
	{
		this(f, false);
	}
	
	public EquationReader(Function func, boolean redox)
	{
		super("Equation Balancer");
		
		instructions = new JLabel("Type your equation below:");
		enter = new TextField();
		result = new JLabel();
		balanced = new JLabel();
		
		if(redox)
		{
			acid = new JRadioButton("Acidic", true);
			base = new JRadioButton("Basic");
			ButtonGroup g = new ButtonGroup();
			g.add(acid);
			g.add(base);
			JPanel buttons = new JPanel();
			buttons.add(acid);
			buttons.add(base);
		}
		
		button = new JButton("Balance");
		button.addActionListener(new BListener());
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
		c.anchor = GridBagConstraints.WEST;
		box.add(instructions, c);
		c.gridy = 1;
		box.add(enter, c);
		c.gridy = 2;
		box.add(buttons, c);
		c.gridy = 3;
		c.gridwidth = 2;
		box.add(result, c);
		c.gridy = 4;
		box.add(balanced, c);
		
		f = func;
		this.redox = redox;
		if(f != null)
		{
			if(f instanceof Redox) use = new JButton("Create Electrochemcial Cell");
			else use = new JButton("Use");
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
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		subpanel.add(box, c);
		c.gridx++;
		subpanel.add(Box.createHorizontalStrut(20), c);
		c.gridx++;
		this.box = Box.createVerticalBox();
		subpanel.add(this.box, c);
		
		panel = new JPanel();
		panel.add(subpanel);
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
			box.setVisible(false);
			box.removeAll();
			result.setIcon(null);
			balanced.setText("");
			String input = enter.getText();
			try
			{
				equation = Equation.parseEquation(input);
				//System.out.println(equation);
				int isBalanced;
				if(!redox)
				{
					isBalanced = equation.balance();
					if(isBalanced == 2) isBalanced = equation.balance2();
				}
				else
				{
					ArrayList<String> steps = new ArrayList<String>();
					isBalanced = equation.balanceRedox(acid.isSelected(), ((Redox)f).getArrays(), steps);
					for(String step: steps) box.add(new JLabel(step));
					box.setVisible(true);
				}
				
				result.setIcon(latex(equation).getIcon());
				if(isBalanced == 0) balanced.setText("This equation could not be balanced programmatically.");
				if(use != null && (!redox || (equation.getLeft().size() == 2 && equation.getRight().size() == 2)))
				{
					panel.setVisible(false);
					use.setVisible(true);
					panel.setVisible(true);
				}
			}
			catch(Throwable e)
			{
				//e.printStackTrace();
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