package com.up.terrainengine.operator.operators;

import com.up.terrainengine.util.TypeReference;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Properties;
import com.up.terrainengine.operator.Terminal;
import com.up.terrainengine.operator.Terminal.Mode;
import com.up.terrainengine.operator.Transferable;
import com.up.terrainengine.structures.Vector;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Ricky
 */
public class Provider extends Operator {
    
    private Terminal terminal;
    private TypeReference type;
    private ProviderProperties props;

    @Override
    public String getName() {
        return "Provider";
    }

    public Provider() {
        this(new TypeReference<Vector<Double>>() {});
    }

    public <T extends Transferable> Provider(TypeReference<T> type) {
        terminal = new Terminal<>(type, this, Mode.OUTPUT, "Provided");
        this.type = type;
        props = new ProviderProperties();
        //TODO: Fix
        props.setValue(null);
    }

    @Override
    public List<Terminal> getTerminals() {
        return Collections.singletonList(terminal);
    }

    @Override
    public boolean operate() {
        terminal.setState(props.getValue());
        return true;
    }

    @Override
    public Properties getProperties() {
        return props;
    }

    private class ProviderProperties extends Properties {

        public ProviderProperties() {
            declareProperty("value", type, null);
        }
        
        public Transferable getValue() {
            return get("value");
        }
        
        public void setValue(Transferable t) {
            set("value", t);
        }
    }
    
}
