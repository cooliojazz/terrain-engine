package com.up.terrainengine.structures;

import java.awt.Color;
import java.util.*;

/**
 *
 * @author Ricky
 */
public class PolynomialMap {
    
    private TreeSet<ControlPoint> points;

    public PolynomialMap(List<ControlPoint> points) {
        this.points = new TreeSet<>(points);
    }

    public PolynomialMap(ControlPoint... points) {
        this(Arrays.asList(points));
    }

    public PolynomialMap() {
        this(new ControlPoint(0, 0), new ControlPoint(1, 1));
    }
    
    public void addPoint(ControlPoint point) {
        points.add(point);
    }
    
    public void removePoint(ControlPoint point) {
        points.remove(point);
    }
    
    public TreeSet<ControlPoint> getPoints() {
        return points;
    }
    
    public double getValueAt(double pos) {
        double yp = 0;
        Iterator<ControlPoint> iit = points.iterator();
        while (iit.hasNext()) {
            ControlPoint is = iit.next();
            double p = 1;
            Iterator<ControlPoint> jit = points.iterator();
            while (jit.hasNext()) {
                ControlPoint js = jit.next();
                if (is != js) { 
                    p *= (pos - js.getPosition()) / (is.getPosition() - js.getPosition());
                }
            }
            yp += p * is.getValue();
        }
        return Math.min(1, Math.max(0, yp));
    }
    
    public static class ControlPoint implements Comparable<ControlPoint> {
        
        private double value;
        private final double pos;

        public ControlPoint(double pos, double color) {
            this.pos = pos;
            this.value = color;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double color) {
            this.value = color;
        }

        public double getPosition() {
            return pos;
        }

        @Override
        public int compareTo(ControlPoint o) {
            return Double.compare(pos, o.pos);
        }
        
    }
}
