/*
 * For if aspects of a container (P, V, n, T) change and one resultant value is unknown.
 * number() returns true- saves latest calculated value, uses saved for any field.
 * 
 * Author: Julia McClellan and Luke Giacalone
 * Version: 2/13/2016
 */

package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import HelperClasses.DoubleEnterField;
import HelperClasses.Units;

public class ContainerChanges extends Function
{
	private static final String[] VALUES = {"Pressure", "Volume", "Moles", "Temperature"};
	
	private JPanel panel;
	private ArrayList<DoubleEnterField> information;
	private JButton calculate;
	private JLabel result;
	private double toSave;
	private CheckBox[] boxes;
	private Box steps;
	
	public ContainerChanges()
	{
		super("Change of Container");
		
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		
		//box with Checkboxes
		c.gridy = 0;
		Box options = Box.createHorizontalBox();
		boxes = new CheckBox[4];
		for(int i = 0; i < 4; i++)
		{
			CheckBox box = new CheckBox(i);
			options.add(box);
			boxes[i] = box;
		}
		
		subpanel.add(options, c);
		
		//adds things to information and adds to subpanel and makes them not visible
		information = new ArrayList<DoubleEnterField>();
		for(int i = 0; i < 4; i++) {
			DoubleEnterField temp = new DoubleEnterField(VALUES[i], i < 2, VALUES[i]);
			information.add(temp);
			temp.setVisible(false);
			c.gridy++;
			subpanel.add(temp, c);
		}
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		c.gridy = 5;
		c.anchor = GridBagConstraints.CENTER;
		subpanel.add(calculate, c);
		
		result = new JLabel("");
		c.gridy = 6;
		c.anchor = GridBagConstraints.WEST;
		subpanel.add(result, c);
		
		steps = Box.createVerticalBox();
		
		panel = new JPanel();
		panel.add(subpanel);
		panel.add(steps);
		
		toSave = 0;
	}
	
	private class CheckBox extends JPanel {
		
		private JLabel label;
		private JCheckBox check;
		
		public CheckBox(int index) {
			Box box = Box.createHorizontalBox();
			
			check = new JCheckBox();
			check.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					steps.setVisible(false);
					result.setText("");
					if(VALUES[0].equals(label.getText()))
						information.get(0).setVisible(!information.get(0).isVisible());
					else if(VALUES[1].equals(label.getText()))
						information.get(1).setVisible(!information.get(1).isVisible());
					else if(VALUES[2].equals(label.getText()))
						information.get(2).setVisible(!information.get(2).isVisible());
					if(VALUES[3].equals(label.getText()))
						information.get(3).setVisible(!information.get(3).isVisible());
				}
			});
			
			label = new JLabel(VALUES[index]);
			
			box.add(check);
			box.add(label);
			this.add(box);
		}
		
		public void setSelected(boolean selected)
		{
			check.setSelected(true);
		}
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			double before = 1, after = 1;
			DoubleEnterField unknown = null;
			int sigFigs = Integer.MAX_VALUE;
			steps.removeAll();
			steps.setVisible(false);
			String[] step = {"(", "(", "(", "("};
			for(DoubleEnterField field: information)
			{
				if(field.isVisible())
				{
					sigFigs = Math.min(sigFigs, field.getSigFigs());
					double thisBefore = field.getBeforeValue();
					if(thisBefore == Units.ERROR_VALUE)
					{
						result.setText("There was a problem with your input.");
						return;
					}
					before *= thisBefore;
					double val = thisBefore;
					if(!field.isLeft())
					{
						val = 1 / val;
						step[1] += val + " * ";
					}
					else step[0] += val + " * ";
					steps.add(new JLabel(field.getName() + "-before = " + val + " " + field.getStandardUnit()));
					
					double thisAfter = val = field.getAfterValue();
					if(thisAfter == Units.ERROR_VALUE)
					{
						result.setText("There was a problem with your input.");
						return;
					}
					else if(thisAfter == Units.UNKNOWN_VALUE)
					{
						if(unknown == null)
						{
							unknown = field;
							steps.add(new JLabel(field.getName() + "-after = ? " + field.getStandardUnit()));
							if(field.isLeft()) step[2] += "x * ";
							else step[3] += "x * ";
						}
						else
						{
							result.setText("Only one value can be left blank.");
							return;
						}
					}
					else 
					{
						after *= thisAfter;
						if(!field.isLeft())
						{
							val = 1 / val;
							step[3] += val + " * ";
						}
						else step[2] += val + " * ";
						steps.add(new JLabel(field.getName() + "-after = " + val + " " + field.getStandardUnit()));
					}
				}
			}
			steps.add(Box.createVerticalStrut(10));
			
			if(unknown != null)
			{
				for(int index = 0; index < 4; index ++)
				{
					if(step[index].equals("("))
					{
						if(index % 2 == 0) step[index] = "1";
						else step[index] = "";
					}
					else
					{
						step[index] = step[index].substring(0, step[index].length() - 3) + ")";
						if(index % 2 != 0) step[index] = " / " + step[index];
					}
				}
				steps.add(new JLabel(step[0] + step[1] + " = " + step[2] + step[3]));
				
				double resultant;
				if(unknown.isLeft()) resultant = before / after;
				else resultant = after / before;
				steps.add(new JLabel("x = " + resultant + " " + unknown.getStandardUnit()));
				String unit = unknown.getDesiredUnit();
				toSave = unknown.getBlankAmount(resultant);
				if(toSave != resultant) steps.add(new JLabel(resultant + " " + unknown.getStandardUnit() + " = " + toSave + " " + unknown.getDesiredUnit()));
				result.setText(unknown.getName() + " = " + Function.withSigFigs(resultant, sigFigs) + " " + unit);
				steps.setVisible(true);
			}
			else result.setText("Leave a value blank.");
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
		if(information.size() != 0)
		{
			ArrayList<String> options = new ArrayList<String>();
			for(DoubleEnterField field: information)
			{
				options.add(field.getName() + " - before");
				options.add(field.getName() + " - after");
			}
			Object selected = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
					null, options.toArray(), options.get(0));
			if(selected instanceof String)
			{
				int index = options.indexOf(selected);
				DoubleEnterField field = information.get(index / 2);
				if(index % 2 == 0) field.setBeforeValue(num);
				else field.setAfterValue(num);
				if(!field.isVisible())
				{
					boxes[index].setSelected(true);
					field.setVisible(true);
				}
			}
		}
	}
	
	public String getHelp()
	{
		return "<html>Choose from the checkboxes at the top which<br>"
				+ "aspects of the container changed. Enter both<br>"
				+ "the before and after values for all but one.<br>"
				+ "Leave only the after value blank for this<br>"
				+ "component. Press the calculate button to find<br>"
				+ "the value of the missing changed aspect.</html>";
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}