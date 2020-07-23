package com.up.terrainengine.gui.components;

import com.up.terrainengine.structures.PolynomialMap;
import com.up.terrainengine.structures.PolynomialMap.ControlPoint;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author Ricky
 */
public class CurveEditor extends Panel {
    
    private PolynomialMap map;
    private HashMap<ControlPoint, Canvas> points = new HashMap<>();

    public CurveEditor(PolynomialMap m) {
        this.map = m;
        setLayout(null);
        setMinimumSize(new Dimension(200, 200));
        setSize(200, 200);
        for (ControlPoint s : map.getPoints()) {
            addPointMover(s, getPointMover(s));
        }
        addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    addPoint((double)e.getX() / getWidth(), (double)(getHeight() - e.getY()) / getHeight());
                }
            });
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }
    
    @Override
    public void paint(Graphics g) {
        g.setClip(-10, -10, getWidth() + 10, getHeight() + 10);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.BLACK);
        for (int i = 1; i < getWidth(); i++) {
            g.drawLine(i - 1, getHeight() - (int)(map.getValueAt((double)(i - 1) / getWidth()) * getHeight()), i, getHeight() - (int)(map.getValueAt((double)i / getWidth()) * getHeight()));
        }
        
        super.paint(g);
    }

    @Override
    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        for (Entry<ControlPoint, Canvas> e : points.entrySet()) {
            e.getValue().setLocation((int)(e.getKey().getPosition() * getWidth()) - 10, getHeight() - (int)(e.getKey().getValue() * getHeight()) - 5);
        }
    }
    
    private void addPoint(double location, double value) {
        ControlPoint s = new ControlPoint(location, value);
        map.addPoint(s);
        repaint();
        addPointMover(s, getPointMover(s));
    }
    
    private void addPointMover(ControlPoint p, Canvas mover) {
        mover.setLocation((int)(p.getPosition() * getWidth()) - 5, getHeight() - (int)(p.getValue() * getHeight()) - 5);
        points.put(p, mover);
        add(mover);
    }
    
    private Canvas getPointMover(ControlPoint p) {
        Canvas c = new Canvas() {
            @Override
            public void update(Graphics g) {
                paint(g);
            }

            @Override
            public void paint(Graphics g) {
                g.setColor(Color.GRAY);
                g.fillRect(0, 0, 9, 9);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, 9, 9);
            }
        };
        c.setSize(10, 10);
        c.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() != 1) {
                        map.removePoint(p);
                        repaint();
                        remove(c);
                    }
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    
                }
            });
        return c;
    }
}
