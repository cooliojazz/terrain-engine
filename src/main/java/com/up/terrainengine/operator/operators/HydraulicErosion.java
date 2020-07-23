package com.up.terrainengine.operator.operators;

import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.Terminal;
import com.up.terrainengine.operator.Terminal.Mode;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ricky
 */
public class HydraulicErosion extends Operator {
    
    private Terminal<VectorMap<Double>>[] terminals = new Terminal[] {
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.INPUT, "In"),
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.OUTPUT, "Height Map"),
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.OUTPUT, "Water Map"),
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.OUTPUT, "Sediment Map"),
        };
    
    private HydraulicErosionProperties props = new HydraulicErosionProperties();

    @Override
    public String getName() {
        return "Hydraulic Erosion";
    }

    @Override
    public List<Terminal> getTerminals() {
        return Arrays.asList(terminals);
    }

    @Override
    public boolean operate() {
        int msize = terminals[0].getState().size();
        double[][] heightmap = new double[msize][msize];
        double[][] watermap = new double[msize][msize];
        double[][] sedimentmap = new double[msize][msize];
        for (int x = 0; x < msize; x++) {
            for (int y = 0; y < msize; y++) {
                heightmap[x][y] = terminals[0].getState().get(x, y).get();
                watermap[x][y] = 0.001;
            }
        }
        
        for (int i = 0; i < props.getGenerations(); i++) {
            double[][][] maps = runGeneration(heightmap, watermap, sedimentmap);
            heightmap = maps[0];
            watermap = maps[1];
            sedimentmap = maps[2];
        }
        
        VectorMap<Double> heightOut = new VectorMap<>(msize, 3);
        VectorMap<Double> waterOut = new VectorMap<>(msize, 3);
        VectorMap<Double> sedimentOut = new VectorMap<>(msize, 3);
        for (int x = 0; x < msize; x++) {
            for (int y = 0; y < msize; y++) {
                heightOut.set(x, y, new Vector<>(heightmap[x][y]));
                waterOut.set(x, y, new Vector<>(Math.max(Math.min(watermap[x][y], 1), 0)));
                sedimentOut.set(x, y, new Vector<>(Math.max(Math.min(sedimentmap[x][y], 1), 0)));
            }
        }
        terminals[1].setState(heightOut);
        terminals[2].setState(waterOut);
        terminals[3].setState(sedimentOut);
        return true;
    }
    
    private double[][][] runGeneration(double[][] heightmap, double[][] watermap, double[][] sedimentmap) {
        int size = heightmap.length;
        double[][] heightmapOut = new double[size][size];
        double[][] watermapOut = new double[size][size];
        double[][] sedimentmapOut = new double[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double height = heightmap[x][y];
                double water = watermap[x][y] + watermapOut[x][y];
                double sediment = sedimentmap[x][y] + sedimentmapOut[x][y];
                
                //Rain
                water = Math.min(1, water + 0.0001);
                
                //Gather sediment
                if (sediment < water / 2) {
                    double amount = Math.min(water / 2 - sediment, Math.min(height, water / 10));
                    height -= amount;
                    sediment += amount;
                }
                
                //Deposit excess
                if (sediment > water / 2) {
                    double amount = Math.min(sediment - water / 2, 1 - height);
                    height += amount;
                    sediment -= amount;
                }
                
                //Do flow
                double waterPerTile = water / 8;
                double sedimentPerTile = Math.min(sediment / 8, waterPerTile / 2);
                for (int[] tile : getAdjacentTiles(x, y)) {
                    if (tile[0] >= 0 && tile[1] >= 0 && tile[0] < size && tile[1] < size) {
                        double slope = (height + water) - (heightmap[tile[0]][tile[1]] + watermap[tile[0]][tile[1]]);
                        if (slope > 0) {
                            double actualWaterMoved = Math.min(slope * waterPerTile, 1 - watermapOut[tile[0]][tile[1]]);
                            double actualSedimentMoved = Math.min(slope * sedimentPerTile, 1 - sedimentmapOut[tile[0]][tile[1]]);
                            watermapOut[tile[0]][tile[1]] += actualWaterMoved;
                            water -= actualWaterMoved;
                            sedimentmapOut[tile[0]][tile[1]] += actualSedimentMoved;
                            sediment -= actualSedimentMoved;
                        } else {
                            
                        }
                    }
                }
                
                //Update terrain
                heightmapOut[x][y] = height;
//                watermapOut[x][y] = water + watermapOut[x][y];
//                sedimentmapOut[x][y] = sediment + sedimentmapOut[x][y];
                watermapOut[x][y] = water;
                sedimentmapOut[x][y] = sediment;
            }
        }
        return new double[][][] {heightmapOut, watermapOut, sedimentmapOut};
    }
    
    private int[][] getAdjacentTiles(int x, int y) {
        return new int[][] {new int[] {x - 1, y}, new int[] {x - 1, y - 1}, new int[] {x, y - 1}, new int[] {x + 1, y - 1}, new int[] {x + 1, y}, new int[] {x + 1, y + 1}, new int[] {x, y + 1}, new int[] {x - 1, y + 1}, };
    }

    @Override
    public Properties getProperties() {
        return props;
    }
    
    private class HydraulicErosionProperties extends Properties {

        public HydraulicErosionProperties() {
            declareProperty("generations", new TypeReference<Integer>() {}, 10);
        }
        
        public int getGenerations() {
            return get("generations");
        }
        
        public void setGenerations(int gens) {
            set("generations", gens);
        }
    }
    
}
