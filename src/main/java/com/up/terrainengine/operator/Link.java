package com.up.terrainengine.operator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.up.terrainengine.util.TypeReference;

/**
 *
 * @author Ricky
 */
public class Link<T extends Transferable> {
    
    private final Terminal<T> input;
    private final Terminal<T> output;

    public Link(Terminal<T> input, Terminal<T> output) {
        this.input = input;
        this.output = output;
    }

    @JsonIgnore
    public Terminal<T> getInput() {
        return input;
    }

    @JsonIgnore
    public Terminal<T> getOutput() {
        return output;
    }
    
    public TypeReference<T> getType() {
        return input.getType();
    }
    
    public boolean transfer() {
        if ((needsUpdate() && !input.getParent().update()) || !input.getParent().getLastResult()) return false;
        output.setState(input.getState());
        return true;
    }
    
    public boolean needsUpdate() {
        return input.getParent().needsUpdate();
    }
}
