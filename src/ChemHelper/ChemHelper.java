/*
 * The main class for the ChemHelper project
 * 
 * Author: Julia McClellan, Luke Giacalone, and Ted Pyne -- MXCSClub
 * Version: 2/3/2016
 */

package ChemHelper;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.LinkedList;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Equation.Equation;
import Functions.*;

public class ChemHelper extends JFrame {		//Primary GUI class
	Container pane;
	JPanel last, buttons, eqButtons, numButtons;
	JMenuBar menu;
	Function[] funcs;
	JButton saveEq, useEq, saveNum, useNum, help;
	Equation equation;
	Function lastFunc;
	LinkedList<Double> savedNumbers;
	
	private static Preferences preferences; {
		preferences = Preferences.userNodeForPackage(ChemHelper.class);
	}
	
	public ChemHelper() {
		LoadingDialog ld = new LoadingDialog();
		
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		this.setTitle("ChemHelper");
		
		createMenu();
		this.setJMenuBar(menu);
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
		
		help = new JButton("Help");
		help.setVisible(false);
		help.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					JOptionPane.showMessageDialog(pane, lastFunc.getHelp(), "Help", JOptionPane.QUESTION_MESSAGE);
				}
			});
		
		buttons = new JPanel();
		buttons.add(eqButtons);
		buttons.add(numButtons);
		buttons.add(help);
		pane.add(buttons, BorderLayout.SOUTH);
		buttons.setVisible(false);
		equation = null;
		
		pack();
		this.setPreferredSize(this.getSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		ld.dispose();
	}

	private void createMenu()
	{
		funcs = new Function[20];
		funcs[0] = new PeriodicTable(ChemHelper.getIntPreference("Table Style"), ChemHelper.getBooleanPreference("State Colors"));
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
		funcs[12] = new Nuclear();
		funcs[13] = new GibbsEnergy();
		funcs[14] = new Empirical();
		funcs[15] = new Density();
		funcs[16] = new StateChangeTemp();
		funcs[17] = new Waves();
		funcs[18] = new About();
		funcs[19] = new Prefs(this);
		
		String[] menuNames = {"General", "Stoichiometry", "Gas Laws", "Reactions", "Reaction Energy", "Other", "Help"};
			//Lists the names of the different menus on the menu bar.
		int[] menuCutoffs = {0, 2, 6, 9, 13, 14, 18}; //Specifies the indices where a new menu would start from funcs
		
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
				eqButtons.setVisible(lastFunc.equation());
				numButtons.setVisible(lastFunc.number());
				help.setVisible(lastFunc.help());
				buttons.setVisible(true);
				pane.repaint();
				pack();
				last = func;
				lastFunc.resetFocus();
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
	
	//a jframe that acts as a joptionpane to show that the prgram is loading and closes when the program is finished loading
	private class LoadingDialog extends JFrame {
		
		private JButton cancel;
		
		public LoadingDialog() {
			super("ChemHelper");
			Box box = Box.createVerticalBox();
			box.setAlignmentY(CENTER_ALIGNMENT);
			box.add(Box.createVerticalStrut(10));
			JLabel label = new JLabel("Loading ChemHelper...");
			label.setAlignmentX(Box.CENTER_ALIGNMENT);
			box.add(label);
			box.add(Box.createVerticalStrut(10));
			cancel = new JButton("Cancel");
			cancel.setAlignmentX(Box.CENTER_ALIGNMENT);
			box.add(cancel);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.add(box);
			this.setPreferredSize(new Dimension(300, 110));
			this.pack();
			this.setLocationRelativeTo(null);
			this.setVisible(true);
			
			//if cancel is hit, the program ends
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		
	}
	
	public void refreshTable() {
		funcs[0] = new PeriodicTable(ChemHelper.getIntPreference("Table Style"), ChemHelper.getBooleanPreference("State Colors"));
		menu.getMenu(0).remove(0);
		menu.getMenu(0).add(new FunctionMenuItem(funcs[0]), 0);
	}
	
	public static void changePreference(String key, boolean value) {
		preferences.putBoolean(key, value);
	}
	
	public static void changePreference(String key, int value) {
		preferences.putInt(key, value);
	}
	
	public static boolean getBooleanPreference(String key) {
		return preferences.getBoolean(key, false);
	}
	
	public static int getIntPreference(String key) {
		return preferences.getInt(key, 0);
	}
	
	public static void main(String[] args) {
		if(System.getProperty("os.name").equals("Mac OS X"))
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		new ChemHelper();
	}
}