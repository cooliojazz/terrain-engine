package com.up.terrainengine.operator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.up.terrainengine.util.TypeReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 *
 * @author Ricky
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Properties implements Iterable<Map.Entry<String, Object>> {
    
	@JsonProperty
    private HashMap<String, TypeReference> propTypes = new HashMap<>();
	@JsonProperty
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private HashMap<String, Object> propValues = new HashMap<>();
    private HashMap<String, Supplier<Boolean>> propConditions = new HashMap<>();
    
    protected <T> void declareProperty(String name, TypeReference<T> type, T initValue) {
        declareProperty(name, type, initValue, () -> true);
    }
    
    protected <T> void declareProperty(String name, TypeReference<T> type, T initValue, Supplier<Boolean> condition) {
        propTypes.put(name, type);
        propValues.put(name, initValue);
        propConditions.put(name, condition);
    }
    
    public <T> TypeReference<T> getType(String name) {
        return (TypeReference<T>)propTypes.get(name);
    }
    
    public boolean enabled(String name) {
        return propConditions.get(name).get();
    }
    
	@JsonIgnore
    public <T> T get(String name) {
        return (T)propValues.get(name);
    }
    
	@JsonIgnore
    public <T> void set(String name, T t) {
        propValues.put(name, t);
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return propValues.entrySet().iterator();
    }
	
}
