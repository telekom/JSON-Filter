package de.telekom.jsonfilter;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ValidatorTest {

    @Test
    void invalidMapShouldNotBeValid() {
        Map<String, Object> input = Map.of("foo", "bar");
        var result = Validator.isValidFilterOperator(input);

        assertFalse(result.isValid());
    }
}