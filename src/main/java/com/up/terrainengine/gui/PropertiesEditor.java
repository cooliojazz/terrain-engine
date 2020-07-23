package com.up.terrainengine.gui;

import com.up.terrainengine.gui.components.CurveEditor;
import com.up.terrainengine.gui.components.GradientEditor;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.structures.PolynomialMap;
import com.up.terrainengine.structures.Gradient;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BoxLayout;

/**
 *
 * @author Ricky
 */
public class PropertiesEditor extends java.awt.Dialog {

    private Properties props;
    private Runnable onClose;
    
    /**
     * Creates new form PropertiesEditor
     */
    public PropertiesEditor(Operator o, Frame parent, boolean modal, Runnable onClose) {
        super(parent, modal);
        this.onClose = onClose;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setTitle("Properties of " + o.getClass().getSimpleName());
        this.props = o.getProperties();
        initComponents();
        for (Map.Entry<String, Object> e : props) {
            Panel row = new Panel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.add(new Label(e.getKey()));
            row.add(getEditorFor(e.getKey(), e.getValue()));
            add(row);
        }
        setResizable(false);
        setMinimumSize(new Dimension(250, 10));
        pack();
        o.changed();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new java.awt.Panel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        panel1.setLayout(new javax.swing.BoxLayout(panel1, javax.swing.BoxLayout.Y_AXIS));
        add(panel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
        onClose.run();
    }//GEN-LAST:event_closeDialog

    
    private Component getEditorFor(String key, Object o) {
        if (o.getClass().isEnum()) {
            Choice choice = new Choice();
            for (Object e : o.getClass().getEnumConstants()) {
                choice.add(e.toString());
            }
            choice.select(o.toString());
            choice.addItemListener(e -> props.set(key, Enum.valueOf((Class<? extends Enum>)o.getClass(), (String)e.getItem())));
            return choice;
        } else {
            switch (props.getType(key).getType().getTypeName()) {
                case "com.up.terrainengine.structures.VectorMap<java.lang.Double>": {
                    VectorMap<Double> map = (VectorMap<Double>)o;
                    
                    Table table = new Table(map.size());
                    for (int x = 0; x < map.size(); x++) {
                        for (int y = 0; y < map.size(); y++) {
                            table.setValue(x, y, map.get(x, y).get().toString());
                        }
                    }
                    table.addActionListener(e -> {
                            TextField field = (TextField)e.getSource();
                            int x = Integer.parseInt(field.getName().split(",")[0]);
                            int y = Integer.parseInt(field.getName().split(",")[1]);
                            map.set(x, y, new Vector<>(Double.parseDouble(field.getText())));
                        });
                    return table;
                }
                case "com.up.terrainengine.structures.Gradient": {
                    return new GradientEditor((Gradient)props.get(key));
                }
                case "com.up.terrainengine.structures.PolynomialMap": {
                    return new CurveEditor((PolynomialMap)props.get(key));
                }
                case "java.lang.Double": {
                    TextField field = new TextField(((Double)props.get(key)).toString());
                    field.addActionListener(e -> props.set(key, Double.parseDouble(field.getText())));
                    field.addFocusListener(new FocusAdapter() {public void focusLost(FocusEvent e) {props.set(key, Double.parseDouble(field.getText()));}});
                    return field;
                }
                case "java.lang.Integer": {
                    TextField field = new TextField(((Integer)props.get(key)).toString());
                    field.addActionListener(e -> props.set(key, Integer.parseInt(field.getText())));
                    field.addFocusListener(new FocusAdapter() {public void focusLost(FocusEvent e) {props.set(key, Integer.parseInt(field.getText()));}});
                    return field;
                }
                default: {
                    return new Label("(No editor for " + props.getType(key).getType().getTypeName() + ") " + o.toString());
                }
            }
        }
    }
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                PropertiesEditor dialog = new PropertiesEditor(new java.awt.Frame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Panel panel1;
    // End of variables declaration//GEN-END:variables
}
