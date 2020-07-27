package com.up.terrainengine.gui;

import com.up.terrainengine.operator.Terminal;
import com.up.terrainengine.operator.Terminal.Mode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ********* UNUSED STUFF ***********
 * @author Ricky
 */
public class TerminalDisplay extends Container {
    
    Terminal t;

    public TerminalDisplay(Terminal t) {
        this.t = t;
        setSize(10, 10);
        addMouseListener(new ToolTip());
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(t.getMode() == Mode.INPUT ? Color.RED : Color.GREEN);
        g.fillOval(0, 0, 10, 10);
        g.setClip(0, 0, 105, 15);
        super.paint(g);
    }
    
    private class ToolTip extends MouseAdapter {
        
        private Label display = new Label(t.getDescription());
        {
            display.setBounds(5, 5, 100, 10);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            getParent().getParent().getParent().add(display);
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            getParent().getParent().getParent().remove(display);
            repaint();
        }

        private Container getRootContainer(Container c) {
            if (c.getParent() != null) return getRootContainer(c.getParent());
            return c;
        }
    }
    
}
