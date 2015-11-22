package Functions;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ParticleEquations extends Function {
	JButton findUnknown;
	JTextField energy, freq, wavelength;
	JLabel energyL, freqL, waveL;
	ActionListener listen;
	
	JPanel panel;
	
	public ParticleEquations() {
		super("Particle equations");
		listen = new ButtonListener();
		
		findUnknown = new JButton("Find unknowns");
		
		energy = new JTextField("          ");
		freq = new JTextField("          ");
		wavelength = new JTextField("          ");
		
		energyL = new JLabel("Energy");
		freqL = new JLabel("Frequency");
		waveL = new JLabel("Wavelength");

		panel = new JPanel();
		panel.setLayout(new GridLayout(2,4));
		
		panel.add(wrapInFlow(energy));
		panel.add(wrapInFlow(freq));
		panel.add(wrapInFlow(wavelength));
		panel.add(wrapInFlow(findUnknown));
		
		panel.add(wrapInFlow(energyL));
		panel.add(wrapInFlow(freqL));
		panel.add(wrapInFlow(waveL));
		
		
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}		//E=hc/V
	
	private class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == findUnknown){
				
			}
			
		}
		
	}
	//public static 

}
