/*
 * Parses and balances an equation. Displays equations with latex.
 * equation() returns true- saves latest balanced equation and can display a saved one.
 * 
 * Author: Julia McClellan, Hyun Choi
 * Version: 1/6/2016
 */

package Functions;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ChemHelper.InvalidInputException;
import Equation.Equation;

public class EquationReader extends Function
{
	private JPanel panel, enterPanel, panel1, resultPanel;
	private JTextField enter;
	private JLabel instructions, result, examples, balanced;
	private JButton button;
	private Equation equation;
	
	public EquationReader()
	{
		super("Equation Reader");
		enter = new JTextField("Enter your equation here", 25);
		result = new JLabel();
		balanced = new JLabel();
		instructions = new JLabel("<html>When entering an equation, use the following guidelines:<br>\t\u2022In a compound, put \"/\" between each element"
				+ "<br>\t\u2022If an element has a charge, put it after \"^\"<br>\t\u2022Put coefficients after charges and following \".\"<br>\t\u2022"
				+ "Separate compounds with \"+\"<br>\t\u2022In place of an arrow, put \"=\"<br>\t\u2022Do not include spaces");
		examples = new JLabel("<html>Examples of acceptable equations include:<br>\t\u20222Na^-1+2Cl^1=2Na^-1/Cl^1<br>\t\u20226C/O.2+6H.2/O=C.6/H.12/O.6+6O.2"
				+ "<br>\t\u20224Fe/S+7O.2=2Fe.2/O.3+4S/O.2</html>");
		button = new JButton("Click here!");
		button.addActionListener(new BListener());
		enterPanel = new JPanel();
		enterPanel.add(enter);
		enterPanel.add(button);
		panel1 = new JPanel();
		panel1.setLayout(new GridLayout(2, 1));
		panel1.add(enterPanel);
		resultPanel = new JPanel();
		resultPanel.setLayout(new GridLayout(2, 1));
		resultPanel.add(result);
		resultPanel.add(balanced);
		panel1.add(resultPanel);
		Box boxV = Box.createVerticalBox();
		Box boxH = Box.createHorizontalBox();
		boxH.add(panel1);
		boxH.add(Box.createHorizontalStrut(10));
		boxH.add(instructions);
		boxH.add(Box.createHorizontalStrut(10));
		boxH.add(examples);
		boxV.add(boxH);
		boxV.add(Box.createVerticalStrut(10));
		panel = new JPanel();
		panel.add(boxV);
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
				result.setIcon(latex(equation).getIcon());
				boolean isBalanced = resultant.balance();

				if(isBalanced)
				{
					balanced.setIcon(latex(resultant).getIcon());
				}
				else
				{
					balanced.setText("This equation could not be balanced");
				}
			}
			catch(Throwable e)
			{
				if(!(e instanceof InvalidInputException)) e = new InvalidInputException(-1);
				String output = e.getMessage();
				result.setText(output);
			}
		}
	}
	
	private class EnterField extends JPanel
	{
		private int index;
		private JLabel label;
		private String current;
		private Button arrow, sup, sub;
		
		public EnterField()
		{
			current = "<html>|</html>";
			index = 6;
			label = new JLabel(current);
			arrow = new Button(this, "\u2192", "\u2192", "\u2192");
			sup = new Button(this, "<sup>", "</sup>", "<html>a<sup>b</sup></html>");
			sub = new Button(this, "<sub>", "</sub>", "<html>a<sub>b</sub></html>");
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
				if(arg0.getKeyCode() == 8)
				{
					int goBack = checkBack();
					if(goBack == 0) return;
					else
					{
						current = current.substring(0, index - goBack) + current.substring(index);
						index -= goBack;
						label.setText(current);
					}
				}
				else if(arg0.getKeyCode() == 39)
				{
					//right arrow
				}
				else if(arg0.getKeyCode() == 37)
				{
					//left arrow
				}
			}
			
			public void keyTyped(KeyEvent arg0)
			{
				enter(arg0.getKeyChar() + "");
			}
			
			public void keyReleased(KeyEvent arg0){} 
			private int checkAhead()
			{
				if(index == 6) return 0;
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
				if(current.substring(index - 6, index).equals("</sup>")) return 5;
				if(current.substring(index - 6, index).equals("</sub>")) return 5;
				return 1;
			}
		}
		
		private class Button extends JButton
		{
			private String add1, add2;
			private boolean on;
			private EnterField field;
			
			public Button(EnterField field, String add1, String add2, String display)
			{
				super(display);
				this.add1 = add1;
				this.add2 = add2;
				on = false;
				this.field = field;
				addActionListener(new ButtonListener());
			}
			
			private class ButtonListener implements ActionListener
			{
				public void actionPerformed(ActionEvent arg0)
				{
					if(!on) field.enter(add1);
					else field.enter(add2);
					on = !on;
					field.grabFocus();
				}
			}
		}
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
		return equation;
	}
	
	public void useSaved(Equation equation)
	{
		this.equation = equation;
		JLabel label = latex(equation);
		result.setIcon(label.getIcon());
	}
	/*
	public EquationReader()
	{
		super("L");
		JFrame frame = new JFrame();
		frame.add(new EnterField());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		new EquationReader();
	}*/
}