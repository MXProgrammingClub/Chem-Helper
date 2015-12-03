package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ChemHelper.Compound;
import ChemHelper.InvalidInputException;

public class Effusion extends Function 
{
	private JPanel panel;
	private JLabel instruction, result;
	private JTextField comp1, comp2;
	private JButton calculate;
	private Box box;
	
	public Effusion()
	{
		super("Law of Effusion");
		
		instruction = new JLabel("Enter the two compounds.");
		comp1 = new JTextField(5);
		comp2 = new JTextField(5);
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		result = new JLabel();
		
		box = Box.createVerticalBox();
		box.add(instruction);
		box.add(Box.createVerticalStrut(5));
		box.add(comp1);
		box.add(comp2);
		box.add(Box.createVerticalStrut(5));
		box.add(calculate);
		box.add(Box.createVerticalStrut(10));
		box.add(result);
		
		panel = new JPanel();
		panel.add(box);
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				Compound c1 = Compound.parseCompound(comp1.getText());
				Compound c2 = Compound.parseCompound(comp2.getText());
				double results = Math.sqrt(c1.getMolarMass() / c2.getMolarMass());
				result.setText("" + results);
			}
			catch(Throwable e)
			{
				if(!(e instanceof InvalidInputException))
				{
					e = new InvalidInputException(0);
				}
				result.setText(((InvalidInputException)e).getMessage());
			}
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}
