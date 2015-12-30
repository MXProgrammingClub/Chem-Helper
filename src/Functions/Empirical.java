package Functions;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Elements.Element;

public class Empirical extends Function
{
	private static final double ERROR_VALUE = 101, REST = -1, TOLERANCE = .05;
	private static final String ENTER_REST = "R";
	private static final int MAX_TEST = 20;
	
	private JPanel panel, amounts;
	private JTextField moles, mass;
	private ArrayList<TableRow> rows;
	private Box rowBox;
	private JButton calculate, addRow;
	private JLabel error, empirical, mass1, molecular, mass2;
	
	private Double[] toSave;
	
	public Empirical()
	{
		super("Empirical Formula");
		
		JLabel element = new JLabel(" Element  ");
		element.setBorder(BorderFactory.createLineBorder(Color.black));
		JLabel percent = new JLabel(" Percent  ");
		percent.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel topRow = new JPanel();
		topRow.add(element);
		topRow.add(percent);
		
		rowBox = Box.createVerticalBox();
		rowBox.add(topRow);
		rows = new ArrayList<TableRow>();
		for(int num = 0; num < 2; num++)
		{
			TableRow row = new TableRow();
			rowBox.add(row);
			rows.add(row);
		}
		
		addRow = new JButton("Add row");
		addRow.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						panel.setVisible(false);
						TableRow row = new TableRow();
						rowBox.add(row);
						rows.add(row);
						panel.setVisible(true);
					}
				});
		addRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		calculate = new JButton("Calculate");
		calculate.setAlignmentX(Component.LEFT_ALIGNMENT);
		calculate.addActionListener(new Calculate());
		
		error = new JLabel();
		empirical = new JLabel();
		mass1 = new JLabel();
		molecular = new JLabel();
		mass2 = new JLabel();
		
		moles = new JTextField(5);
		mass = new JTextField(5);
		amounts = new JPanel();
		amounts.add(new JLabel("Moles: "));
		amounts.add(moles);
		amounts.add(new JLabel("  Mass: "));
		amounts.add(mass);
		
		Box box = Box.createVerticalBox();
		box.add(new JLabel("If one percent is given as the remainder, enter \"R\""));
		box.add(rowBox);
		box.add(addRow);
		box.add(Box.createVerticalStrut(10));
		box.add(new JLabel("If known, enter the "));
		box.add(amounts);
		box.add(Box.createVerticalStrut(10));
		box.add(calculate);
		box.add(error);
		box.add(empirical);
		box.add(mass1);
		box.add(molecular);
		box.add(mass2);
		
		panel = new JPanel();
		panel.add(box);
		
		toSave = new Double[2];
	}
	
	private class TableRow extends JPanel
	{
		private JTextField element, percent;
		
		public TableRow()
		{
			element = new JTextField(5);
			element.setBorder(BorderFactory.createLineBorder(Color.black));
			percent = new JTextField(5);
			percent.setBorder(BorderFactory.createLineBorder(Color.black));
			add(element);
			add(percent);
		}
		
		public Element getElement()
		{
			return PeriodicTable.find(element.getText());
		}
		
		public double getPercent()
		{
			String text = percent.getText();
			if(text.equals(ENTER_REST)) return REST;
			try
			{
				double percent = Double.parseDouble(text);
				if(percent >= 100 || percent <= 0) return ERROR_VALUE;
				else return percent;
			}
			catch(Throwable e)
			{
				return ERROR_VALUE;
			}
		}
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			double[] values = new double[rows.size()];
			Element[] elements = new Element[rows.size()];
			int rest = -1;
			double sum = 0;
			for(int index = 0; index < rows.size(); index++)
			{
				values[index] = rows.get(index).getPercent();
				elements[index] = rows.get(index).getElement();
				if(values[index] == ERROR_VALUE)
				{
					error.setText("Value entered outside range of 0 to 100.");
					return;
				}
				if(elements[index] == null)
				{
					error.setText("Invalid element entered.");
					return;
				}
				if(values[index] == REST)
				{
					if(rest != -1)
					{
						error.setText("Only one value can be replaced by \"R\".");
						return;
					}
					rest = index;
				}
				else
				{
					sum += values[index];
				}
			}
			if(rest != -1)
			{
				if(sum >= 100)
				{
					error.setText("Your percent values add up to more than 100.");
					return;
				}
				values[rest] = 100 - sum;
			}
			else if(Math.abs(100 - sum) > TOLERANCE)
			{
				error.setText("Your percent values do not add up to 100.");
				return;
			}
			double min = Double.MAX_VALUE;
			for(int index = 0; index < values.length; index++)
			{
				values[index] /= elements[index].getMolarMass();
				if(min > values[index]) min = values[index];
			}
			int times = 1;
			for(int index = 0; index < values.length; index++)
			{
				values[index] /= min;
				if(!closeToInt(values[index]))
				{
					for(int num = 2; num < MAX_TEST; num++)
					{
						double value = num * values[index];
						if(closeToInt(value))
						{
							if(times % num != 0) times *= num;
							break;
						}
					}
				}
			}
			int[] coefficients = new int[values.length];
			String formula = "<html>Empirical formula: ";
			double eMass = 0;
			for(int index = 0; index < coefficients.length; index++)
			{
				coefficients[index] = round(values[index] * times);
				eMass += coefficients[index] * elements[index].getMolarMass();
				formula += elements[index].getSymbol();
				if(coefficients[index] != 1) formula += "<sub>" + coefficients[index] + "</sub>";
			}
			formula += "<html";
			empirical.setText(formula);
			mass1.setText("Molar Mass = " + eMass + " g/mol");
			toSave[0] = eMass;
			if(!mass.getText().trim().equals(""))
			{
				try
				{
					double mMass = Double.parseDouble(mass.getText()) / Double.parseDouble(moles.getText());
					int factor = round(mMass / eMass);
					formula = "<html>Molecular formula: ";
					for(int index = 0; index < coefficients.length; index++)
					{
						coefficients[index] *= factor;
						formula += elements[index].getSymbol();
						if(coefficients[index] != 1) formula += "<sub>" + coefficients[index] + "</sub>";
					}
					formula += "</html>";
					molecular.setText(formula);
					mass2.setText("Molar Mass = " + mMass + " g/mol");
					toSave[1] = mMass;
				}
				catch(Throwable e)
				{
					molecular.setText("Insuffiecient information to calculate molecular formula.");
				}
			}
		}
		
		private boolean closeToInt(double num)
		{
			int down = (int)num, up = down + 1;
			return num - down < TOLERANCE || up - num < TOLERANCE;
		}
		
		private int round(double num)
		{
			int round = (int)num;
			if(round + .5 > num) return round;
			else return round + 1;
		}
	}
	
	public boolean number()
	{
		return true;
	}
	
	public double saveNumber()
	{
		if(toSave[0] != 0)
		{
			if(toSave[1] != 0)
			{
				Object selected = JOptionPane.showInputDialog(panel, "Choose which number to save", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
						null, toSave, toSave[0]);
				if(selected instanceof Double)
				{
					if(selected.equals(toSave[0])) return toSave[0];
					return toSave[1];
				}
			}
			return toSave[0];
		}
		return 0;
	}
	
	public void useSavedNumber(double num)
	{
		String[] options = {"Moles", "Mass"};
		Object selected = JOptionPane.showInputDialog(panel, "Choose where to use the number", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
				null, options, "Moles");
		if(selected instanceof String)
		{
			if(selected.equals("Moles")) moles.setText("" + num);
			else mass.setText("" + num);
		}
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
}