package com.up.terrainengine.operator;

import com.up.terrainengine.operator.terminal.TerminalDefinition;
import com.up.terrainengine.operator.terminal.Terminal;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.up.terrainengine.operator.terminal.TerminalMode;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Ricky
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public abstract class Operator {
    
    protected boolean needsUpdate = true;
    private boolean lastResult = false;
    private List<TerminalDefinition> terminalDefs;
	@JsonSerialize(keyUsing = TerminalsKeySerializer.class)
	@JsonDeserialize(keyUsing = TerminalsKeyDeserializer.class)
    private HashMap<TerminalDefinition, Terminal> terminals;
	@JsonProperty
    private Properties properties;

	/**
	 * Creates operator with new set of terminals from definitions.
	 */
	protected Operator(Properties properties, TerminalDefinition... definitions) {
		terminalDefs = Arrays.asList(definitions);
		terminals = new HashMap<>(Stream.of(definitions).collect(Collectors.toMap(d -> d, d -> new Terminal(d, this))));
		this.properties = properties;
	}
    
	@JsonIgnore
    public abstract String getName();
	
    public abstract boolean operate();
    
	
	@JsonIgnore
    public List<TerminalDefinition> getTerminalDefinitions() {
		return terminalDefs;
	}
	
	@JsonIgnore
    public List<Terminal> getTerminals() {
		return new ArrayList<>(terminals.values());
	}
	
	@JsonIgnore
    protected <T extends Transferable> Terminal<T> getTerminal(TerminalDefinition<T> def) {
		return terminals.get(def);
	}
	
    @JsonIgnore
    public List<Terminal> getInputs() {
        return terminals.keySet().stream().filter(t -> t.getMode() == TerminalMode.INPUT).map(d -> terminals.get(d)).collect(Collectors.toList());
    }
    
    @JsonIgnore
    public List<Terminal> getOutputs() {
        return terminals.keySet().stream().filter(t -> t.getMode() == TerminalMode.OUTPUT).map(d -> terminals.get(d)).collect(Collectors.toList());
    }

	@JsonIgnore
    public <T extends Properties> T getProperties() {
		return (T)properties;
	}
	
	@JsonIgnore
    public boolean getLastResult() {
        return lastResult;
    }
    
    public boolean update() {
        needsUpdate = false;
        lastResult = true;
        for (Terminal t : terminals.values()) {
            if (t.getDefinition().getMode() == TerminalMode.INPUT) {
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
        for (Terminal t : getInputs()) {
			if (t.getLink() != null) {
				if (t.getLink().needsUpdate()) return true;
			}
		}
        return needsUpdate;
    }
    
    public void changed() {
        needsUpdate = true;
        lastResult = false;
    }
	
	protected static class TerminalsKeySerializer extends JsonSerializer<TerminalDefinition> {

		@Override
		public void serialize(TerminalDefinition t, JsonGenerator jg, SerializerProvider sp) throws IOException {
			jg.writeFieldName("" + t.hashCode());
		}
		
	}
	
	protected static class TerminalsKeyDeserializer extends KeyDeserializer {

		@Override
		public TerminalDefinition deserializeKey(String key, DeserializationContext dc) throws IOException {
			int hash = Integer.parseInt(key);
			Operator o = (Operator)dc.getParser().getParsingContext().getParent().getCurrentValue();
			return o.terminalDefs.stream().filter(d -> d.hashCode() == hash).findAny().orElseThrow(() -> new RuntimeException("Could not find TerminalDefinition key"));
		}
		
	}
    
}
