/*
 * Parses and balances an equation. Displays equations with latex.
 * equation() returns true- saves latest balanced equation and can display a saved one.
 * 
 * Author: Julia McClellan, Hyun Choi
 * Version: 1/13/2016
 */

package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ChemHelper.InvalidInputException;
import Equation.Equation;

public class EquationReader extends Function
{
	private JPanel panel;
	private EnterField enter;
	private JLabel instructions, result, balanced;
	private JButton button;
	private Equation equation;
	
	public EquationReader()
	{
		super("Equation Reader");
		
		instructions = new JLabel("Type your equation below:");
		enter = new EnterField();
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
				Equation resultant = Equation.parseEquation(input);

				equation = resultant;
				boolean isBalanced = resultant.balance();

				result.setIcon(latex(resultant).getIcon());

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
	
	private class EnterField extends JPanel
	{
		private int index;
		private JLabel label;
		private String current;
		private Button sup, sub;
		private JButton arrow;
		
		public EnterField()
		{
			current = "<html>|</html>";
			index = 6;
			label = new JLabel(current);
			arrow = new JButton("\u2192");
			final EnterField field = this;
			arrow.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent arg0)
						{
							enter("\u2192");
							field.grabFocus();
						}
					});
			sup = new Button(this, "<sup>", "</sup>", "<html>a<sup>b</sup></html>");
			sub = new Button(this, "<sub>", "</sub>", "<html>a<sub>b</sub></html>");
			sup.addOther(sub);
			sub.addOther(sup);
			this.addKeyListener(new Key());
			JPanel buttons = new JPanel();
			buttons.add(arrow);
			buttons.add(sup);
			buttons.add(sub);
			Box box = Box.createVerticalBox();
			box.add(label);
			box.add(buttons);
			add(box);
			setFocusable(true);
		}
		
		public String getText()
		{
			return current;
		}
		
		private void enter(String enter)
		{
			current = current.substring(0, index) + enter + current.substring(index);
			index += enter.length();
			label.setText(current);
		}
		
		private class Key implements KeyListener
		{
			public void keyPressed(KeyEvent arg0)
			{
				if(arg0.getKeyCode() == 8) //backspace
				{
					int goBack = checkBack();
					if(goBack == 0) return;
					current = current.substring(0, index - goBack) + current.substring(index);
					index = index - goBack;
					label.setText(current);
				}
				else if(arg0.getKeyCode() == 127) //delete
				{
					int goAhead = checkAhead();
					if(goAhead == 0) return;
					current = current.substring(0, index + 1) + current.substring(index + 1 + goAhead);
					label.setText(current);
				}
				else if(arg0.getKeyCode() == 39) //right arrow
				{
					int newIndex = index + checkAhead();
					current = current.substring(0, index) + current.substring(index + 1, newIndex + 1) + '|' + current.substring(newIndex + 1);
					index = newIndex;
					label.setText(current);
				}
				else if(arg0.getKeyCode() == 37) //left arrow
				{
					int newIndex = index - checkBack();
					current = current.substring(0, newIndex ) + '|' + current.substring(newIndex, index) + current.substring(index + 1);
					index = newIndex;
					label.setText(current);
				}
			}
			
			public void keyTyped(KeyEvent arg0)
			{
				char ch = arg0.getKeyChar();
				if(ch != (char) 8 && ch != (char) 127 && ch != (char) 27 && ch != (char) 10) //backspace, delete, escape, and enter call this but shouldn't
				{
					if(ch == '^' && !sub.isOn()) sup.toggle();
					else if(ch == '_' && !sup.isOn()) sub.toggle();
					else if(ch != '^' && ch != '_') enter(ch + "");
				}
			}
			public void keyReleased(KeyEvent arg0){} 
			
			private int checkAhead()
			{
				if(index == current.length() - 8) return 0;
				if(current.substring(index + 1, index + 7).equals("\u2192")) return 6;
				if(current.substring(index + 1, index + 6).equals("<sup>")) return 5;
				if(current.substring(index + 1, index + 6).equals("<sub>")) return 5;
				if(current.substring(index + 1, index + 7).equals("</sup>")) return 5;
				if(current.substring(index + 1, index + 7).equals("</sub>")) return 5;
				return 1;
			}
			
			private int checkBack()
			{
				if(index == 6) return 0;
				if(current.substring(index - 6, index).equals("\u2192")) return 6;
				if(current.substring(index - 5, index).equals("<sup>")) return 5;
				if(current.substring(index - 5, index).equals("<sub>")) return 5;
				if(current.substring(index - 6, index).equals("</sup>")) return 6;
				if(current.substring(index - 6, index).equals("</sub>")) return 6;
				return 1;
			}
		}
		
		private class Button extends JButton
		{
			private String add1, add2;
			private boolean on;
			private EnterField field;
			private Button other;
			
			public Button(EnterField field, String add1, String add2, String display)
			{
				super(display);
				this.add1 = add1;
				this.add2 = add2;
				on = false;
				this.field = field;
				addActionListener(new ButtonListener());
			}
			
			public void addOther(Button other)
			{
				this.other = other;
			}
			
			public void toggle()
			{
				if(!other.isOn())
				{
					if(!on) field.enter(add1);
					else field.enter(add2);
					on = !on;
				}
				field.grabFocus();
			}
			
			public boolean isOn()
			{
				return on;
			}
			
			private class ButtonListener implements ActionListener
			{
				public void actionPerformed(ActionEvent arg0)
				{
					toggle();
				}
			}
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