package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import ChemHelper.Compound;
import ChemHelper.InvalidInputException;

public class Effusion extends Function 
{
	private JPanel panel, c1Panel, c2Panel, ratePanel;
	private JLabel instruction, result, c1Label, c2Label, rateLabel;
	private JTextField comp1, comp2, ratio;
	private JRadioButton formula1, formula2, mass1, mass2;
	private JButton calculate;
	private Box box;
	private double toSave;
	
	public Effusion()
	{
		super("Law of Effusion");
		
		instruction = new JLabel("Enter what you know.");
		
		c1Label = new JLabel("Compound 1: ");
		comp1 = new JTextField(5);
		formula1 = new JRadioButton("Formula", true);
		mass1 = new JRadioButton("Molar mass");
		ButtonGroup g1 = new ButtonGroup();
		g1.add(formula1);
		g1.add(mass1);
		c1Panel = new JPanel();
		c1Panel.add(c1Label);
		c1Panel.add(comp1);
		c1Panel.add(formula1);
		c1Panel.add(mass1);

		c2Label = new JLabel("Compound 2: ");
		comp2 = new JTextField(5);
		formula2 = new JRadioButton("Formula", true);
		mass2 = new JRadioButton("Molar mass");
		ButtonGroup g2 = new ButtonGroup();
		g2.add(formula2);
		g2.add(mass2);
		c2Panel = new JPanel();
		c2Panel.add(c2Label);
		c2Panel.add(comp2);
		c2Panel.add(formula2);
		c2Panel.add(mass2);
		
		rateLabel = new JLabel("Ratio of rates: ");
		ratio = new JTextField(5);
		ratePanel = new JPanel();
		ratePanel.add(rateLabel);
		ratePanel.add(ratio);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		result = new JLabel();
		
		box = Box.createVerticalBox();
		box.add(instruction);
		box.add(Box.createVerticalStrut(5));
		box.add(c1Panel);
		box.add(c2Panel);
		box.add(ratePanel);
		box.add(Box.createVerticalStrut(5));
		box.add(calculate);
		box.add(Box.createVerticalStrut(10));
		box.add(result);
		
		panel = new JPanel();
		panel.add(box);
		
		toSave = 0;
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				double results;
				if(!comp1.getText().equals("") && !comp2.getText().equals("") && formula1.isSelected() && formula2.isSelected())
				{
					Compound c1 = Compound.parseCompound(comp1.getText());
					Compound c2 = Compound.parseCompound(comp2.getText());
					results = Math.sqrt(c1.getMolarMass() / c2.getMolarMass());
					result.setText("Ratio of rates = " + results);
				}
				else if(!comp1.getText().equals("") && !comp2.getText().equals("") && formula1.isSelected() && !formula2.isSelected())
				{
					Compound c1 = Compound.parseCompound(comp1.getText());
					double mass2 = Double.parseDouble(comp2.getText());
					results = Math.sqrt(c1.getMolarMass() / mass2);
					result.setText("Ratio of rates = " + results);
				}
				else if(!comp1.getText().equals("") && !comp2.getText().equals("") && !formula1.isSelected() && formula2.isSelected())
				{
					double mass1 = Double.parseDouble(comp1.getText());
					Compound c2 = Compound.parseCompound(comp2.getText());
					results = Math.sqrt(mass1 / c2.getMolarMass());
					result.setText("Ratio of rates = " + results);
				}
				else if(!comp1.getText().equals("") && !comp2.getText().equals("") && !formula1.isSelected() && !formula2.isSelected())
				{
					double mass1 = Double.parseDouble(comp1.getText());
					double mass2 = Double.parseDouble(comp2.getText());
					results = Math.sqrt(mass1 / mass2);
					result.setText("Ratio of rates = " + results);
				}
				else if(!comp1.getText().equals("") && !ratio.getText().equals("") && formula1.isSelected())
				{
					double mass1 = Compound.parseCompound(comp1.getText()).getMolarMass();
					double rate = Double.parseDouble(ratio.getText());
					results = mass1 / (rate * rate);
					result.setText("Mass of compound 2 = " + results);
				}
				else if(!comp1.getText().equals("") && !ratio.getText().equals("") && !formula1.isSelected())
				{
					double mass1 = Double.parseDouble(comp1.getText());
					double rate = Double.parseDouble(ratio.getText());
					results = mass1 / (rate * rate);
					result.setText("Mass of compound 2 = " + results);
				}
				else if(!comp2.getText().equals("") && !ratio.getText().equals("") && formula2.isSelected())
				{
					double mass2 = Compound.parseCompound(comp2.getText()).getMolarMass();
					double rate = Double.parseDouble(ratio.getText());
					results = (rate * rate) / mass2;
					result.setText("Mass of compound 1 = " + results);
				}
				else if(!comp2.getText().equals("") && !ratio.getText().equals("") && !formula2.isSelected())
				{
					double mass2 = Double.parseDouble(comp2.getText());
					double rate = Double.parseDouble(ratio.getText());
					results = (rate * rate) / mass2;
					result.setText("Mass of compound 1 = " + results);
				}
				else
				{
					result.setText("You did not enter enough information to make any calulations.");
					return;
				}
				toSave = results;
			}
			catch(Throwable e)
			{
				if(!(e instanceof InvalidInputException))
				{
					e = new InvalidInputException(-1);
				}
				result.setText(((InvalidInputException)e).getMessage());
			}
		}
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		return toSave;
	}
	
	public void useSavedNumber(double num)
	{
		String[] options = {"Compound 1", "Compound 2", "Ratio"};
		Object selected = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, options, "Compound 1");
		if(selected instanceof String)
		{
			if(selected.equals("Compound 1"))
			{
				comp1.setText("" + num);
				mass1.setSelected(true);
			}
			else if(selected.equals("Compound 2"))
			{
				comp2.setText("" + num);
				mass2.setSelected(true);
			}
			else ratio.setText("" + num);
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}
