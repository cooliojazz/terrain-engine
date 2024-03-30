package com.up.terrainengine.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * References a generic type.
 *
 * Originally from crazybob@google.com (Bob Lee)
 */
@JsonSerialize(using = TypeReference.TypeReferenceSerializer.class)
@JsonDeserialize(using = TypeReference.TypeReferenceDeserializer.class)
public abstract class TypeReference<T> {

    protected final String type;

    private TypeReference(String type) {
		this.type = type;
    }

    public TypeReference() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        this.type = ((ParameterizedType)superclass).getActualTypeArguments()[0].getTypeName();
    }
	
    /**
     * Gets the referenced type.
     */
    public String getType() {
        return this.type;
    }

	@Override
	public boolean equals(Object obj) {
		return obj instanceof TypeReference && ((TypeReference)obj).type.equals(type);
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}
	
	
	protected static class JsonTypeReference extends TypeReference<Void> {

		@JsonCreator
		public JsonTypeReference(@JsonProperty String type) {
			super(type);
		}
		
	}
	
	protected static class TypeReferenceSerializer extends JsonSerializer<TypeReference> {

		@Override
		public void serialize(TypeReference t, JsonGenerator gen, SerializerProvider prov) throws IOException {
			gen.writeString(t.getType());
		}
		
		@Override
		public void serializeWithType(TypeReference t, JsonGenerator gen, SerializerProvider prov, TypeSerializer ts) throws IOException {
			WritableTypeId id = ts.typeId(t, JsonToken.START_OBJECT);
			id.id = "com.up.terrainengine.util.TypeReference$JsonTypeReference";
			ts.writeTypePrefix(gen, id);
			gen.writeStringField("type", t.getType());
			ts.writeTypeSuffix(gen, id);
		}
		
	}
	
	protected static class TypeReferenceDeserializer extends JsonDeserializer<TypeReference> {

		@Override
		public TypeReference deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
			return new JsonTypeReference(parser.getValueAsString());
		}
		
	}
}
