package io.spring.api.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ErrorResourceSerializer extends JsonSerializer<ErrorResource> {
    Logger logger = LoggerFactory.getLogger(ErrorResourceSerializer.class);
    @Override
    public void serialize(ErrorResource value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            Map<String, List<String>> json = new HashMap<>();
            gen.writeStartObject();
            gen.writeObjectFieldStart("errors");
            for (FieldErrorResource fieldErrorResource : value.getFieldErrors()) {
                if (!json.containsKey(fieldErrorResource.getField())) {
                    json.put(fieldErrorResource.getField(), new ArrayList<String>());
                }
                json.get(fieldErrorResource.getField()).add(fieldErrorResource.getMessage());
            }
            for (Map.Entry<String, List<String>> pair : json.entrySet()) {
                gen.writeArrayFieldStart(pair.getKey());
                pair.getValue().forEach(content -> {
                    try {
                        gen.writeString(content);
                    } catch (IOException e) {
                        logger.error("context", e);
                    }
                });
                gen.writeEndArray();
            }
            gen.writeEndObject();
            gen.writeEndObject();
        }catch (JsonProcessingException j1){
            logger.error("context",j1);
        }
    }
}
