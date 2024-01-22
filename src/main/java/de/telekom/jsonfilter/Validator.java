package de.telekom.jsonfilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.telekom.jsonfilter.operator.Operator;
import de.telekom.jsonfilter.operator.ValidationResult;
import de.telekom.jsonfilter.serde.OperatorDeserializer;
import de.telekom.jsonfilter.serde.OperatorSerializer;

import java.util.Map;

public class Validator {

    private static final ObjectMapper om = initObjectMapper();

    private static ObjectMapper initObjectMapper() {
        var opModule = new SimpleModule();
        opModule.addSerializer(Operator.class, new OperatorSerializer());
        opModule.addDeserializer(Operator.class, new OperatorDeserializer());

        var om = new ObjectMapper(new YAMLFactory());
        om.registerModule(opModule);

        return om;
    }

    public static ValidationResult isValidFilterOperator(Map<String, Object> input) {
        try {
            om.readValue(om.writeValueAsString(input), Operator.class);
            return ValidationResult.valid();
        } catch (JsonProcessingException e) {
            return ValidationResult.withError(e.getMessage());
        }
    }
}
