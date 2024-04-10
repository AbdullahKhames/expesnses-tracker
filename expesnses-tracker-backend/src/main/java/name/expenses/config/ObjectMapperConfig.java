package name.expenses.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ObjectMapperConfig {

    public static ObjectMapper getObjectMapper(){
        ObjectMapper mapper = JsonMapper.builder()
                .build();
        // Create a custom DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Create a custom serializer with the desired format
        LocalDateTimeSerializer serializer = new LocalDateTimeSerializer(formatter);

        // Create a custom deserializer with the desired format
        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer(formatter);

        // Register the custom serializer and deserializer
        mapper.registerModule(new JavaTimeModule()
                .addSerializer(LocalDateTime.class, serializer)
                .addDeserializer(LocalDateTime.class, deserializer));
        return mapper;
    }
}
