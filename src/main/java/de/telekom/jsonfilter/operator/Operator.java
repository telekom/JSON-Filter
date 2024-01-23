package de.telekom.jsonfilter.operator;

public interface Operator {

    OperatorEnum getOperator();

    /**
     * Validates the operator(-chain).
     * @return A ValidationResult that describes the result of the validation.
     */
    ValidationResult validate();

    /**
     * Evaluates a given JSON-payload against the operator(-chain).
     * @param json The JSON-payload that should be evaluated.
     * @return A EvaluationResult that describes the result of the evaluation.
     */
    EvaluationResult evaluate(String json);
}
