package de.telekom.jsonfilter.operator.logic;

import de.telekom.jsonfilter.operator.EvaluationResult;
import de.telekom.jsonfilter.operator.Operator;

import java.util.List;
import java.util.stream.Collectors;

public class OrOperator extends LogicOperator {

    public OrOperator(List<Operator> operatorList) {
        super(LogicOperatorEnum.OR);
        this.operators = operatorList;
    }

    @Override
    public EvaluationResult evaluate(String json) {
        return EvaluationResult.fromResultList(this, operators.stream().map(op -> op.evaluate(json)).collect(Collectors.toList()));
    }
}
