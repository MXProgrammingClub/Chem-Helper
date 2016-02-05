/*
 * For if aspects of a container (P, V, n, T) change and one resultant value is unknown.
 * number() returns true- saves latest calculated value, uses saved for any field.
 * 
 * Author: Julia McClellan and Luke Giacalone
 * Version: 1/31/2016
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

import HelperClasses.EnterField;
import HelperClasses.Units;

public class ContainerChanges extends Function
{
	private static final int UNKNOWN_VALUE = -501, ERROR_VALUE = -502;
	private static final String[] VALUES = {"Pressure", "Volume", "Moles", "Temperature"};
	private static final String[][] UNITS = {{"atm", "torr", "kPa"}, 
			{"pL", "nL", "\u00B5L", "mL", "cL", "dL", "L", "daL", "hL", "kL", "ML", "TL", "GL"}, 
			{"mol"}, {"K", "\u2103", "\u2109"}};
	
	private JPanel panel;
	private ArrayList<DoubleEnterField> information;
	private JButton calculate;
	private JLabel result;
	private double toSave;
	private CheckBox[] boxes;
	
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
			DoubleEnterField temp = new DoubleEnterField(VALUES[i], i < 2, UNITS[i]);
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
		
		panel = new JPanel();
		panel.add(subpanel);
		
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
	
	//a class that uses two EnterFields to make: beforeValue -> afterValue
	private class DoubleEnterField extends JPanel {
		
		private String name;
		private boolean isLeft;
		private EnterField before;
		private EnterField after;
		
		public DoubleEnterField(String name, boolean isLeft, String[] units) {
			this.name = name;
			this.isLeft = isLeft;
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			c.gridx = 0;
			c.gridy = 0;
			before = new EnterField(name, units);
			if(name.equals(VALUES[1])) //setting volume to L
				before.setUnit(6);
			this.add(before, c);
			
			c.gridx = 1;
			this.add(new JLabel("\u2192"), c);
			
			c.gridx = 2;
			after = new EnterField(null, units);
			if(name.equals(VALUES[1])) //setting volume to L
				after.setUnit(6);
			after.remove(0); //removing the empty space where label is
			this.add(after, c);
		}
		
		public double getBeforeValue()
		{
			try
			{
				double value = Double.parseDouble(before.getText());
				if(name.equals(VALUES[0])) {
					if(before.getUnit() == 1) value = Units.torrToatm(value);
					else if(before.getUnit() == 2) value = Units.kPaToatm(value);
				}
				else if(name.equals(VALUES[1])) { //volume
					value = Units.volumeToLiters(value, before.getUnit());
				}
				else if(name.equals(VALUES[3])) {
					if(before.getUnit() == 1) value = Units.celsiusToKelvin(value);
					else if(before.getUnit() == 2) value = Units.fahrenheitToKelvin(value);
				}
				if(!isLeft) value = 1 / value;
				return value;
			}
			catch(Throwable e)
			{
				return ERROR_VALUE;
			}
		}
		
		public void setBeforeValue(double value)
		{
			before.setText("" + value);
		}
		
		public void setAfterValue(double value)
		{
			after.setText("" + value);
		}
		
		public double getAfterValue() 
		{
			try 
			{
				double value = Double.parseDouble(after.getText());
				if(name.equals(VALUES[0])) 
				{
					if(after.getUnit() == 1) value = Units.torrToatm(value);
					else if(after.getUnit() == 2) value = Units.kPaToatm(value);
				}
				else if(name.equals(VALUES[1])) { //volume
					value = Units.volumeToLiters(value, after.getUnit());
				}
				else if(name.equals(VALUES[3])) 
				{
					if(after.getUnit() == 1) value = Units.celsiusToKelvin(value);
					else if(after.getUnit() == 2) value = Units.fahrenheitToKelvin(value);
				}
				if(!isLeft) value = 1 / value;
				return value;
			}
			catch(Throwable e) 
			{
				if(e.getMessage().equals("empty String")) return UNKNOWN_VALUE;
 				return ERROR_VALUE;
			}
		}
		
		public String getDesiredUnit()
		{
			return (String) after.getUnitName();
		}
		
		public String getName()
		{
			return name;
		}
		
		public boolean isLeft()
		{
			return isLeft;
		}
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			double before = 1, after = 1;
			DoubleEnterField unknown = null;
			
			for(DoubleEnterField field: information)
			{
				if(field.isVisible())
				{
					double thisBefore = field.getBeforeValue();
					if(thisBefore != ERROR_VALUE) before *= thisBefore;
					else
					{
						result.setText("There was a problem with your input.");
						return;
					}
					double thisAfter = field.getAfterValue();
					if(thisAfter == ERROR_VALUE)
					{
						result.setText("There was a problem with your input.");
						return;
					}
					else if(thisAfter == UNKNOWN_VALUE)
					{
						if(unknown == null) unknown = field;
						else
						{
							result.setText("Only one value can be left blank.");
							return;
						}
					}
					else after *= thisAfter;
				}
			}
			if(unknown != null)
			{
				double resultant;
				if(unknown.isLeft()) resultant = before / after;
				else resultant = after / before;
				if(unknown.getName().equals(VALUES[0]))
				{
					if(unknown.getDesiredUnit().equals(UNITS[0][1])) resultant = Units.atmTotorr(resultant);
					else if(unknown.getDesiredUnit().equals(UNITS[0][2])) resultant = Units.atmTokPa(resultant);
				}
				else if(unknown.getName().equals(VALUES[3]))
				{
					if(unknown.getDesiredUnit().equals(UNITS[3][1])) resultant = Units.kelvinToCelsius(resultant);
					else if(unknown.getDesiredUnit().equals(UNITS[3][2])) resultant = Units.kelvinToFahrenheit(resultant);
				}
				toSave = resultant;
				result.setText(unknown.getName() + " = " + resultant + " " + unknown.getDesiredUnit());
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
	
	public JPanel getPanel()
	{
		return panel;
	}
}