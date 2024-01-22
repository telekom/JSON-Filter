package de.telekom.jsonfilter.operator;

public interface Operator {

    OperatorEnum getOperator();

    /**
     * Validates the operator and all of its children.
     *
     * @return true when the operator and all of its children are valid,
     * false when the operator or one of its children is not valid.
     */
    ValidationResult validate();

    /**
     * Evaluates the given json against the operator(-chain).
     *
     * @param json Given payload
     * @return true when the json is valid against the operator(-chain),
     * false when is is not.
     */
    EvaluationResult evaluate(String json);
}
