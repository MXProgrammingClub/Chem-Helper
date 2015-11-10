package Functions;

import javax.swing.JFrame;
import javax.swing.JPanel;


public abstract class Function {
	
	public abstract JPanel getPanel();		//Return the frame containing all components for that chem function
	
	public abstract String toString();		//Return function name
	
}
