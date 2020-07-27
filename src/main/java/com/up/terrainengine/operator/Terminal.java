package com.up.terrainengine.operator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.up.terrainengine.util.TypeReference;

/**
 *
 * @author Ricky
 */
public class Terminal<T extends Transferable> {
    
    private final TypeReference<T> typeClass;
    private Operator parent;
    private final Mode mode;
    private final String description;
    private T state = null;
    private Link<T> link = null;

    public Terminal(TypeReference<T> typeClass, Operator parent, Mode mode, String description) {
        this.typeClass = typeClass;
        this.parent = parent;
        this.mode = mode;
        this.description = description;
    }

    public TypeReference<T> getType() {
        return typeClass;
    }

    @JsonIgnore
    public Operator getParent() {
        return parent;
    }

    public Mode getMode() {
        return mode;
    }

    public String getDescription() {
        return description;
    }
    
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
            if (getMode() == Mode.INPUT) getLink().getInput().setLink(null);
            if (getMode() == Mode.OUTPUT) getLink().getOutput().setLink(null);
            setLink(null);
        }
    }
    
    public static enum Mode {INPUT, OUTPUT}
}
