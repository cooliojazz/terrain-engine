package com.up.terrainengine.gui;

import com.up.terrainengine.operator.Link;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Terminal;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Ricky
 */
public class GraphDisplay extends Panel {
    
    private ArrayList<GraphNodeDisplay> nodes = new ArrayList<>();
    Linking linking = null;
    private MouseTracker tracker = new MouseTracker();

    public GraphDisplay() {
        super(null);
        setMinimumSize(new Dimension(400, 400));
        setPreferredSize(new Dimension(400, 400));
        addMouseMotionListener(tracker);
    }

    public ArrayList<GraphNodeDisplay> getNodes() {
        return nodes;
    }
    
    public void addNode(Operator o) {
        GraphNodeDisplay node = new GraphNodeDisplay(o);
        add(node);
        nodes.add(node);
    }
    
    public void removeNode(GraphNodeDisplay n) {
        n.getOperator().getInputs().forEach(Terminal::unlink);
        n.getOperator().getOutputs().forEach(Terminal::unlink);
        remove(n);
        nodes.remove(n);
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics bg = buffer.getGraphics();
        if (linking != null) {
            bg.setColor(Color.YELLOW.darker());
            bg.drawLine(linking.source.getTerminalPosition(linking.start).x, linking.source.getTerminalPosition(linking.start).y, tracker.getLast().x, tracker.getLast().y);
        }
        for (GraphNodeDisplay node : nodes) {
            for (Terminal t : node.getOperator().getTerminals()) {
                Link l = t.getLink();
                if (l != null) {
                    bg.setColor(Color.YELLOW.darker());
                    Point start;
                    Point end;
                    if (t == l.getInput()) {
                        start = node.getTerminalPosition(l.getInput());
                        end = nodes.stream().filter(n -> n.containsTerminal(l.getOutput())).findAny().get().getTerminalPosition(l.getOutput());
                    } else {
                        start = nodes.stream().filter(n -> n.containsTerminal(l.getInput())).findAny().get().getTerminalPosition(l.getInput());
                        end = node.getTerminalPosition(l.getOutput());
                    }
                    bg.drawLine(start.x, start.y, end.x, end.y);
                }
            }
        }
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }
    
    static class Linking {
        
        GraphNodeDisplay source;
        Terminal start;
        Terminal end;

        public Linking(Terminal start, GraphNodeDisplay source) {
            this.source = source;
            this.start = start;
        }
        
    }
    
    private class MouseTracker extends MouseAdapter {

        private Point last;
        
        @Override
        public void mouseDragged(MouseEvent e) {
            last = e.getPoint();
            for (GraphNodeDisplay node : nodes) {
                if (node.getBounds().contains(e.getPoint()) && e.getSource() != node) node.dispatchEvent(new MouseEvent(GraphDisplay.this, e.getID(), e.getWhen(), e.getModifiers(), e.getX() - node.getX(), e.getY() - node.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            }
            repaint();
        }

//        @Override
//        public void mouseReleased(MouseEvent e) {
//            for (GraphNodeDisplay node : nodes) {
//                if (node.getBounds().contains(e.getPoint()) && e.getSource() != node) node.dispatchEvent(new MouseEvent(GraphDisplay.this, e.getID(), e.getWhen(), e.getModifiers(), e.getX() - node.getX(), e.getY() - node.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton()));
//            }
//        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
            last = e.getPoint();
            repaint();
        }

        public Point getLast() {
            return last;
        }
        
    }
}
