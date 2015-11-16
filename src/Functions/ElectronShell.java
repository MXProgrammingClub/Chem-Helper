package Functions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Elements.Element;

public class ElectronShell extends Function {
    private JRadioButton num, sym, name;
    private JPanel buttons, panel, enterPanel;
    private JLabel info, results;
    private JButton calc;
    private JTextField enter;
    private ButtonGroup options;

    public ElectronShell() {
        super("Electron Shell Configuration");
        options = new ButtonGroup();
        num = new JRadioButton("Atomic Number");
        sym = new JRadioButton("Symbol");
        name = new JRadioButton("Element Name");
        options.add(num);
        options.add(sym);
        options.add(name);
        buttons = new JPanel();
        buttons.setLayout(new GridLayout(3, 1));
        buttons.add(num);
        buttons.add(sym);
        buttons.add(name);
        info = new JLabel("");
        results = new JLabel();
        enter = new JTextField(20);
        calc = new JButton("Find Configuration");
        calc.addActionListener(new ButtonListener());
        enterPanel = new JPanel();
        enterPanel.add(enter);
        enterPanel.add(calc);
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(buttons, BorderLayout.WEST);
        panel.add(info, BorderLayout.NORTH);
        panel.add(enterPanel, BorderLayout.CENTER);
        panel.add(results, BorderLayout.SOUTH);
    }

    public JPanel getPanel() {
        return panel;
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            String input = enter.getText();
            Element[] table = PeriodicTable.TABLE;
            if (num.isSelected()) {
                try {
                    int atomicNum = Integer.parseInt(input);
                    if (atomicNum < 1 || table.length < atomicNum) {
                        results.setText(atomicNum + " is not a valid atomic number.");
                    } else {
                        results.setText(table[atomicNum - 1].getEShell());
                    }
                } catch (NumberFormatException e) {
                    results.setText("Please enter a number.");
                }

            }
            if (sym.isSelected()) {
                boolean found = false;
                for (int element = 0; element < table.length && !found; element++) {
                    if (table[element].getSymbol().equals(input)) {
                        results.setText(table[element].getEShell());
                        found = true;
                    }
                    if (!found) {
                        results.setText(input + " is not a valid symbol.");
                    }
                }
            }
            if (name.isSelected()) {
                boolean found = false;
                for (int element = 0; element < table.length && !found; element++) {
                    if (table[element].getName().equals(input)) {
                        results.setText(table[element].getEShell());
                        found = true;
                    }
                    if (!found) {
                        results.setText(input + " is not a valid element.");
                    }
                }
            }
        }
    }
}