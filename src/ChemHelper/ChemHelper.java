package ChemHelper;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Functions.*;



public class ChemHelper extends JFrame{		//Primary GUI class
	Container pane;
	JList<Function> funcs;
	
	public ChemHelper(){
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		funcs = new JList<Function>(populateFuncs());
		funcs.addListSelectionListener(new FuncListener());
		
		pane.add(funcs, BorderLayout.WEST);
		pane.add(new JPanel());
		funcs.setSize(200, HEIGHT);
		
		setSize(1100,600);
		setVisible(true);
	}

	
	private Function[] populateFuncs() {
		Function[] funcs = new Function[2];
		
		funcs[0] = new PeriodicTable();
		funcs[1] = new ElectronShell();
		return funcs;
	}
	
	private class FuncListener implements ListSelectionListener{

		public void valueChanged(ListSelectionEvent arg0) {
			changeFunc(arg0.getLastIndex());
		}

		private void changeFunc(int lastIndex) {
			pane.remove(1);
			JPanel func = funcs.getModel().getElementAt(lastIndex).getPanel();
			pane.add(func);
			func.setVisible(true);
		}
		
	}

	public static void main(String[] args){
		new ChemHelper();
	}
}