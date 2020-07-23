package com.up.terrainengine.operator.operators;

import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.Terminal;
import com.up.terrainengine.operator.Terminal.Mode;
import com.up.terrainengine.structures.VectorMap;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ricky
 */
public class Printer extends Operator {
    
    private Terminal<VectorMap>[] terminals = new Terminal[] {
            new Terminal<>(new TypeReference<VectorMap>() {}, this, Mode.INPUT, "Printable")
        };

    @Override
    public String getName() {
        return "Logger";
    }

    @Override
    public List<Terminal> getTerminals() {
        return Arrays.asList(terminals);
    }

    @Override
    public boolean operate() {
        System.out.println(terminals[0].getState().toString());
        return true;
    }

    @Override
    public Properties getProperties() {
        return new Properties();
    }
    
}
