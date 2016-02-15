package Functions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
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
	private Box box;
	private JButton calculate;
	private JLabel result;
	
	public OxidationNumber() {
		super("Oxidation Number");
		setPanel();
	}


	private void setPanel() {
		panel = new JPanel();
		panel.add(new JLabel("Enter the compound to calulate Oxidation numbers for: "));
		box = Box.createVerticalBox();
		field = new TextField(80);
		calculate = new JButton("Calulate");
		calculate.addActionListener(new ButtonListener());
		box.add(field);
		box.add(calculate);
		panel.add(box);
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
				box.add(new JLabel("There was an error with your input"));
			}
		}

		private void findNumbers(Compound compound) {
			Ions[] ions = compound.getIons();
			int charge = 0;
			for(Ions ion:ions) charge+=ion.getCharge()*ion.getNum();
			System.out.println(charge);
		}
		
	}
}
