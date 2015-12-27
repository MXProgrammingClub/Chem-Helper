package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class IdealGas extends Function 
{
	public static final double R = .0821, STANDARD_PRESSURE = 1, STANDARD_TEMPERATURE = 273.15;

	private static final int UNKNOWN_VALUE = -500, ERROR_VALUE = -501; // Values which none of the entered values could be.
	private static final String[][] UNITS = {{"atm", "torr", "kPa"}, {"L"}, {"mol"}, {"K", "\u2103", "\u2109"}};
	private static final String[] VALUES = {"      Pressure", "          Volume", "             Moles", "Temperature"};
	
	private JPanel panel;
	private JButton calculate;
	private JCheckBox stp;
	private EnterField[] values;
	private JLabel result;
	
	public IdealGas()
	{
		super("Ideal Gas Law");
		
		Box valueBox = Box.createVerticalBox();
		values = new EnterField[VALUES.length];
		for(int index = 0; index < values.length; index++)
		{
			EnterField field = new EnterField(VALUES[index], UNITS[index]);
			values[index] = field;
			valueBox.add(field);
		}
		
		stp = new JCheckBox("STP");
		stp.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						if(stp.isSelected())
						{
							values[0].setAmount(STANDARD_PRESSURE);
							values[0].setUnit(0);
							values[3].setAmount(STANDARD_TEMPERATURE);
							values[3].setUnit(0);
						}
					}
				});
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		
		result = new JLabel();
		
		Box box = Box.createVerticalBox();
		box.add(new JLabel("Enter known information and select desired unit for the unknown quantity."));
		box.add(valueBox);
		box.add(stp);
		box.add(calculate);
		box.add(result);
		
		panel = new JPanel();
		panel.add(box);
	}
	
	private class EnterField extends JPanel
	{
		private JTextField amount;
		private JComboBox<String> unit;
		
		public EnterField(String name, String[] units)
		{
			amount = new JTextField(5);
			unit = new JComboBox<String>(units);
			unit.setEditable(true);
			unit.setSelectedIndex(0);
			
			add(new JLabel(name));
			add(amount);
			add(unit);
		}
		
		public void setAmount(double newAmount)
		{
			amount.setText("" + newAmount);
		}
		
		public double getAmount()
		{
			try
			{
				double value = Double.parseDouble(amount.getText());
				return value;
			}
			catch(Throwable e)
			{
				if(e.getMessage().equals("empty String")) return UNKNOWN_VALUE;
				return ERROR_VALUE;
			}
		}
		
		public void setUnit(int index)
		{
			unit.setSelectedIndex(index);
		}
		
		public int getUnit()
		{
			return unit.getSelectedIndex();
		}
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			double[] quantities = new double[4];
			int blank = -1;
			for(int index = 0; index < values.length; index++)
			{
				quantities[index] = values[index].getAmount();
				if(quantities[index] == ERROR_VALUE)
				{
					result.setText("An entered value was not a number.");
					return;
				}
				if(quantities[index] == UNKNOWN_VALUE)
				{
					if(blank == -1) blank = index;
					else
					{
						result.setText("Only one value can be left blank.");
						return;
					}
				}
				else if(values[index].getUnit() != 0)
				{
					if(index == 0)
					{
						if(values[index].getUnit() == 1) quantities[index] *= 0.00131579;
						else quantities[index] *= 0.00986923;
					}
					else if(index == 3)
					{
						if(values[index].getUnit() == 1) quantities[index] += 273.15;
						else quantities[index] = (quantities[index] + 459.67) * 5 / 9;
					}
				}
			}
			double unknown;
			int unitNum = values[blank].getUnit();
			String unit = UNITS[blank][unitNum];
			if(blank == 0)
			{
				unknown = R * quantities[2] * quantities[3] / quantities[1];
				if(unitNum == 1) unknown /= 0.00131579;
				else if(unitNum == 2) unknown /= 0.00986923;
				
			}
			else if(blank == 1) unknown = R * quantities[2] * quantities[3] / quantities[0];
			else if(blank == 2) unknown = quantities[0] * quantities[1] / (R * quantities[3]);
			else
			{
				unknown = quantities[0] * quantities[1] / (R * quantities[2]);
				if(unitNum == 1) unknown -= 273.15;
				else if(unitNum == 2) unknown = (unknown - 459.67) * 9 / 5;
			}
			result.setText(VALUES[blank].trim() + " = " + unknown + " " + unit);
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}