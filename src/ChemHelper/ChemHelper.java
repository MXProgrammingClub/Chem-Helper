/*
 * The main class for the ChemHelper project
 * 
 * Author: Julia McClellan, Luke Giacalone, Ted Pyne -- MXCSClub
 * Version: 11/22/2015
 */

package ChemHelper;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import Equation.Equation;
import Functions.*;



public class ChemHelper extends JFrame{		//Primary GUI class
	Container pane;
	JPanel last, buttons;
	JMenuBar menu;
	Function[] funcs;
	JButton save, use;
	Equation equation;
	Function lastFunc;
	
	public ChemHelper(){
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		createMenu();
		pane.add(menu, BorderLayout.NORTH);
		pane.add(funcs[0].getPanel(), BorderLayout.WEST); //sets periodic table to show by default
		last = funcs[0].getPanel();
		lastFunc = funcs[0];
		
		save = new JButton("Save equation");
		save.addActionListener(new EquationSaver());
		use = new JButton("Use saved");
		use.addActionListener(new EquationSaver());
		buttons = new JPanel();
		buttons.add(save);
		buttons.add(use);
		pane.add(buttons, BorderLayout.SOUTH);
		buttons.setVisible(false);
		equation = null;
		
		pack();
		this.setPreferredSize(this.getSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void createMenu()
	{
		funcs = new Function[7];
		funcs[0] = new PeriodicTable();
		funcs[1] = new ElectronShell();
		funcs[2] = new Stoichiometry();
		funcs[3] = new LimitingReactant();
		funcs[4] = new PercentYield();
		funcs[5] = new EquationReader();
		funcs[6] = new RateLaw();
		
		//Currently this system is completely random as there are not enough things to make an actually useful system but I wanted to make the framework
		String[] menuNames = {"General Information", "Stoichiometry", "Other"}; //Lists the names of the different menus on the menu bar.
		int[] menuCutoffs = {0, 2, 5}; //Specifies the indices where a new menu would start from funcs
		
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
				lastFunc = ((FunctionMenuItem)arg0.getSource()).getFunction();
				JPanel func = lastFunc.getPanel();
				pane.add(func, BorderLayout.WEST);
				if(lastFunc.equation()) buttons.setVisible(true);
				else buttons.setVisible(false);
				pane.repaint();
				pack();
				last = func;
			}
		}
	}
	
	private class EquationSaver implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			if(((JButton)arg0.getSource()).getText().equals("Save equation"))
			{
				equation = lastFunc.saveEquation();
			}
			else
			{
				if(equation != null) lastFunc.useSaved(equation);
			}
		}
	}
	
	public static void main(String[] args){
		new ChemHelper();
	}
}