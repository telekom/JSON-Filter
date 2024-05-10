// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator.comparison;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.path.PathCompiler;
import de.telekom.jsonfilter.exception.NoSingleValueException;
import de.telekom.jsonfilter.operator.EvaluationResult;
import de.telekom.jsonfilter.operator.Operator;
import de.telekom.jsonfilter.operator.ValidationResult;
import lombok.Getter;
import net.minidev.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public abstract class ComparisonOperator<T> implements Operator {
    @Getter
    final ComparisonOperatorEnum operator;
    T expectedValue;
    String jsonPath;


    protected ComparisonOperator(ComparisonOperatorEnum operator) {
        this.operator = operator;
    }

    public static <T> Operator instantiate(ComparisonOperatorEnum operator, String jsonPath, T expectedValue) {
        return switch (operator) {
            case EQ -> new EqualsOperator<>(jsonPath, expectedValue);
            case NE -> new NotEqualsOperator<>(jsonPath, expectedValue);
            case RX -> new RegexOperator<>(jsonPath, expectedValue);
            case LT -> new LessThanOperator<>(jsonPath, expectedValue);
            case LE -> new LessEqualOperator<>(jsonPath, expectedValue);
            case GT -> new GreaterThanOperator<>(jsonPath, expectedValue);
            case GE -> new GreaterEqualOperator<>(jsonPath, expectedValue);
            case IN -> new InOperator<>(jsonPath, (List<?>) expectedValue);
            case CT -> new ContainsOperator<>(jsonPath, expectedValue);
            case NCT -> new NotContainsOperator<>(jsonPath, expectedValue);
        };
    }

    @Override
    public EvaluationResult evaluate(String json) {
        return compare(json, jsonPath, expectedValue);
    }

    abstract EvaluationResult compare(String json, String jsonPath, T expectedValue);

    protected Comparable<T> getActualValue(String json, String jsonPath) throws NoSingleValueException {
        var valueList = getActualValues(json, jsonPath);
        if (valueList.size() == 1) {
            return valueList.getFirst();
        } else {
            throw new NoSingleValueException(jsonPath, valueList.size());
        }
    }

    protected List<Comparable<T>> getActualValues(String json, String jsonPath) {
        List<Comparable<T>> valueList = new ArrayList<>();

        Object readResult = JsonPath.read(json, jsonPath);
        if (readResult instanceof Comparable) {
            valueList.add((Comparable<T>) readResult);
        } else if (readResult instanceof JSONArray rrArray) {
            valueList = rrArray.stream().flatMap(v -> {
                if (v instanceof JSONArray ja) {
                    return ja.stream();
                } else {
                    return Stream.of(v);
                }
            }).filter(Comparable.class::isInstance).map(rr -> (Comparable<T>) rr).collect(Collectors.toList());
        }

        return valueList;
    }

    public ValidationResult validate() {
        try {
            PathCompiler.compile(jsonPath);
            return ValidationResult.valid();
        } catch (InvalidPathException ipe) {
            return ValidationResult.withError(ipe.getLocalizedMessage(), getOperator());
        }
    }
}
