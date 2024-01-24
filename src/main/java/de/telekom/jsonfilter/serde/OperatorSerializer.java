// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.telekom.jsonfilter.operator.Operator;
import de.telekom.jsonfilter.operator.comparison.ComparisonOperator;
import de.telekom.jsonfilter.operator.comparison.ComparisonOperatorEnum;
import de.telekom.jsonfilter.operator.logic.LogicOperator;
import de.telekom.jsonfilter.operator.logic.LogicOperatorEnum;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class OperatorSerializer extends StdSerializer<Operator> {

    public OperatorSerializer() {
        this(null);
    }

    public OperatorSerializer(Class<Operator> t) {
        super(t);
    }

    @Override
    public void serialize(Operator value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (Arrays.stream(LogicOperatorEnum.values()).anyMatch(e -> e.equals(value.getOperator()))) {
            List<Operator> childOps = ((LogicOperator) value).getOperators();
            if (!childOps.isEmpty()) {
                gen.writeStartObject();
                gen.writeArrayFieldStart(value.getOperator().toString().toLowerCase());
                for (Operator op : childOps) {
                    serialize(op, gen, provider);
                }
                gen.writeEndArray();
                gen.writeEndObject();
            }
        }

        if (Arrays.stream(ComparisonOperatorEnum.values()).anyMatch(e -> e.equals(value.getOperator()))) {
            String field = ((ComparisonOperator<?>) value).getJsonPath();
            Object expectedValue = ((ComparisonOperator<?>) value).getExpectedValue();
            gen.writeStartObject();
            gen.writeObjectFieldStart(value.getOperator().toString().toLowerCase());
            gen.writeStringField("field", field);
            gen.writeObjectField("value", expectedValue);
            gen.writeEndObject();
            gen.writeEndObject();
        }
    }
}
