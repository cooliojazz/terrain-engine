package com.up.terrainengine;

import com.up.terrainengine.gui.MainDisplay;

/**
 *
 * @author Ricky
 */
public class Main {
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		System.setProperty("sun.awt.noerasebackground", "true");
        MainDisplay gui = new MainDisplay();
        gui.setVisible(true);
    }
    
}
