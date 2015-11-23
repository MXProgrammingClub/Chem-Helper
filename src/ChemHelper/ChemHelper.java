/*
 * The main class for the ChemHelper project
 * 
 * Author: Julia McClellan, Luke Giacalone, Ted Pyne -- MXCSClub
 * Version: 11/22/2015
 */

package ChemHelper;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import Functions.*;



public class ChemHelper extends JFrame{		//Primary GUI class
	Container pane;
	JPanel last;
	JMenuBar menu;
	Function[] funcs;
	
	public ChemHelper(){
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		createMenu();
		pane.add(menu, BorderLayout.NORTH);
		pane.add(funcs[0].getPanel(), BorderLayout.WEST); //sets periodic table to show by default
		last = funcs[0].getPanel();
		pack();
		this.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void createMenu()
	{
		funcs = new Function[4];
		funcs[0] = new PeriodicTable();
		funcs[1] = new ElectronShell();
		funcs[2] = new EquationReader();
		funcs[3] = new ParticleEquations();
		
		//Currently this system is completely random as there are not enough things to make an actually useful system but I wanted to make the framework
		String[] menuNames = {"Things you may want to use", "Things that are currently unusable"}; //Lists the names of the different menus on the menu bar.
		int[] menuCutoffs = {0, 2}; //Specifies the indices where a new menu would start from funcs
		
		menu = new JMenuBar();
		for(int menuNum = 0; menuNum < menuCutoffs.length; menuNum++)
		{
			int startIndex = menuCutoffs[menuNum], endIndex;
			if(menuNum + 1 == menuCutoffs.length) endIndex = funcs.length - 1;
			else endIndex = menuCutoffs[menuNum + 1] - 1;
			JMenu thisMenu = new JMenu(menuNames[menuNum]);
			for(int index = startIndex; index <= endIndex; index++)
			{
				thisMenu.add(new FunctionMenuItem(funcs[index]));
			}
			menu.add(thisMenu);
		}
	}

	private class FunctionMenuItem extends JMenuItem
	{
		private Function function;
		
		public FunctionMenuItem(Function function)
		{
			super(function.toString());
			this.function = function;
			addActionListener(new FunctionListener());
		}
		
		public Function getFunction()
		{
			return function;
		}
		
		private class FunctionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if(last!=null) pane.remove(last);
				JPanel func = ((FunctionMenuItem)arg0.getSource()).getFunction().getPanel();
				pane.add(func, BorderLayout.WEST);
				
				//func.setVisible(true);
				//func.repaint();
				pane.repaint();
				pack();
				
				//repaint();
				last = func;
			}
		}
	}
	
	public static void main(String[] args){
		new ChemHelper();
	}
}