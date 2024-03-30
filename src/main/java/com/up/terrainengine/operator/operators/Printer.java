package com.up.terrainengine.operator.operators;

import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.terminal.TerminalDefinition;
import com.up.terrainengine.operator.terminal.TerminalMode;
import com.up.terrainengine.structures.VectorMap;

/**
 *
 * @author Ricky
 */
public class Printer extends Operator {
    
    private static TerminalDefinition<VectorMap<Double>> input = new TerminalDefinition<>(new TypeReference<VectorMap<Double>>() {}, TerminalMode.INPUT, "Printable");

    public Printer() {
		super(new Properties(), input);
	}

    @Override
    public String getName() {
        return "Logger";
    }

    @Override
    public boolean operate() {
        System.out.println(getTerminal(input).getState().toString());
        return true;
    }
    
}
