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
public class Combiner extends Operator {
    
    private Terminal<VectorMap<Double>>[] terminals = new Terminal[] {
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.INPUT, "A"),
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.INPUT, "B"),
            new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.OUTPUT, "Out"),
        };
    
    private CombinerProperties props = new CombinerProperties();

    @Override
    public String getName() {
        return "Combiner";
    }

    @Override
    public List<Terminal> getTerminals() {
        return Arrays.asList(terminals);
    }

    @Override
    public boolean operate() {
        int msize = terminals[0].getState().size();
        int vsize = terminals[0].getState().get(0, 0).size();
        VectorMap<Double> out = new VectorMap<>(msize, vsize);
        for (int x = 0; x < msize; x++) {
            for (int y = 0; y < msize; y++) {
                Vector<Double> v = new Vector<>(vsize);
                for (int i = 0; i < vsize; i++) {
                    switch (props.getType()) {
                        case ADD: {
                            v.set(i, Math.max(Math.min(terminals[0].getState().get(x, y).get(i) + terminals[1].getState().get(x, y).get(i), 1), 0));
                            break;
                        }
                        case AVERAGE: {
                            v.set(i, Math.max(Math.min((terminals[0].getState().get(x, y).get(i) + terminals[1].getState().get(x, y).get(i)) / 2, 1), 0));
                            break;
                        }
                        case MULTIPLY: {
                            v.set(i, Math.max(Math.min(terminals[0].getState().get(x, y).get(i) * terminals[1].getState().get(x, y).get(i), 1), 0));
                            break;
                        }
                        case SUBTRACT: {
                            v.set(i, Math.max(Math.min(terminals[0].getState().get(x, y).get(i) - terminals[1].getState().get(x, y).get(i), 1), 0));
                            break;
                        }
                    }
                    
                }
                out.set(x, y, v);
            }
        }
        terminals[2].setState(out);
        return true;
    }

    @Override
    public Properties getProperties() {
        return props;
    }
    
    public static enum Type {ADD, AVERAGE, MULTIPLY, SUBTRACT}
    
    private class CombinerProperties extends Properties {

        public CombinerProperties() {
            declareProperty("type", new TypeReference<Type>() {}, Type.ADD);
        }
        
        public Type getType() {
            return get("type");
        }
        
        public void setType(Type type) {
            set("type", type);
        }
    }
    
}
