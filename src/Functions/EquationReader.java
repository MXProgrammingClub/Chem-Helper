package Functions;

import javax.swing.*;

import Elements.Equation;
import Elements.InvalidInputException;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EquationReader extends Function
{
	private JPanel panel, enterPanel, panel1;
	private JTextField enter;
	private JLabel instructions, result, examples;
	private JButton button;
	
	public EquationReader()
	{
		super("Equation Reader");
		enter = new JTextField("Enter your equation here");
		result = new JLabel();
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
		panel1.add(result);
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
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
	
	private class BListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			String input = enter.getText(), output;
			try
			{
				output = "<html>" + Equation.parseEquation(input) + "</html>";
			}
			catch(Throwable e)
			{
				if(!(e instanceof InvalidInputException)) e = new InvalidInputException(-1);
				output = e.getMessage();
			}
			result.setText(output);
		}
	}
}