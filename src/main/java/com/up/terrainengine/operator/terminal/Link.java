package com.up.terrainengine.operator.terminal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.up.terrainengine.operator.Transferable;
import com.up.terrainengine.util.TypeReference;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 *
 * @author Ricky
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Link<T extends Transferable> {
	
    private final Terminal<T> input;
    private final Terminal<T> output;

    public Link(@JsonProperty Terminal<T> input, @JsonProperty Terminal<T> output) {
        this.input = input;
        this.output = output;
    }

    public Terminal<T> getInput() {
        return input;
    }

    public Terminal<T> getOutput() {
        return output;
    }
    
	@JsonIgnore
    public TypeReference<T> getType() {
        return input.getDefinition().getType();
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
