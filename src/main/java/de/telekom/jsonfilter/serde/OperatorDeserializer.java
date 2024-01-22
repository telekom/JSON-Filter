package de.telekom.jsonfilter.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.telekom.jsonfilter.exception.OperatorParsingException;
import de.telekom.jsonfilter.operator.Operator;
import de.telekom.jsonfilter.operator.comparison.ComparisonOperator;
import de.telekom.jsonfilter.operator.comparison.ComparisonOperatorEnum;
import de.telekom.jsonfilter.operator.logic.LogicOperator;
import de.telekom.jsonfilter.operator.logic.LogicOperatorEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
public class OperatorDeserializer extends StdDeserializer<Operator> {

    private final int maxComplexity;
    private static final int DEFAULT_MAX_COMPLEXITY = 42;

    public OperatorDeserializer() {
        this(null);
    }

    public OperatorDeserializer(int maxComplexity) {
        super((Class<?>) null);
        this.maxComplexity = maxComplexity;
    }

    public OperatorDeserializer(Class<?> vc) {
        super(vc);
        this.maxComplexity = DEFAULT_MAX_COMPLEXITY;
    }

    public OperatorDeserializer(Class<?> vc, int maxComplexity) {
        super(vc);
        this.maxComplexity = maxComplexity;
    }

    @Override
    public Operator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        var opPair = parseOperator(n, 0);

        if (opPair.getRight() > maxComplexity) {
            throw new OperatorParsingException("Complexity of the filter exceeds the allowed complexity of " + maxComplexity, n);
        } else {
            return opPair.getLeft();
        }
    }

    private Pair<Operator, Integer> parseOperator(JsonNode n, int currentComplexity) throws OperatorParsingException {
        if (currentComplexity > maxComplexity) {
            throw new OperatorParsingException("Complexity of the filter exceeds the allowed complexity of " + maxComplexity, n);
        }
        if (n.fieldNames().hasNext()) {
            var opName = n.fieldNames().next().toUpperCase();
            var opNode = n.get(n.fieldNames().next());

            var op = getComparisonOperator(n, opName, opNode);
            if (op != null) return Pair.of(op, ++currentComplexity);

            var opPair = getLogicOperator(opName, opNode, currentComplexity);
            if (opPair != null && opPair.getLeft() != null) return Pair.of(opPair.getLeft(), opPair.getRight() + 1);

            throw new OperatorParsingException("Operator " + opName + " is no valid operator", n);

        } else {
            throw new OperatorParsingException("Could not find field name", n);
        }
    }

    private Pair<Operator, Integer> getLogicOperator(String opName, JsonNode opNode, int currentComplexity) throws OperatorParsingException {
        try {
            var opAbbr = LogicOperatorEnum.valueOf(opName);
            var opList = new ArrayList<Operator>();
            for (JsonNode child : opNode) {
                var opPair = parseOperator(child, currentComplexity);
                opList.add(opPair.getLeft());

                currentComplexity = opPair.getRight();
            }

            return Pair.of(LogicOperator.instantiate(opAbbr, opList), currentComplexity);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Operator getComparisonOperator(JsonNode n, String opName, JsonNode opNode) throws OperatorParsingException {
        try {
            var opAbbr = ComparisonOperatorEnum.valueOf(opName);
            if (!opNode.has("field")) {
                throw new OperatorParsingException("Mandatory field \"field\" not set", n);
            } else if (!opNode.has("value")) {
                throw new OperatorParsingException("Mandatory field \"value\" not set", n);
            }

            var valueNode = opNode.get("value");
            Object value = getValueFromNode(valueNode);

            var op = ComparisonOperator.instantiate(opAbbr, opNode.get("field").textValue(), value);
            assert op != null;
            var vResult = op.validate();
            if (vResult.isValid()) {
                return op;
            } else {
                throw new OperatorParsingException("Operator of type " + vResult.getOperator().getValue() + " was created, but is not valid: " + vResult.getValidationError(), opNode);
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Object getValueFromNode(JsonNode valueNode) throws OperatorParsingException {
        switch (valueNode.getNodeType()) {
            case STRING:
                return valueNode.textValue();
            case NUMBER:
                return valueNode.numberValue();
            case BOOLEAN:
                return valueNode.booleanValue();
            case ARRAY:
                List<Optional<Object>> optionals = StreamSupport.stream(valueNode.spliterator(), false).map(this::uncheckedGetValueFromNode).toList();
                if (optionals.contains(Optional.empty())) {
                    throw new OperatorParsingException("Invalid type in array", valueNode);
                } else {
                    return optionals.stream().filter(Optional::isPresent).map(Optional::get).toList();
                }
            default:
                throw new OperatorParsingException("Cannot use value of type " + valueNode.getNodeType(),
                        valueNode);
        }
    }

    private Optional<Object> uncheckedGetValueFromNode(JsonNode valueNode) {
        try {
            return Optional.of(getValueFromNode(valueNode));
        } catch (OperatorParsingException ex) {
            return Optional.empty();
        }
    }
}
