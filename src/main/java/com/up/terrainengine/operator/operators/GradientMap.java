package com.up.terrainengine.operator.operators;

import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.terminal.TerminalDefinition;
import com.up.terrainengine.operator.terminal.TerminalMode;
import com.up.terrainengine.structures.Gradient;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;
import java.awt.Color;

/**
 *
 * @author Ricky
 */
public class GradientMap extends Operator {
	
	private static TerminalDefinition<VectorMap<Double>> inTerminal = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.INPUT, "In");
	private static TerminalDefinition<VectorMap<Double>> outTerminal = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.OUTPUT, "Out");

	public GradientMap() {
		super(new GradientMapProperties(), inTerminal, outTerminal);
	}
	
    @Override
    public String getName() {
        return "Gradient Map";
    }

    @Override
    public boolean operate() {
        int msize = getTerminal(inTerminal).getState().size();
        VectorMap<Double> out = new VectorMap<>(msize, 3);
        for (int x = 0; x < msize; x++) {
            for (int y = 0; y < msize; y++) {
                Vector<Double> v = new Vector<>(3);
                Color c = this.<GradientMapProperties>getProperties().getGradient().getColorAt(getTerminal(inTerminal).getState().get(x, y).get());
                v.set(0, c.getRed() / 255.0);
                v.set(1, c.getGreen() / 255.0);
                v.set(2, c.getBlue() / 255.0);
                out.set(x, y, v);
            }
        }
        getTerminal(outTerminal).setState(out);
        return true;
    }
    
    private static class GradientMapProperties extends Properties {

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
