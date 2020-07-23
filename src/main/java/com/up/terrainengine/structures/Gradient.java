package com.up.terrainengine.structures;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.Color;
import java.util.*;

/**
 *
 * @author Ricky
 */
public class Gradient {
    
    private TreeSet<Stop> stops;

    public Gradient(List<Stop> stops) {
        this.stops = new TreeSet<>(stops);
    }

    public Gradient(Stop... stops) {
        this(Arrays.asList(stops));
    }

    public Gradient() {
        this(new Stop(Color.BLACK, 0), new Stop(Color.WHITE, 1));
    }
    
    public void addStop(Stop stop) {
        stops.add(stop);
    }
    
    public TreeSet<Stop> getStops() {
        return stops;
    }
    
    public void setStops(TreeSet<Stop> stops) {
        this.stops = stops;
    }
    
    public Color getColorAt(double pos) {
        if (pos < 0) pos = 0;
        if (pos > 1) pos = 1;
        Stop[] stops = getStopsAround(pos);
        return new Color(
                getIntBetween(pos, stops[0].getColor().getRed(), stops[0].getPosition(), stops[1].getColor().getRed(), stops[1].getPosition()),
                getIntBetween(pos, stops[0].getColor().getGreen(), stops[0].getPosition(), stops[1].getColor().getGreen(), stops[1].getPosition()),
                getIntBetween(pos, stops[0].getColor().getBlue(), stops[0].getPosition(), stops[1].getColor().getBlue(), stops[1].getPosition()));
    }
    
    private int getIntBetween(double pos, int i1, double p1, int i2, double p2) {
        return (int)((pos - p1) / (p2 - p1) * (i2 - i1)) + i1;
    }
    
    private Stop[] getStopsAround(double pos) {
        Iterator<Stop> it = stops.iterator();
        Stop prev = it.next();
        while (it.hasNext()) {
            Stop cur = it.next();
            if (cur.pos >= pos) {
                return new Stop[] {prev, cur};
            }
            prev = cur;
        }
        return null;
    }
    
    public static class Stop implements Comparable<Stop> {
        
        private Color color;
        private final double pos;

        @JsonCreator
        public Stop(@JsonProperty("color") Color color, @JsonProperty("position") double position) {
            this.color = color;
            this.pos = position;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public double getPosition() {
            return pos;
        }

        @Override
        public int compareTo(Stop o) {
            return Double.compare(pos, o.pos);
        }
        
    }
}
