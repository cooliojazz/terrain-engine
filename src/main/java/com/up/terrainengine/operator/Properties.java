package com.up.terrainengine.operator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.up.terrainengine.util.TypeReference;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricky
 */
public class Properties implements Iterable<Map.Entry<String, Object>> {
    
    private static final Logger log = Logger.getLogger(Properties.class.getName());
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build());
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        SimpleModule colorMod = new SimpleModule("color");
        colorMod.addDeserializer(Color.class, new JsonDeserializer<Color>() {
            @Override
            public Color deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
                return new Color(jp.getIntValue());
            }
        });
        colorMod.addSerializer(Color.class, new JsonSerializer<Color>() {
            @Override
            public void serialize(Color c, JsonGenerator jg, SerializerProvider sp) throws IOException {
                jg.writeNumber(c.getRGB());
            }
        });
        mapper.registerModule(colorMod);
    }
    
    private HashMap<String, TypeReference> propTypes = new HashMap<>();
    private HashMap<String, Object> propValues = new HashMap<>();
    
    protected <T> void declareProperty(String name, TypeReference<T> type, T initValue) {
        propTypes.put(name, type);
        propValues.put(name, initValue);
    }
    
    public <T> TypeReference<T> getType(String name) {
        return (TypeReference<T>)propTypes.get(name);
    }
    
    public <T> T get(String name) {
        return (T)propValues.get(name);
    }
    
    public <T> void set(String name, T t) {
        propValues.put(name, t);
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return propValues.entrySet().iterator();
    }
    
    public String getJson() {
        try {
            return mapper.writeValueAsString(propValues);
        } catch (JsonProcessingException ex) {
            log.log(Level.SEVERE, "Error converting properties to JSON");
            return null;
        }
    }
    
    public void setJson(String json) {
        try {
            propValues = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<HashMap<String, Object>>() {});
        } catch (JsonProcessingException ex) {
            log.log(Level.SEVERE, "Error getting properties from JSON");
        }
    }
    
}
