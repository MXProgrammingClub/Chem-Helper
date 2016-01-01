package Functions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ChemHelper.Compound;
import Equation.Equation;

public class RateLaw extends Function 
{
	private JPanel panel, equationPanel, tablePanel, resultPanel;
	private EquationReader reader;
	private Equation equation;
	private JButton useEquation, calculate, reset, add;
	private ArrayList<TableRow> rows;
	private Box box1, table, box2, box3, box4;
	private JLabel errorMessage, displayEquation, law, kValue;
	private ArrayList<Compound> compounds;
	
	public RateLaw()
	{
		super("Rate Law");
		panel = new JPanel();
		setPanel();
	}
	
	private void setPanel()
	{
		reader = new EquationReader();
		useEquation = new JButton("Use equation");
		useEquation.addActionListener(new UseEquation());
		errorMessage = new JLabel();
		box1 = Box.createVerticalBox();
		box1.add(reader.getPanel());
		box1.add(errorMessage);
		box1.add(useEquation);
		equationPanel = new JPanel();
		equationPanel.add(box1);
		
		displayEquation = new JLabel();
		add = new JButton("Add row");
		add.addActionListener(new AddRow());
		calculate = new JButton("Calculate");
		calculate.addActionListener(new Calculate());
		table = Box.createVerticalBox();
		box4 = Box.createVerticalBox();
		box4.add(displayEquation);
		box4.add(Box.createVerticalStrut(10));
		box4.add(table);
		box4.add(add);
		box4.add(Box.createVerticalStrut(10));
		box4.add(calculate);
		tablePanel = new JPanel();
		tablePanel.add(box4);
		tablePanel.setVisible(false);
		
		law = new JLabel();
		kValue = new JLabel();
		reset = new JButton("Reset");
		reset.addActionListener(new Reset());
		box2 = Box.createVerticalBox();
		box2.add(law);
		box2.add(kValue);
		box2.add(reset);
		resultPanel = new JPanel();
		resultPanel.add(box2);
		resultPanel.setVisible(false);
		
		box3 = Box.createVerticalBox();
		box3.add(tablePanel);
		box3.add(resultPanel);
		
		panel.add(equationPanel);
		panel.add(box3);
	}
	
	private class UseEquation implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			equation = reader.getEquation();
			if(equation == null)
			{
				errorMessage.setText("You have not entered a valid equation");
			}
			else
			{
				panel.remove(equationPanel);
				panel.repaint();
				compounds = equation.getLeft();
				displayEquation.setText("<html>" + equation + "</html");
				generateTable();
				tablePanel.setVisible(true);
			}
		}
	}
	
	private class TableRow extends JPanel
	{
		private JTextField[] concentrations;
		private JTextField rate;
		private Box box;
		private JLabel numLabel;
		
		public TableRow(int num)
		{
			concentrations = new JTextField[compounds.size()];
			numLabel = new JLabel("" + num);
			numLabel.setBorder(BorderFactory.createLineBorder(Color.black));
			numLabel.setPreferredSize(new Dimension(30, 20));
			box = Box.createHorizontalBox();
			box.add(numLabel);
			for(int index = compounds.size() - 1; index >= 0; index--)
			{
				JTextField field = new JTextField(5);
				field.setBorder(BorderFactory.createLineBorder(Color.black));
				box.add(field);
				concentrations[index] = field;
			}
			rate = new JTextField(5);
			rate.setBorder(BorderFactory.createLineBorder(Color.black));
			box.add(rate);
			add(box);
		}
		
		public String getConcentration(int index)
		{
			return concentrations[index].getText();
		}
		
		public String getRate()
		{
			return rate.getText();
		}
	}
	
	private void generateTable()
	{
		rows = new ArrayList<TableRow>();
		int cols = compounds.size(), rows = cols + 1;
		Box header = Box.createHorizontalBox();
		JLabel trial = new JLabel("Trial");
		trial.setPreferredSize(new Dimension(30, 30));
		trial.setMinimumSize(new Dimension(30, 30));
		trial.setMaximumSize(new Dimension(30, 30));
		trial.setBorder(BorderFactory.createLineBorder(Color.black));
		header.add(trial);
		JTextField testSize = new JTextField(5);
		for(Compound c: compounds)
		{
			String compound = c.toString();
			if(c.getNum() != 1) compound = compound.substring(1);
			JLabel cLabel = new JLabel("<html>" + compound + "</html>", SwingConstants.CENTER);
			cLabel.setPreferredSize(new Dimension(testSize.getPreferredSize().width, 30));
			cLabel.setMaximumSize(new Dimension(testSize.getPreferredSize().width, 30));
			cLabel.setMinimumSize(new Dimension(testSize.getPreferredSize().width, 30));
			cLabel.setBorder(BorderFactory.createLineBorder(Color.black));
			header.add(cLabel);
		}
		JLabel rate = new JLabel("Rate", SwingConstants.CENTER);
		rate.setPreferredSize(new Dimension(testSize.getPreferredSize().width, 30));
		rate.setMinimumSize(new Dimension(testSize.getPreferredSize().width, 30));
		rate.setMaximumSize(new Dimension(testSize.getPreferredSize().width, 30));
		rate.setBorder(BorderFactory.createLineBorder(Color.black));
		header.add(rate);
		table.add(header);
		for(int row = 0; row < rows; row++)
		{
			TableRow newRow = new TableRow(row);
			this.rows.add(newRow);
			table.add(newRow);
		}
	}
	
	private class AddRow implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			tablePanel.setVisible(false);
			TableRow newRow = new TableRow(rows.size());
			rows.add(newRow);
			table.add(newRow);
			tablePanel.setVisible(true);
		}
	}
	
	private class Calculate implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			box4.remove(errorMessage);
			int[] orders = new int[compounds.size()];
			int sigFigs = Integer.MAX_VALUE;
			for(int order = 0; order < orders.length; order++)
			{
				int row1 = 0, row2 = 0;
				boolean found = false;
				for(; row1 < rows.size(); row1++)
				{
					for(; row2 < rows.size(); row2++)
					{
						if(row2 != row1)
						{
							double c1, c2;
							try
							{
								c1 = Double.parseDouble(rows.get(row1).getConcentration(order));
								c2 = Double.parseDouble(rows.get(row2).getConcentration(order));
							}
							catch(Throwable e)
							{
								errorMessage.setText("There was a problem with your input.");
								box4.add(errorMessage);
								return;
							}
							if(Function.sigFigs(rows.get(row1).getConcentration(order)) < sigFigs)
							{
								sigFigs = Function.sigFigs(rows.get(row1).getConcentration(order));
							}
							if(Function.sigFigs(rows.get(row2).getConcentration(order)) < sigFigs)
							{
								sigFigs = Function.sigFigs(rows.get(row1).getConcentration(order));
							}
							if(c1 != c2)
							{
								boolean equal = true;
								{
									for(int check = 0; equal && check < orders.length; check++)
									{
										try
										{
											c1 = Double.parseDouble(rows.get(row1).getConcentration(check));
											c2 = Double.parseDouble(rows.get(row2).getConcentration(check));
										}
										catch(Throwable e)
										{
											errorMessage.setText("There was a problem with your input.");
											box4.add(errorMessage);
											return;
										}
										if(Function.sigFigs(rows.get(row1).getConcentration(check)) < sigFigs)
										{
											sigFigs = Function.sigFigs(rows.get(row1).getConcentration(check));
										}
										if(Function.sigFigs(rows.get(row2).getConcentration(check)) < sigFigs)
										{
											sigFigs = Function.sigFigs(rows.get(row1).getConcentration(check));
										}
										if(check != order && c1 != c2) equal = false;
									}
								}
								if(equal) found = true;
							}
						}
						if(found) break;
					}
					if(found) break;
				}
				if(!found)
				{
					errorMessage.setText("These values are not sufficient to calculate the rate law.");
					box4.add(errorMessage);
					return;
				}
				double toOrder = Double.parseDouble(rows.get(row1).getConcentration(order)) / Double.parseDouble(rows.get(row2).getConcentration(order)), 
						rateRatio; 
				try
				{
					double rate1 = Double.parseDouble(rows.get(row1).getRate()), rate2 = Double.parseDouble(rows.get(row2).getRate()); 
					rateRatio = rate1 / rate2;
				}
				catch(Throwable e)
				{
					errorMessage.setText("There was a problem with your input.");
					box4.add(errorMessage);
					return;
				}		
				int testOrder = 1;
				for(; Math.abs(Math.pow(toOrder, testOrder) - rateRatio) < .01; testOrder++);
				orders[order] = testOrder;
			}
			String rateLaw = "<html>Rate = k";
			double withoutK = 1;
			int sumOrders = 0;
			for(int index = 0; index < orders.length; index ++)
			{
				String cString = compounds.get(index).toString();
				if(compounds.get(index).getNum() != 1) cString = cString.substring(1);
				cString = "[" + cString + "]";
				rateLaw += cString;
				if(orders[index] != 1) rateLaw += "<sup>" + orders[index] + "</sup>";
				withoutK *= Math.pow(Double.parseDouble(rows.get(0).getConcentration(index)), orders[index]);
				sumOrders += orders[index];
			}
			law.setText(rateLaw);
			double k = Double.parseDouble(rows.get(0).getRate()) / withoutK;
			String unit = "L<sup>" + (sumOrders - 1) + "</sup>/mol<sup>" + (sumOrders - 1) + "</sup>*s";
			kValue.setText("<html>k = " + Function.withSigFigs(k, sigFigs) + unit + "<html>");
			resultPanel.setVisible(true);
		}
	}
	
	private class Reset implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			panel.setVisible(false);
			panel.removeAll();
			setPanel();
			panel.setVisible(true);
		}
	}
	
	public JPanel getPanel() 
	{
		return panel;
	}

	public boolean equation()
	{
		return true;
	}
	
	public Equation saveEquation()
	{
		return reader.saveEquation();
	}
	
	public void useSaved(Equation equation)
	{
		reader.useSaved(equation);
	}
}