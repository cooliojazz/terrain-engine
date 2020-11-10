package com.up.terrainengine.operator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.up.terrainengine.operator.Terminal.Mode;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Ricky
 */
public abstract class Operator {
    
    protected boolean needsUpdate = true;
    private boolean lastResult = false;
    
    public abstract String getName();
    public abstract List<Terminal> getTerminals();
    public abstract Properties getProperties();
    public abstract boolean operate();
//    public abstract PropertiesEditor getEditor();
    
    @JsonIgnore
    public List<Terminal> getInputs() {
        return getTerminals().stream().filter(t -> t.getMode() == Mode.INPUT).collect(Collectors.toList());
    }
    
    @JsonIgnore
    public List<Terminal> getOutputs() {
        return getTerminals().stream().filter(t -> t.getMode() == Mode.OUTPUT).collect(Collectors.toList());
    }

    public boolean getLastResult() {
        return lastResult;
    }
    
    public boolean update() {
        needsUpdate = false;
        lastResult = true;
        for (Terminal t : getTerminals()) {
            if (t.getMode() == Mode.INPUT) {
                if (t.getLink() != null) {
                    if (!t.getLink().transfer()) lastResult = false;
                } else {
                    lastResult = false;
                }
            }
        }
        if (lastResult) lastResult = operate();
        return lastResult;
    }
    
    public boolean needsUpdate() {
        for (Terminal t : getTerminals()) {
            if (t.getMode() == Mode.INPUT) {
                if (t.getLink() != null) {
                    if (t.getLink().needsUpdate()) return true;
                }
            }
        }
        return needsUpdate;
    }
    
    public void changed() {
        needsUpdate = true;
        lastResult = false;
    }
    
}
