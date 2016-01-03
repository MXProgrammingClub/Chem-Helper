/*
 * The main class for the ChemHelper project
 * 
 * Author: Julia McClellan, Luke Giacalone, Ted Pyne -- MXCSClub
 * Version: 12/31/2015
 */

package ChemHelper;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Equation.Equation;
import Functions.*;



public class ChemHelper extends JFrame{		//Primary GUI class
	Container pane;
	JPanel last, buttons, eqButtons, numButtons;
	JMenuBar menu;
	Function[] funcs;
	JButton saveEq, useEq, saveNum, useNum;
	Equation equation;
	Function lastFunc;
	LinkedList<Double> savedNumbers;
	
	public ChemHelper(){
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		createMenu();
		pane.add(menu, BorderLayout.NORTH);
		pane.add(funcs[0].getPanel(), BorderLayout.WEST); //sets periodic table to show by default
		last = funcs[0].getPanel();
		lastFunc = funcs[0];
		
		saveEq = new JButton("Save equation");
		saveEq.addActionListener(new EquationSaver());
		useEq = new JButton("Use saved");
		useEq.addActionListener(new EquationSaver());
		eqButtons = new JPanel();
		eqButtons.add(saveEq);
		eqButtons.add(useEq);
		eqButtons.setVisible(false);
		
		saveNum = new JButton("Save numbers");
		saveNum.addActionListener(new NumberSaver());
		useNum = new JButton("Use saved");
		useNum.addActionListener(new NumberSaver());
		numButtons = new JPanel();
		numButtons.add(saveNum);
		numButtons.add(useNum);
		numButtons.setVisible(false);
		savedNumbers = new LinkedList<Double>();
		
		buttons = new JPanel();
		buttons.add(eqButtons);
		buttons.add(numButtons);
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
		funcs = new Function[14];
		funcs[0] = new PeriodicTable();
		funcs[1] = new ElectronShell();
		funcs[2] = new CompoundStoichiometry();
		funcs[3] = new Stoichiometry();
		funcs[4] = new LimitingReactant();
		funcs[5] = new PercentYield();
		funcs[6] = new IdealGas();
		funcs[7] = new ContainerChanges();
		funcs[8] = new Effusion();
		funcs[9] = new EquationReader();
		funcs[10] = new RateLaw();
		funcs[11] = new Combustion();
		funcs[12] = new Empirical();
		funcs[13] = new Density();
		
		String[] menuNames = {"General Information", "Stoichiometry", "Gas Laws", "Reactions", "Other"}; //Lists the names of the different menus on the menu bar.
		int[] menuCutoffs = {0, 2, 6, 9, 12}; //Specifies the indices where a new menu would start from funcs
		
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
				if(lastFunc.equation()) eqButtons.setVisible(true);
				else eqButtons.setVisible(false);
				if(lastFunc.number()) numButtons.setVisible(true);
				else numButtons.setVisible(false);
				buttons.setVisible(true);
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
				if(lastFunc.saveEquation() != null) equation = lastFunc.saveEquation();
			}
			else
			{
				if(equation != null) lastFunc.useSaved(equation);
			}
		}
	}

	private class NumberSaver implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			if(((JButton)arg0.getSource()).getText().equals("Save numbers"))
			{
				double toSave = lastFunc.saveNumber();
				if(toSave != 0 && savedNumbers.indexOf(toSave) == -1) savedNumbers.addFirst(toSave);
			}
			else
			{
				Object selected = JOptionPane.showInputDialog(pane, "Choose a number to use", "Choose Number", JOptionPane.PLAIN_MESSAGE, 
						null, savedNumbers.toArray(), new Double(0));
				if(selected instanceof Double) lastFunc.useSavedNumber((Double)selected);
			}
		}
	}
	
	public static void main(String[] args){
		new ChemHelper();
	}
}