package com.up.terrainengine.structures;

import com.up.terrainengine.operator.Transferable;

/**
 *
 * @author Ricky
 */
public class VectorMap<T> implements Transferable {
    
    Vector<T>[][] map;

    public VectorMap(int mapSize, int vecSize) {
        this.map = new Vector[mapSize][mapSize];
    }
    
    public Vector<T> get(int x, int y) {
        return map[x][y];
    }
    
    public void set(int x, int y, Vector<T> t) {
        map[x][y] = t;
    }
    
    public int size() {
        return map.length;
    }

    @Override
    public String toString() {
        String vms = "{\n";
        for (int x = 0; x < map.length; x++) {
            String line = "  {";
            for (int y = 0; y < map.length; y++) {
                Vector v = map[x][y];
                line += v.toString() + (y < map.length - 1 ? ", " : "");
            }
            vms += line + "}" + (x < map.length - 1 ? "," : "") + "\n";
        }
        return vms + "}";
    }
    
    
}
