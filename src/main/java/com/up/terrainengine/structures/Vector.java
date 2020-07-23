package com.up.terrainengine.structures;

/**
 *
 * @author Ricky
 */
public class Vector<T> {
    
    private Object[] vec;

    public Vector(T... vec) {
        this.vec = vec;
    }

    public Vector(int size) {
        this.vec = new Object[size];
    }
    
    public T get(int i) {
        return (T)vec[i];
    }
    
    public void set(int i, T t) {
        vec[i] = t;
    }
    
    public T get() {
        return (T)vec[0];
    }
    
    public void set(T t) {
        vec[0] = t;
    }
    
    public T getR() {
        return (T)vec[0];
    }
    
    public T getG() {
        return (T)vec[1];
    }
    
    public T getB() {
        return (T)vec[2];
    }
    
    public int size() {
        return vec.length;
    }

    @Override
    public String toString() {
        String vs = "<";
        for (int i = 0; i < vec.length; i++) {
            vs += vec[i] + (i < vec.length - 1 ? ", " : "");
        }
        return vs + ">";
    }
    
}
