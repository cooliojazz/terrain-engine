package com.up.terrainengine.operator.operators;

import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.terminal.TerminalDefinition;
import com.up.terrainengine.operator.terminal.TerminalMode;
import com.up.terrainengine.structures.Vector;
import com.up.terrainengine.structures.VectorMap;

/**
 *
 * @author Ricky
 */
public class Combiner extends Operator {
    
	private static TerminalDefinition<VectorMap<Double>> aTerminal = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.INPUT, "A");
	private static TerminalDefinition<VectorMap<Double>> bTerminal = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.INPUT, "B");
	private static TerminalDefinition<VectorMap<Double>> outTerminal = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.OUTPUT, "Out");

    public Combiner() {
		super(new CombinerProperties(), aTerminal, bTerminal, outTerminal);
    }

	@Override
	public String getName() {
		return "Combiner";
	}

    @Override
    public boolean operate() {
        int msize = getTerminal(aTerminal).getState().size();
        int vsize = getTerminal(aTerminal).getState().get(0, 0).size();
        VectorMap<Double> out = new VectorMap<>(msize, vsize);
        for (int x = 0; x < msize; x++) {
            for (int y = 0; y < msize; y++) {
                Vector<Double> v = new Vector<>(vsize);
                for (int i = 0; i < vsize; i++) {
                    switch (this.<CombinerProperties>getProperties().getType()) {
                        case ADD: {
                            v.set(i, Math.max(Math.min(getTerminal(aTerminal).getState().get(x, y).get(i) + getTerminal(bTerminal).getState().get(x, y).get(i), 1), 0));
                            break;
                        }
//                        case ALPHA_COMPOSITE: {
//							if () {
//								if (i < 3) {
//									v.set(i, Math.max(Math.min(terminals[0].getState().get(x, y).get(i) * Math.min(terminals[0].getState().get(x, y).get(4) - terminals[1].getState().get(x, y).get(i), 1), 0));
//								} else {
						//Need to do this first for the formula, grrr
//									v.set(i, Math.max(Math.min(terminals[0].getState().get(x, y).get(i) + terminals[1].getState().get(x, y).get(i) * (1 - terminals[0].getState().get(x, y).get(i)), 1), 0));
//								}
//								System.out.println("Not enough components for this operation");
//								break;
//							}
//                        }
                        case AVERAGE: {
                            v.set(i, Math.max(Math.min((getTerminal(aTerminal).getState().get(x, y).get(i) + getTerminal(bTerminal).getState().get(x, y).get(i)) / 2, 1), 0));
                            break;
                        }
                        case MULTIPLY: {
                            v.set(i, Math.max(Math.min(getTerminal(aTerminal).getState().get(x, y).get(i) * getTerminal(bTerminal).getState().get(x, y).get(i), 1), 0));
                            break;
                        }
                        case SUBTRACT: {
                            v.set(i, Math.max(Math.min(getTerminal(aTerminal).getState().get(x, y).get(i) - getTerminal(bTerminal).getState().get(x, y).get(i), 1), 0));
                            break;
                        }
                    }
                    
                }
                out.set(x, y, v);
            }
        }
        getTerminal(outTerminal).setState(out);
        return true;
    }
    
    public static enum Type {ADD, AVERAGE, MULTIPLY, SUBTRACT, ALPHA_COMPOSITE}
    
    private static class CombinerProperties extends Properties {

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
