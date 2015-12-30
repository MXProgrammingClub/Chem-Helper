package Functions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ContainerChanges extends Function
{
	private static final int UNKNOWN_VALUE = -501, ERROR_VALUE = -502;
	private static final String[] values = {"Pressure", "Volume", "Moles", "Temperature"};
	
	private JPanel panel, options;
	private Box valueBox;
	private ArrayList<EnterField> information;
	private JButton calculate, reset;
	private JLabel result;

	public ContainerChanges()
	{
		super("Change of Container");
		
		options = new JPanel();
		for(int index = 0; index < values.length; index++) new ClickLabel(index);
		
		valueBox = Box.createVerticalBox();
		information = new ArrayList<EnterField>();
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		calculate.setVisible(false);
		
		result = new JLabel();
		reset = new JButton("Reset");
		reset.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						panel.setVisible(false);
						panel.removeAll();
						ContainerChanges c = new ContainerChanges();
						panel.add(c.getPanel());
						panel.setVisible(true);
					}
				});
		
		Box box = Box.createVerticalBox();
		box.add(options);
		box.add(valueBox);
		box.add(calculate);
		box.add(result);
		box.add(Box.createVerticalStrut(10));
		box.add(reset);
		
		panel = new JPanel();
		panel.add(box);
	}
	
	private class ClickLabel
	{
		private EnterField field;
		private JLabel label;
		
		public ClickLabel(int index)
		{
			label = new JLabel(values[index]);
			label.setBorder(BorderFactory.createLineBorder(Color.black));
			label.addMouseListener(new ClickListener());
			options.add(label);
			field = new EnterField(values[index], index < 2, IdealGas.UNITS[index]);
		}
		
		private class ClickListener implements MouseListener
		{
			public void mouseClicked(MouseEvent arg0) 
			{
				panel.setVisible(false);
				options.remove(label);
				information.add(field);
				valueBox.add(field);
				if(information.size() == 2) calculate.setVisible(true);
				panel.setVisible(true);
			}
			public void mouseEntered(MouseEvent arg0) {} public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {} public void mouseReleased(MouseEvent arg0) {}
		}
	}
	
	private class EnterField extends JPanel
	{
		private JTextField before, after;
		private JComboBox<String> beforeUnit, afterUnit;
		private boolean isLeft;
		private String name;
		
		public EnterField(String name, boolean isLeft, String[] units)
		{
			this.name = name;
			this.isLeft = isLeft;
			before = new JTextField(5);
			after = new JTextField(5);
			beforeUnit = new JComboBox<String>(units);
			afterUnit = new JComboBox<String>(units);
			
			Box box = Box.createHorizontalBox();
			box.add(new JLabel(name));
			box.add(Box.createHorizontalStrut(5));
			box.add(before);
			box.add(beforeUnit);
			box.add(new JLabel("\u2192"));
			box.add(after);
			box.add(afterUnit);
			
			add(box);
		}
		
		public double getBeforeValue()
		{
			try
			{
				double value = Double.parseDouble(before.getText());
				if(name.equals(values[0]))
				{
					if(beforeUnit.getSelectedIndex() == 1) value = IdealGas.torrToatm(value);
					else if(beforeUnit.getSelectedIndex() == 2) value = IdealGas.kPaToatm(value);
				}
				else if(name.equals(values[3]))
				{
					if(beforeUnit.getSelectedIndex() == 1) value = IdealGas.celsiusToKelvin(value);
					else if(beforeUnit.getSelectedIndex() == 2) value = IdealGas.fahrenheitToKelvin(value);
				}
				if(!isLeft) value = 1 / value;
				return value;
			}
			catch(Throwable e)
			{
				return ERROR_VALUE;
			}
		}
		
		public double getAfterValue()
		{
			try
			{
				double value = Double.parseDouble(after.getText());
				if(name.equals(values[0]))
				{
					if(afterUnit.getSelectedIndex() == 1) value = IdealGas.torrToatm(value);
					else if(afterUnit.getSelectedIndex() == 2) value = IdealGas.kPaToatm(value);
				}
				else if(name.equals(values[3]))
				{
					if(afterUnit.getSelectedIndex() == 1) value = IdealGas.celsiusToKelvin(value);
					else if(afterUnit.getSelectedIndex() == 2) value = IdealGas.fahrenheitToKelvin(value);
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
			return (String)afterUnit.getSelectedItem();
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
			EnterField unknown = null;
			
			for(EnterField field: information)
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
			if(unknown != null)
			{
				double resultant;
				if(unknown.isLeft()) resultant = before / after;
				else resultant = after / before;
				if(unknown.getName().equals(values[0]))
				{
					if(unknown.getDesiredUnit().equals(IdealGas.UNITS[0][1])) resultant = IdealGas.atmTotorr(resultant);
					else if(unknown.getDesiredUnit().equals(IdealGas.UNITS[0][2])) resultant = IdealGas.atmTokPa(resultant);
				}
				else if(unknown.getName().equals(values[3]))
				{
					if(unknown.getDesiredUnit().equals(IdealGas.UNITS[3][1])) resultant = IdealGas.kelvinToCelsius(resultant);
					else if(unknown.getDesiredUnit().equals(IdealGas.UNITS[3][2])) resultant = IdealGas.kelvinToFahrenheit(resultant);
				}
				result.setText(unknown.getName() + " = " + resultant + " " + unknown.getDesiredUnit());
			}
			else result.setText("Leave a value blank.");
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}