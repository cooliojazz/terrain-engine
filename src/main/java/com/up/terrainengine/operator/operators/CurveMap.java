package com.up.terrainengine.operator.operators;

import com.up.terrainengine.structures.PolynomialMap;
import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.Terminal;
import com.up.terrainengine.operator.Terminal.Mode;
import com.up.terrainengine.structures.PolynomialMap.ControlPoint;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ricky
 */
public class CurveMap extends Operator {
    
    private Terminal<VectorMap<Double>>[] terminals = new Terminal[] {
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.INPUT, "In"),
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.OUTPUT, "Out"),
        };
    
    private GradientMapProperties props = new GradientMapProperties();

    @Override
    public String getName() {
        return "Curve Map";
    }

    @Override
    public List<Terminal> getTerminals() {
        return Arrays.asList(terminals);
    }

    @Override
    public boolean operate() {
        int msize = terminals[0].getState().size();
        VectorMap<Double> out = new VectorMap<>(msize, 3);
        for (int x = 0; x < msize; x++) {
            for (int y = 0; y < msize; y++) {
                out.set(x, y, new Vector<>(props.getMap().getValueAt(terminals[0].getState().get(x, y).get())));
            }
        }
        terminals[1].setState(out);
        return true;
    }

    @Override
    public Properties getProperties() {
        return props;
    }
    
    private class GradientMapProperties extends Properties {

        public GradientMapProperties() {
            declareProperty("map", new TypeReference<PolynomialMap>() {}, new PolynomialMap(new ControlPoint(0, 0), new ControlPoint(0.25, 0.4), new ControlPoint(0.75, 0.6), new ControlPoint(1, 1)));
        }
        
        public PolynomialMap getMap() {
            return get("map");
        }
        
        public void setGradient(PolynomialMap map) {
            set("map", map);
        }
    }
    
}
