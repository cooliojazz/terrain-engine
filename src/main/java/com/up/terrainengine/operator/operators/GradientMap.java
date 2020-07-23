package com.up.terrainengine.operator.operators;

import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.Terminal;
import com.up.terrainengine.operator.Terminal.Mode;
import com.up.terrainengine.structures.Gradient;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ricky
 */
public class GradientMap extends Operator {
    
    private Terminal<VectorMap<Double>>[] terminals = new Terminal[] {
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.INPUT, "In"),
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.OUTPUT, "Out"),
        };
    
    private GradientMapProperties props = new GradientMapProperties();

    @Override
    public String getName() {
        return "Gradient Map";
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
                Vector<Double> v = new Vector<>(3);
                Color c = props.getGradient().getColorAt(terminals[0].getState().get(x, y).get());
                v.set(0, c.getRed() / 255.0);
                v.set(1, c.getGreen() / 255.0);
                v.set(2, c.getBlue() / 255.0);
                out.set(x, y, v);
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
            declareProperty("gradient", new TypeReference<Gradient>() {}, new Gradient());
        }
        
        public Gradient getGradient() {
            return get("gradient");
        }
        
        public void setGradient(Gradient type) {
            set("gradient", type);
        }
    }
    
}
