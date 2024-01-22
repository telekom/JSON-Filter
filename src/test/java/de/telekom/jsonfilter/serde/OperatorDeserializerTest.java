package de.telekom.jsonfilter.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.telekom.jsonfilter.exception.OperatorParsingException;
import de.telekom.jsonfilter.operator.Operator;
import de.telekom.jsonfilter.operator.comparison.ComparisonOperator;
import de.telekom.jsonfilter.operator.logic.AndOperator;
import de.telekom.jsonfilter.operator.logic.LogicOperator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class OperatorDeserializerTest {

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
    void shouldDeserializeAllOperators() throws IOException, URISyntaxException {

        var operator = om.readValue(new File(getClass().getClassLoader().getResource("serdeTest.yaml").toURI()), Operator.class);

        assertEquals("and", operator.getOperator().toString().toLowerCase());
        var andOperators = ((LogicOperator) operator).getOperators();

        assertEquals("ct", andOperators.get(0).getOperator().toString().toLowerCase());
        Assertions.assertEquals("$.contains", ((ComparisonOperator<?>) andOperators.get(0)).getJsonPath());
        assertEquals("CONTAINS_VALUE", ((ComparisonOperator<?>) andOperators.get(0)).getExpectedValue());

        assertEquals("eq", andOperators.get(1).getOperator().toString().toLowerCase());
        assertEquals("$.equal", ((ComparisonOperator<?>) andOperators.get(1)).getJsonPath());
        assertEquals("EQUAL_VALUE", ((ComparisonOperator<?>) andOperators.get(1)).getExpectedValue());

        assertEquals("ge", andOperators.get(2).getOperator().toString().toLowerCase());
        assertEquals("$.greater-equal", ((ComparisonOperator<?>) andOperators.get(2)).getJsonPath());
        assertEquals("GREATER_EQUAL_VALUE", ((ComparisonOperator<?>) andOperators.get(2)).getExpectedValue());

        assertEquals("gt", andOperators.get(3).getOperator().toString().toLowerCase());
        assertEquals("$.greater", ((ComparisonOperator<?>) andOperators.get(3)).getJsonPath());
        assertEquals("GREATER_VALUE", ((ComparisonOperator<?>) andOperators.get(3)).getExpectedValue());

        assertEquals("in", andOperators.get(4).getOperator().toString().toLowerCase());
        assertEquals("$.in", ((ComparisonOperator<?>) andOperators.get(4)).getJsonPath());
        assertEquals(List.of("IN_VALUE_0", "IN_VALUE_1", "IN_VALUE_2"), ((ComparisonOperator<?>) andOperators.get(4)).getExpectedValue());

        assertEquals("le", andOperators.get(5).getOperator().toString().toLowerCase());
        assertEquals("$.less-equal", ((ComparisonOperator<?>) andOperators.get(5)).getJsonPath());
        assertEquals("LESS_EQUAL_VALUE", ((ComparisonOperator<?>) andOperators.get(5)).getExpectedValue());

        assertEquals("lt", andOperators.get(6).getOperator().toString().toLowerCase());
        assertEquals("$.less", ((ComparisonOperator<?>) andOperators.get(6)).getJsonPath());
        assertEquals("LESS_VALUE", ((ComparisonOperator<?>) andOperators.get(6)).getExpectedValue());

        assertEquals("ne", andOperators.get(7).getOperator().toString().toLowerCase());
        assertEquals("$.not-equal", ((ComparisonOperator<?>) andOperators.get(7)).getJsonPath());
        assertEquals("NOT_EQUAL_VALUE", ((ComparisonOperator<?>) andOperators.get(7)).getExpectedValue());

        assertEquals("rx", andOperators.get(8).getOperator().toString().toLowerCase());
        assertEquals("$.regex", ((ComparisonOperator<?>) andOperators.get(8)).getJsonPath());
        assertEquals("REGEX_VALUE", ((ComparisonOperator<?>) andOperators.get(8)).getExpectedValue());

        assertEquals("nct", andOperators.get(9).getOperator().toString().toLowerCase());
        assertEquals("$.nct", ((ComparisonOperator<?>) andOperators.get(9)).getJsonPath());
        assertEquals("NOT_CONTAINS_VALUE", ((ComparisonOperator<?>) andOperators.get(9)).getExpectedValue());
    }

    @Test
    void validTypesShouldBeMappedToNodes() throws URISyntaxException, IOException {
        AndOperator op = (AndOperator) om.readValue(new File(getClass().getClassLoader().getResource("validTypeTest.yaml").toURI()), Operator.class);
        assertEquals("foo", ((ComparisonOperator<?>) op.getOperators().get(0)).getExpectedValue());
        assertEquals(200, ((ComparisonOperator<?>) op.getOperators().get(1)).getExpectedValue());
        assertEquals(true, ((ComparisonOperator<?>) op.getOperators().get(2)).getExpectedValue());
        assertEquals(List.of("foo", 200, true), ((ComparisonOperator<?>) op.getOperators().get(3)).getExpectedValue());
    }

    @Test
    void missingFieldShouldThrow() {
        assertThrows(OperatorParsingException.class, () -> om.readValue(new File(getClass().getClassLoader().getResource("missingFieldTest.yaml").toURI()), Operator.class));
    }

    @Test
    void missingValueShouldThrow() {
        assertThrows(OperatorParsingException.class, () -> om.readValue(new File(getClass().getClassLoader().getResource("missingValueTest.yaml").toURI()), Operator.class));
    }

    @Test
    void invalidOperatorShouldThrow() {
        assertThrows(OperatorParsingException.class, () -> om.readValue(new File(getClass().getClassLoader().getResource("invalidOperatorTest.yaml").toURI()), Operator.class));
    }

    @Test
    void invalidRegexShouldThrow() {
        assertThrows(OperatorParsingException.class, () -> om.readValue(new File(getClass().getClassLoader().getResource("invalidRegexTest.yaml").toURI()), Operator.class));
    }

    @Test
    void invalidTypeShouldThrow() {
        assertThrows(OperatorParsingException.class, () -> om.readValue(new File(getClass().getClassLoader().getResource("invalidTypeTest.yaml").toURI()), Operator.class));
    }

    @Test
    void invalidTypeInArrayShouldThrow() {
        assertThrows(OperatorParsingException.class, () -> om.readValue(new File(getClass().getClassLoader().getResource("invalidTypeInArrayTest.yaml").toURI()), Operator.class));
    }

    @Test
    void tooComplexFilterShouldThrow() {
        assertThrows(OperatorParsingException.class, () -> om.readValue(new File(getClass().getClassLoader().getResource("tooComplexFilterTest.yaml").toURI()), Operator.class));
    }

    @Test
    void notTooComplexFilterShouldNotThrow() {
        assertDoesNotThrow(() -> om.readValue(new File(getClass().getClassLoader().getResource("notTooComplexFilterTest.yaml").toURI()), Operator.class));
    }

    @Test
    void dummyTest() {
        assertDoesNotThrow(() -> om.readValue(new File(getClass().getClassLoader().getResource("dummyTest.yaml").toURI()), Operator.class));
    }

    @Test
    void nctTest() {
        assertDoesNotThrow(() -> om.readValue(new File(getClass().getClassLoader().getResource("nctBlacklist.yaml").toURI()), Operator.class));
    }
}