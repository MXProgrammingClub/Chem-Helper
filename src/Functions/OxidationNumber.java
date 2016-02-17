package Functions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ChemHelper.InvalidInputException;
import Equation.Compound;
import Equation.Ions;
import HelperClasses.TextField;

public class OxidationNumber extends Function{	//Oxidation number in compound
	private JPanel panel;
	private TextField field;
	private JButton calculate;
	private JLabel result;
	
	public OxidationNumber() {
		super("Oxidation Number");
		setPanel();
	}


	private void setPanel() {
		panel = new JPanel();
		JPanel subpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTH;
		subpanel.add(new JLabel("Enter the compound to calulate Oxidation numbers for: "), c);
		c.gridy++;
		field = new TextField(TextField.EQUATION);
		calculate = new JButton("Calulate");
		calculate.addActionListener(new ButtonListener());
		subpanel.add(field, c);
		c.gridy++;
		subpanel.add(calculate, c);
		result = new JLabel();
		subpanel.add(result, c);
		panel.add(subpanel);
	}


	public JPanel getPanel() {
		return panel;
	}
	
	private class ButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			try {
				Compound compound = Compound.parseCompound(field.getText());
				findNumbers(compound);
			} catch (InvalidInputException e) {
				result.setText("There was an error with your input");
			}
		}

		private void findNumbers(Compound compound) {
			Ions[] ions = compound.getIons();
			int charge = 0;
			for(Ions ion:ions) charge+=ion.getCharge()*ion.getNum();
			result.setText(charge + "");
		}
		
	}
}
