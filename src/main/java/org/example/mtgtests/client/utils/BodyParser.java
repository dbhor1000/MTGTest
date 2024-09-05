package org.example.mtgtests.client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Function;

@Component
public class BodyParser {

    private final ObjectMapper mapper;

    public BodyParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T parse(Class<T> clazz, String body) {
        return parse(clazz, body, (thrown) -> new BodyParsingException(thrown));
    }

    public <T, R extends RuntimeException>
    T parse(Class<T> clazz, String body, Function<IOException, R> handler) {
        try {
            return mapper.readValue(body, clazz);
        } catch (IOException exception) {
            throw handler.apply(exception);
        }
    }
}
