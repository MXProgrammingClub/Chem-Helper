package ChemHelper;
import java.awt.*;

import javax.swing.*;

import Functions.Function;



public class ChemHelper extends JFrame{		//Primary GUI class
	Container pane;
	JList<Function> funcs;
	
	public ChemHelper(){
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		
		funcs = new JList<Function>(populateFuncs());
		
		
		pane.add(funcs, BorderLayout.WEST);
		funcs.setSize(200, HEIGHT);
		
		setSize(800,800);
		setVisible(true);
	}
	
	private Function[] populateFuncs() {
		Function[] funcs = new Function[1];
		
		//funcs[0] = new Function();
		return funcs;
	}

	public static void main(String[] args){
		new ChemHelper();
	}
}