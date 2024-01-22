package de.telekom.jsonfilter.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.telekom.jsonfilter.operator.comparison.NotContainsOperator;
import de.telekom.jsonfilter.operator.logic.AndOperator;
import de.telekom.jsonfilter.serde.OperatorDeserializer;
import de.telekom.jsonfilter.serde.OperatorSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class OperatorTest {

    static ObjectMapper om;

    @BeforeAll
    static void initTest() {
        SimpleModule m = new SimpleModule();
        m.addDeserializer(Operator.class, new OperatorDeserializer());
        m.addSerializer(Operator.class, new OperatorSerializer());

        om = new ObjectMapper(new YAMLFactory());
        om.registerModule(m);
    }

    @Test
    void validJsonNodeShouldReturnTrue() throws URISyntaxException, IOException {
        var operator = om.readValue(new File(getClass().getClassLoader().getResource("serdeTest.yaml").toURI()), Operator.class);
        JsonNode payload = new ObjectMapper().readTree(new File(getClass().getClassLoader().getResource("validPayload.json").toURI()));

        assertTrue(operator.evaluate(payload.toString()).isMatch());
    }

    @Test
    void nctWithBlacklistTest() throws URISyntaxException, IOException {
        AndOperator operator = (AndOperator) assertDoesNotThrow(() -> om.readValue(new File(getClass().getClassLoader().getResource("nctBlacklist.yaml").toURI()), Operator.class));
        JsonNode payload = new ObjectMapper().readTree(new File(getClass().getClassLoader().getResource("blacklistedPayload.json").toURI()));

        EvaluationResult result = operator.evaluate(payload.toString());

        assertFalse(result.isMatch());
        assertEquals("Not all child-operators matched.", result.getCauseDescription());

        NotContainsOperator nctOperator = null;
        for (Operator notContainsOperator : operator.getOperators()) {
            nctOperator = (NotContainsOperator) notContainsOperator;

            EvaluationResult nctResult = nctOperator.evaluate(payload.toString());

            assertFalse(nctResult.isMatch());
            assertEquals("Actual value did contain expected value.", nctResult.getCauseDescription());
        }
    }

    @Test
    void nctWithBlacklistNoMatchTest() throws URISyntaxException, IOException {
        AndOperator operator = (AndOperator) assertDoesNotThrow(() -> om.readValue(new File(getClass().getClassLoader().getResource("nctBlacklist.yaml").toURI()), Operator.class));
        JsonNode payload = new ObjectMapper().readTree(new File(getClass().getClassLoader().getResource("noMatchedBlacklistedPayload.json").toURI()));

        EvaluationResult result = operator.evaluate(payload.toString());

        assertTrue(result.isMatch());
        assertEquals("", result.getCauseDescription());

        NotContainsOperator nctOperator = null;
        for (Operator notContainsOperator : operator.getOperators()) {
            nctOperator = (NotContainsOperator) notContainsOperator;

            EvaluationResult nctResult = nctOperator.evaluate(payload.toString());

            assertTrue(nctResult.isMatch());
            assertEquals("", nctResult.getCauseDescription());
        }
    }

    @Test
    void tooManyActualValuesShouldReturnError() throws URISyntaxException, IOException {
        Operator operator = assertDoesNotThrow(() -> om.readValue(new File(getClass().getClassLoader().getResource("tooManyActualValuesFilter.yaml").toURI()), Operator.class));
        JsonNode payload = new ObjectMapper().readTree(new File(getClass().getClassLoader().getResource("tooManyActualValuesPayload.json").toURI()));

        EvaluationResult result = operator.evaluate(payload.toString());

        assertFalse(result.isMatch());
        assertEquals("An exception occurred during the evaluation: \nThe evaluation of \"$..type\" contained too many values. Expected 1, got 2.", result.getCauseDescription());
    }
}