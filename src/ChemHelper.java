
import java.awt.*;

import javax.swing.*;


public class ChemHelper extends JFrame{		//Primary GUI class
	Container pane;
	
	public ChemHelper(){
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
	}
	
	public static void main(String[] args){
		new ChemHelper();
	}
}
