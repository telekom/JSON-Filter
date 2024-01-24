// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.operator.logic;

import de.telekom.jsonfilter.operator.EvaluationResult;
import de.telekom.jsonfilter.operator.Operator;

import java.util.List;

public class AndOperator extends LogicOperator {

    public AndOperator(List<Operator> operatorList) {
        super(LogicOperatorEnum.AND);
        this.operators = operatorList;
    }

    @Override
    public EvaluationResult evaluate(String json) {
        return EvaluationResult.fromResultList(this, operators.stream().map(op -> op.evaluate(json)).toList());
    }
}
