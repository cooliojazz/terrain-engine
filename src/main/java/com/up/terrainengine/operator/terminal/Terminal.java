package com.up.terrainengine.operator.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.up.terrainengine.operator.Operator;
import com.up.terrainengine.operator.Transferable;
import com.up.terrainengine.util.TypeReference;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 *
 * @author Ricky
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public final class Terminal<T extends Transferable> {
    
//    private final TypeReference<T> type;
    private Operator parent;
    private TerminalDefinition<T> definition;
//    private final Mode mode;
//    private final String description;
	@JsonIgnore
    private T state = null;
    private Link<T> link = null;

//    protected Terminal() {
//		type = null;
//		mode = Mode.INPUT;
//		description = "";
//	}
	
    public Terminal(TerminalDefinition<T> definition, Operator parent) {
        this.definition = definition;
        this.parent = parent;
    }
	
//    public Terminal(TypeReference<T> type, Operator parent, Mode mode, String description) {
//        this.type = type;
//        this.parent = parent;
//        this.mode = mode;
//        this.description = description;
//    }

//    public TypeReference<T> getType() {
//        return type;
//    }

    public TerminalDefinition<T> getDefinition() {
        return definition;
    }

	public Operator getParent() {
		return parent;
	}

//    public Mode getMode() {
//        return mode;
//    }
//
//    public String getDescription() {
//        return description;
//    }
    
    public T getState() {
        return state;
    }
    
    public void setState(T state) {
        this.state = state;
    }

    public Link<T> getLink() {
        return link;
    }

    public void setLink(Link<T> link) {
        this.link = link;
    }
    
    public void linkTo(Terminal<T> t) {
        Link<T> l = new Link<>(this, t);
        setLink(l);
        t.setLink(l);
        t.getParent().changed();
    }
    
    public void unlink() {
        if (link != null) {
            //Don't think this is working right
            getLink().getOutput().getParent().changed();
            //Clear the opposing link
//            if (getMode() == Mode.INPUT) getLink().getInput().setLink(null);
//            if (getMode() == Mode.OUTPUT) getLink().getOutput().setLink(null);
            if (getDefinition().getMode() == TerminalMode.INPUT) getLink().getInput().setLink(null);
            if (getDefinition().getMode() == TerminalMode.OUTPUT) getLink().getOutput().setLink(null);
            setLink(null);
        }
    }
    
//    public static enum Mode {INPUT, OUTPUT}
}
