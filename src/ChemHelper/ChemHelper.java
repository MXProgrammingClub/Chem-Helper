package ChemHelper;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Functions.*;



public class ChemHelper extends JFrame{		//Primary GUI class
	Container pane;
	JList<Function> funcs;
	Function[] funcList;
	JPanel last;
	
	public ChemHelper(){
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		funcList = populateFuncs();
		funcs = new JList<Function>(funcList);
		funcs.addListSelectionListener(new FuncListener());
		
		pane.add(funcs, BorderLayout.WEST);
		pane.add(new JPanel());
		funcs.setSize(200, HEIGHT);
		
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	
	private Function[] populateFuncs() {
		Function[] funcs = new Function[3];
		
		funcs[0] = new PeriodicTable();
		funcs[1] = new ElectronShell();
		funcs[2] = new EquationReader();
		return funcs;
	}
	
	private class FuncListener implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent arg0) {				
			if(last!=null) pane.remove(last);
			JPanel func = funcList[funcs.getSelectedIndex()].getPanel();
			pane.add(func, BorderLayout.EAST);
			
			func.setVisible(true);
			func.repaint();
			pane.repaint();
			pack();
			repaint();
			last = func;
		}
	}

	public static void main(String[] args){
		new ChemHelper();
	}
}