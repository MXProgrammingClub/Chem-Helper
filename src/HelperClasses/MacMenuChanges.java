package HelperClasses;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ChemHelper.ChemHelper;

public class MacMenuChanges {
	
	ChemHelper chelper;
	
	public void changeMenu(ChemHelper ch) {
		chelper = ch;
		chelper.menu.getMenu(chelper.menu.getMenuCount() - 1).remove(0);
		com.apple.eawt.Application.getApplication().setAboutHandler(new AboutWindow());
		chelper.menu.getMenu(chelper.menu.getMenuCount() - 1).remove(0);
		com.apple.eawt.Application.getApplication().setPreferencesHandler(new PreferenceWindow());
	}
	
	private class AboutWindow extends JFrame implements com.apple.eawt.AboutHandler {
		public void handleAbout(com.apple.eawt.AppEvent.AboutEvent arg0) {
			if(chelper.last != null) chelper.pane.remove(chelper.last);
			chelper.lastFunc = chelper.funcs[24];
			JPanel func = chelper.funcs[24].getPanel();
			chelper.pane.add(func, BorderLayout.WEST);
			chelper.eqButtons.setVisible(chelper.lastFunc.equation());
			chelper.numButtons.setVisible(chelper.lastFunc.number());
			chelper.help.setVisible(chelper.lastFunc.help());
			chelper.buttons.setVisible(true);
			chelper.pane.repaint();
			chelper.pack();
			chelper.last = func;
			chelper.lastFunc.resetFocus();
		}
	}
	
	private class PreferenceWindow extends JFrame implements com.apple.eawt.PreferencesHandler {
		public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent arg0) {
			if(chelper.last != null) chelper.pane.remove(chelper.last);
			chelper.lastFunc = chelper.funcs[25];
			JPanel func = chelper.funcs[25].getPanel();
			chelper.pane.add(func, BorderLayout.WEST);
			chelper.eqButtons.setVisible(chelper.lastFunc.equation());
			chelper.numButtons.setVisible(chelper.lastFunc.number());
			chelper.help.setVisible(chelper.lastFunc.help());
			chelper.buttons.setVisible(true);
			chelper.pane.repaint();
			chelper.pack();
			chelper.last = func;
			chelper.lastFunc.resetFocus();
		}
	}
	
}
