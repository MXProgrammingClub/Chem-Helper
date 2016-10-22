/*
 * The main class for the ChemHelper project
 * 
 * Author: Julia McClellan, Luke Giacalone, and Ted Pyne -- MXCSClub
 * Version: 2/9/2016
 */

package ChemHelper;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import Equation.Equation;
import Functions.*;
import HelperClasses.MacMenuChanges;
import HelperClasses.Preferences;

public class ChemHelper extends JFrame { //Primary GUI class
	public static Dimension dimension;
	//place where to save the preferences file -- use extension .prefs to make it harder to edit by hand
	private static final String PREFS_FILE = "preferences.prefs"; 
	
	public Container pane;
	public JPanel last, buttons, eqButtons, numButtons;
	public JMenuBar menu;
	public Function[] funcs;
	public JButton saveEq, useEq, saveNum, useNum, help;
	public Equation equation;
	public Function lastFunc;
	public LinkedList<Double> savedNumbers;
	public Preferences preferences;
	
	public ChemHelper() {
		LoadingDialog ld = new LoadingDialog();
		new TeXFormula("").createTeXIcon(TeXConstants.STYLE_DISPLAY, 20); //loads the latex package
		
		try {
			preferences = new Preferences(PREFS_FILE);
		} catch (IOException e) {}
		
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		this.setTitle("ChemHelper");
		
		createMenu();
		this.setJMenuBar(menu);
		pane.add(funcs[0].getPanel(), BorderLayout.WEST); //sets periodic table to show by default
		last = funcs[0].getPanel();
		lastFunc = funcs[0];
		
		
		if(System.getProperty("os.name").contains("Mac")) {
			MacMenuChanges mac = new MacMenuChanges();
			mac.changeMenu(this);
		}
		
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
		
		Function.setSigFigPref(preferences.getInteger("SigFigs_Format"));
		
		//Add a shutdownhook that will save the preferences set by the user
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					preferences.export();
				} catch (FileNotFoundException | UnsupportedEncodingException e) {}
			}
		});
		
		pack();
		this.setPreferredSize(this.getSize());
		dimension = getPreferredSize();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		ld.dispose();
		JOptionPane.showMessageDialog(this, "UNAUTHORIZED DISTRIBUTION OF CHEMHELPER IS STRICTLY\nPROHIBITED! Any unauthorized"
				+ " distribution is breaking a Major\nSchool Rule and will result in disciplinary action.");
		JOptionPane.showMessageDialog(this, "Welcome to ChemHelper Beta v0.5.0! Please submit bugs and\nsuggestions to"
				+ " https://goo.gl/forms/F0yj4206Jrx2NkT23");
	}

	private void createMenu()
	{
		funcs = new Function[30];
		funcs[0] = new PeriodicTable(getIntPreference("Table_Style"), getBooleanPreference("Table_State_Colors"));
		funcs[1] = new ElectronShell();
		funcs[2] = new Density();
		funcs[3] = new OxidationNumber();
		funcs[4] = new CompoundStoichiometry();
		funcs[5] = new Stoichiometry();
		funcs[6] = new LimitingReactant();
		funcs[7] = new PercentYield();
		funcs[8] = new IdealGas();
		funcs[9] = new ContainerChanges();
		funcs[10] = new Effusion();
		funcs[11] = new EquationReader();
		funcs[12] = new Combustion();
		funcs[13] = new Redox();
		funcs[14] = new Equilibrium();
		funcs[15] = new Neutralization();
		funcs[16] = new GibbsEnergy();
		funcs[17] = new HeatEquation();
		funcs[18] = new RateLaw();
		funcs[19] = new ReactionEnthalpy();
		funcs[20] = new Solutions();
		funcs[21] = new StateChangeTemp();
		funcs[22] = new HenrysLaw();
		funcs[23] = new Dilution();
		funcs[24] = new Empirical();
		funcs[25] = new Waves();
		funcs[26] = new Nuclear();
		funcs[27] = new pHCalculator();
		funcs[28] = new About();
		funcs[29] = new Prefs(this);
		
		String[] menuNames = {"General", "Stoichiometry", "Gas Laws", "Reactions", "Reaction Energy", "Solutions", "Other", "Help"};
			//Lists the names of the different menus on the menu bar.
		int[] menuCutoffs = {0, 4, 8, 11, 16, 20, 24, 28}; //Specifies the indices where a new menu would start from funcs
		
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
		JMenuItem reportMenuItem = new JMenuItem("Submit Report");
		reportMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://goo.gl/forms/F0yj4206Jrx2NkT23"));
				} catch (IOException | URISyntaxException e1) {}
			}
		});
		menu.getMenu(menu.getMenuCount() - 1).add(reportMenuItem);
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
				Object selected = JOptionPane.showInputDialog(pane, "Choose a number to use", "Choose Number"
						, JOptionPane.PLAIN_MESSAGE, null, savedNumbers.toArray(), new Double(0));
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
		funcs[0] = new PeriodicTable(this.getIntPreference("Table_Style"), this.getBooleanPreference("Table_State_Colors"));
		menu.getMenu(0).remove(0);
		menu.getMenu(0).add(new FunctionMenuItem(funcs[0]), 0);
	}
	
	public void changePreference(String key, boolean value) {
		preferences.putBoolean(key, value);
	}
	
	public void changePreference(String key, int value) {
		preferences.putInteger(key, value);
	}
	
	public boolean getBooleanPreference(String key) {
		return preferences.getBoolean(key);
	}
	
	public int getIntPreference(String key) {
		return preferences.getInteger(key);
	}
	
	public static void main(String[] args) {
		if(System.getProperty("os.name").contains("Mac"))
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		ChemHelper chelper = null;
		try {
			chelper = new ChemHelper();
		}
		catch(Throwable e) {
			if(chelper != null) {
				try {
					chelper.preferences.export();
				} catch (FileNotFoundException | UnsupportedEncodingException e1) {}
				chelper.dispose();
			}
			new CrashFrame(e, e.getStackTrace());
		}
	}
	
}