package com.up.terrainengine.operator.operators;

import com.up.terrainengine.structures.PolynomialMap;
import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.terminal.TerminalDefinition;
import com.up.terrainengine.operator.terminal.TerminalMode;
import com.up.terrainengine.structures.PolynomialMap.ControlPoint;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;

/**
 *
 * @author Ricky
 */
public class CurveMap extends Operator {
    
	private static TerminalDefinition<VectorMap<Double>> inTerminal = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.INPUT, "In");
	private static TerminalDefinition<VectorMap<Double>> outTerminal = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.OUTPUT, "Out");

	public CurveMap() {
		super(new GradientMapProperties(), inTerminal, outTerminal);
	}
	
    @Override
    public String getName() {
        return "Curve Map";
    }

    @Override
    public boolean operate() {
        int msize = getTerminal(inTerminal).getState().size();
        VectorMap<Double> out = new VectorMap<>(msize, 3);
        for (int x = 0; x < msize; x++) {
            for (int y = 0; y < msize; y++) {
                out.set(x, y, new Vector<>(this.<GradientMapProperties>getProperties().getMap().getValueAt(getTerminal(inTerminal).getState().get(x, y).get())));
            }
        }
        getTerminal(outTerminal).setState(out);
        return true;
    }
    
    private static class GradientMapProperties extends Properties {

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
