package com.up.terrainengine.gui.components;

import com.up.terrainengine.structures.Gradient;
import com.up.terrainengine.structures.Gradient.Stop;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.JColorChooser;

/**
 *
 * @author Ricky
 */
public class GradientEditor extends Panel {
    
    private Gradient gradient;
    private HashMap<Stop, Canvas> swatches = new HashMap<>();

    public GradientEditor(Gradient g) {
        this.gradient = g;
        setLayout(null);
        setMinimumSize(new Dimension(100, 50));
        setSize(100, 50);
        for (Stop s : gradient.getStops()) {
            addSwatchSelector(s, getSwatchSelector(s));
        }
        addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    addStop((double)e.getX() / getWidth());
                }
            });
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        Iterator<Stop> it = gradient.getStops().iterator();
        Stop prev = it.next();
        while (it.hasNext()) {
            Stop cur = it.next();
            int prevPos = (int)(prev.getPosition() * getWidth());
            int width = (int)((cur.getPosition() - prev.getPosition()) * getWidth()) + 1;
            g2d.setPaint(new GradientPaint(prevPos, 0, prev.getColor(), prevPos + width, 0, cur.getColor()));
            g2d.fillRect(prevPos, 0, width, getHeight());
            prev = cur;
        }
        super.paint(g);
    }

    @Override
    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        for (Entry<Stop, Canvas> e : swatches.entrySet()) {
            e.getValue().setLocation((int)(e.getKey().getPosition() * getWidth()) - 10, getHeight() / 2 - 10);
        }
    }
    
    private void addStop(double location) {
        Color initColor = gradient.getColorAt(location);
        Stop s = new Stop(initColor, location);
        gradient.addStop(s);
        addSwatchSelector(s, getSwatchSelector(s));
    }
    
    private void removeStop(Gradient.Stop s) {
        remove(swatches.remove(s));
        gradient.removeStop(s);
        repaint();
    }
    
    private void changeStopColor(Gradient.Stop s) {
        Color newC = JColorChooser.showDialog(null, "Choose new color for stop at " + s.getPosition(), s.getColor());
        s.setColor(newC);
        swatches.get(s).repaint();
        repaint();
    }
    
    private void addSwatchSelector(Stop s, Canvas swatch) {
        swatch.setLocation((int)(s.getPosition() * getWidth()) - 10, getHeight() / 2 - 10);
        swatches.put(s, swatch);
        add(swatch);
    }
    
    private Canvas getSwatchSelector(Gradient.Stop s) {
        Canvas c = new Canvas() {
            @Override
            public void update(Graphics g) {
                paint(g);
            }

            @Override
            public void paint(Graphics g) {
                g.setColor(s.getColor());
                g.fillRect(0, 0, 19, 19);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, 19, 19);
            }
        };
        c.setSize(20, 20);
        c.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1) {
                        changeStopColor(s);
                    } else {
                        removeStop(s);
                    }
                }
            });
        return c;
    }
}
