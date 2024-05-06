// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator.logic;

import de.telekom.jsonfilter.operator.Operator;
import de.telekom.jsonfilter.operator.ValidationResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class LogicOperator implements Operator {
    @Getter
    final LogicOperatorEnum operator;

    @Getter
    List<Operator> operators = new ArrayList<>();

    protected LogicOperator(LogicOperatorEnum operator) {
        this.operator = operator;
    }

    public ValidationResult validate() {
        if (operators.stream().map(Operator::validate).allMatch(ValidationResult::isValid)) {
            return ValidationResult.valid();
        } else {
            return ValidationResult.withError("One or more children are not valid", getOperator());
        }
    }

    public static Operator instantiate(LogicOperatorEnum operator, List<Operator> operatorList) {
        return switch (operator) {
            case AND -> new AndOperator(operatorList);
            case OR -> new OrOperator(operatorList);
        };
    }
}
