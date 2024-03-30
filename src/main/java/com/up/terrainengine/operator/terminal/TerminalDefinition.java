package com.up.terrainengine.operator.terminal;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.up.terrainengine.operator.Transferable;
import com.up.terrainengine.util.TypeReference;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.util.Objects;

/**
 *
 * @author Ricky
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public final class TerminalDefinition<T extends Transferable> {
    
    private final TypeReference<T> type;
    private final TerminalMode mode;
    private final String description;
	
    public TerminalDefinition(TypeReference<T> type, TerminalMode mode, String description) {
        this.type = type;
        this.mode = mode;
        this.description = description;
    }

    public TypeReference<T> getType() {
        return type;
    }

    public TerminalMode getMode() {
        return mode;
    }

    public String getDescription() {
        return description;
    }

	@Override
	public boolean equals(Object obj) {
		return obj.hashCode() == hashCode();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.type);
		hash = 97 * hash + Objects.hashCode(this.mode.name());
		hash = 97 * hash + Objects.hashCode(this.description);
		return hash;
	}
	
}
