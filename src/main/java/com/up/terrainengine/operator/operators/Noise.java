package com.up.terrainengine.operator.operators;

import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.Terminal;
import com.up.terrainengine.operator.Terminal.Mode;
import com.up.terrainengine.operator.Transferable;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;
import com.up.terrainengine.util.SimplexNoise;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Ricky
 */
public class Noise extends Operator {
    
    private Terminal terminal = new Terminal<>(new TypeReference<VectorMap<Double>>() {}, this, Mode.OUTPUT, "Noise");
    private NoiseProperties props = new NoiseProperties();

    @Override
    public String getName() {
        return "Noise";
    }

    @Override
    public List<Terminal> getTerminals() {
        return Collections.singletonList(terminal);
    }

    @Override
    public boolean operate() {
        VectorMap<Double> out = new VectorMap<>(props.getSize(), 1);
        SimplexNoise noise = new SimplexNoise(props.getOctaves(), props.getOctaveScale());
        double scale = props.getScale();
        for (int x = 0; x < props.getSize(); x++) {
            for (int y = 0; y < props.getSize(); y++) {
                out.set(x, y, new Vector<>((noise.simplexNoise(x * scale , y * scale) + 1) / 2));
            }
        }
        terminal.setState(out);
        return true;
    }

    @Override
    public Properties getProperties() {
        return props;
    }

    private class NoiseProperties extends Properties {

        public NoiseProperties() {
            declareProperty("size", new TypeReference<Integer>() {}, 128);
            declareProperty("octaves", new TypeReference<Integer>() {}, 4);
            declareProperty("octaveScale", new TypeReference<Double>() {}, 0.5);
            declareProperty("scale", new TypeReference<Double>() {}, 0.05);
        }
        
        public Integer getSize() {
            return get("size");
        }
        
        public void setValue(int s) {
            set("size", s);
        }
        
        public Integer getOctaves() {
            return get("octaves");
        }
        
        public void setOctaves(int o) {
            set("octaves", o);
        }
        
        public Double getOctaveScale() {
            return get("octaveScale");
        }
        
        public void setOctaveScale(double s) {
            set("octaveScale", s);
        }
        
        public Double getScale() {
            return get("scale");
        }
        
        public void setScale(double s) {
            set("scale", s);
        }
    }
    
}
