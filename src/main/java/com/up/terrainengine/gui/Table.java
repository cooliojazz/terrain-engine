package com.up.terrainengine.gui;

import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 *
 * @author Ricky
 */
public class Table extends Panel {
    
    private TextField[][] inputs;

    public Table(int size) {
        setLayout(new GridLayout(size, size));
        this.inputs = new TextField[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                TextField f = new TextField();
                f.setName(x + "," + y);
                add(f);
                inputs[x][y] = f;
            }
        }
    }
    
    public void addActionListener(ActionListener l) {
        for (int x = 0; x < inputs.length; x++) {
            for (int y = 0; y < inputs.length; y++) {
                inputs[x][y].addActionListener(l);
                inputs[x][y].addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            l.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, ((TextField)e.getSource()).getText()));
                        }
                    });
            }
        }
    }
    
    public String getValue(int x, int y) {
        return inputs[x][y].getText();
    }
    
    public void setValue(int x, int y, String value) {
        inputs[x][y].setText(value);
    }
    
}
