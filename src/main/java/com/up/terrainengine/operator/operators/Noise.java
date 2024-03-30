package com.up.terrainengine.operator.operators;

import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.terminal.TerminalDefinition;
import com.up.terrainengine.operator.terminal.TerminalMode;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;
import com.up.terrainengine.util.SimplexNoise;

/**
 *
 * @author Ricky
 */
public class Noise extends Operator {
    
    private static TerminalDefinition output = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.OUTPUT, "Noise");

	public Noise() {
		super(new NoiseProperties(), output);
	}

    @Override
    public String getName() {
        return "Noise";
    }

    @Override
    public boolean operate() {
		NoiseProperties props = this.<NoiseProperties>getProperties();
		VectorMap<Double> out = new VectorMap<>(props.getSize(), 1);
		if (props.getType() == Type.SIMPLEX) {
			SimplexNoise noise = new SimplexNoise(props.getOctaves(), props.getOctaveScale());
			double scale = props.getScale();
			for (int x = 0; x < props.getSize(); x++) {
				for (int y = 0; y < props.getSize(); y++) {
					out.set(x, y, new Vector<>((noise.simplexNoise(x * scale , y * scale) + 1) / 2));
				}
			}
		}
		if (props.getType() == Type.WHITE) {
			for (int x = 0; x < props.getSize(); x++) {
				for (int y = 0; y < props.getSize(); y++) {
					// Add scaling?
					out.set(x, y, new Vector<>(Math.random()));
				}
			}
		}
		getTerminal(output).setState(out);
		return true;
    }

	private enum Type {SIMPLEX, WHITE};
	
    private static class NoiseProperties extends Properties {

        public NoiseProperties() {
            declareProperty("type", new TypeReference<Type>() {}, Type.SIMPLEX);
            declareProperty("size", new TypeReference<Integer>() {}, 128);
			
            declareProperty("octaves", new TypeReference<Integer>() {}, 4, () -> getType() == Type.SIMPLEX);
            declareProperty("octaveScale", new TypeReference<Double>() {}, 0.5, () -> getType() == Type.SIMPLEX);
            declareProperty("scale", new TypeReference<Double>() {}, 0.05);
        }
        
        public Type getType() {
            return get("type");
        }
        
        public void setType(Type t) {
			set("type", t);
        }
        
        public Integer getSize() {
            return get("size");
        }
        
        public void setSize(int s) {
            set("size", s);
        }
		
		// Or alternatively, allow conditional props? Is that easily possible?
        
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
